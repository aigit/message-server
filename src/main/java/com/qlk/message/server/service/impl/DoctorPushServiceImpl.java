package com.qlk.message.server.service.impl;

import io.netty.channel.ChannelHandler;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.baymax.common.message.bo.GroupPushDeviceParamsBO;
import com.qlk.baymax.common.redis.RedisUtil;
import com.qlk.baymax.common.utils.biz.AppJumpProtocol;
import com.qlk.baymax.common.utils.lang.ConfigUtil;
import com.qlk.baymax.common.utils.net.RpcHttpUtils;
import com.qlk.baymax.common.utils.thread.ThreadPoolUtil;
import com.qlk.message.server.bean.PushParams;
import com.qlk.message.server.controller.PushController;
import com.qlk.message.server.exception.ExceptionCodes;
import com.qlk.message.server.service.IDeviceTokenService;
import com.qlk.message.server.service.IChannelPushService;
import com.qlk.message.server.service.IUserPushService;
import com.qlk.message.server.service.handler.ChannelPushHandler;
import com.qlk.message.server.utils.AndroidPushUtil;
import com.qlk.message.server.utils.JacksonMapper;
import com.qlk.message.server.utils.PropertyValueConstants;
import com.qlk.message.server.utils.RedisSetPageUtil;
import com.qlk.message.server.vo.GroupMessageVO;
import com.qlk.message.server.vo.GroupResendPushVO;
import com.qlk.message.server.vo.PushJsonVo;
import com.qlk.message.server.vo.UserDeviceVo;
import com.tencent.xinge.bean.Browser;
import com.tencent.xinge.bean.ClickAction;
import com.tencent.xinge.bean.Message;
import com.tencent.xinge.bean.MessageAndroid;

/**
 * @author Ldl
 * @date 2018/11/13 14:50
 * @since 1.0.0
 */
@Service("doctorService")
public class DoctorPushServiceImpl implements IUserPushService {

    private static Logger LOGGER = CommonLoggerFactory.getLogger(PushController.class);

    /**
     * 可push内容最小长度
     */
    private static final int PUSH_CONTENT_MIN_BYTE_LENGTH = 30;

    /**
     * 汉字所占字节数
     */
    private static final int ZH_CN_LENGTH_UNIT = 3;

    /**
     * 分组发送push每页取出的用户数量
     */
    private static final int NOTICE_GROUP_SEND_USERS_PAGESIZE = 99;

    /**
     * 默认声音类型
     */
    private static final String SOUND_TYPE_DEFAULT = "default";

    @Autowired
    private IDeviceTokenService deviceTokenService;

    @Autowired
    private ChannelPushHandler channelPushHandler;

    @Override
    public void saveTaskPush(String taskId) throws BusinessException {

    }

    @Override
    public void sendSinglePushMessage(PushParams pushParams) throws BusinessException {
        // 根据用户id和用户type，来查询调用android或者ios的发送服务
        UserDeviceVo device = deviceTokenService.findUserDevice(pushParams.getUserId(), pushParams.getUserType());
        if (device == null) {
            LOGGER.warn("推送消息设备没有找到，push Params {} ", pushParams);
            throw new BusinessException(ExceptionCodes.PUSH_MESSAGE_DEVICE_NULL);
        }
        Integer status = device.getStatus();
        if (status != null && status != 0) {
            LOGGER.info("推送消息设备发现用户已经禁止接受推送， push Params {} ", pushParams);
            throw new BusinessException(ExceptionCodes.PUSH_MESSAGE_DEVICE_NORECEIVE);
        }
        Integer platType = device.getPlatType();

        // 获取声音信息
        String sound = device.getSound();
        // 构造发送push的json
        String iosMessage = this.handleMessage(pushParams.getMessage(), sound, pushParams.getLinkUrl());
        String token = device.getToken();
        pushParams.setToken(token);
        pushParams.setPushMessage(iosMessage);
        pushParams.setPlatType(platType);
        channelPushHandler.sendSingleDevice(pushParams);
    }

    @Override
    public void pushToAllUser(String message, String linkUrl) throws BusinessException {
        // 处理push消息
        String pushMessage = null;
        try {
            pushMessage = this.handleMessage(message, null, linkUrl);
        } catch (BusinessException e) {
            throw new BusinessException(ExceptionCodes.PUSH_MESSAGE_FORMAT_ERROR);
        }
        PushParams pushParams = new PushParams(PropertyValueConstants.USER_TYPE_DOCTOR, null, message, linkUrl, null, pushMessage);
        /*
         先发送android
         */
        channelPushHandler.getChannelPushService(PropertyValueConstants.PLATFORM_ANDRIOD).sendAllDevice(pushParams);
        /**
         * 再发送ios
         */
        channelPushHandler.getChannelPushService(PropertyValueConstants.PLATFORM_IOS).sendAllDevice(pushParams);
    }

    @Override
    public void batchPushByGroup(String message, String linkUrl, String recipientCacheTopushKey) throws BusinessException {
        String userType = PropertyValueConstants.USER_TYPE_DOCTOR;
        String repushval = null;
        LOGGER.info("exist:{},zcard:{}", RedisUtil.keyOps().existsKey(recipientCacheTopushKey), RedisUtil.zsetOps()
                                                                                                         .zcard(recipientCacheTopushKey) == 0L);
        if (!RedisUtil.keyOps().existsKey(recipientCacheTopushKey) || RedisUtil.zsetOps().zcard(recipientCacheTopushKey) == 0L) {
            LOGGER.error("batchPushByGroup 待Push的用户列表为空,recipientCacheTopushKey:{},userType:{},pushMessage:{}", recipientCacheTopushKey,
                    userType, message);
            return;
        }
        PushParams pushParams = new PushParams();
        pushParams.setMessage(message);
        pushParams.setLinkUrl(linkUrl);
        pushParams.setRecipientCacheTopushKey(recipientCacheTopushKey);
        String soundOpenMessage = this.handleMessage(message, SOUND_TYPE_DEFAULT, linkUrl);
        String soundClosedMessage = this.handleMessage(message, null, linkUrl);
        pushParams.setSoundOpenMessage(soundOpenMessage);
        pushParams.setSoundClosedMessage(soundClosedMessage);

        GroupResendPushVO groupResendPushVo = new GroupResendPushVO(userType, message, linkUrl, recipientCacheTopushKey);
        repushval = JSON.toJSONString(groupResendPushVo);
        LOGGER.info("batchPushByGroup,分组push参数:{}", repushval);
        /*
         *放入重发缓存
         */
        RedisUtil.setOps().sadd(PropertyValueConstants.GROUP_PUSH_RESEND_KEY, repushval);
        Set<String> userIdSet = null;
        RedisSetPageUtil redisPageUtil = new RedisSetPageUtil(recipientCacheTopushKey, NOTICE_GROUP_SEND_USERS_PAGESIZE);
        int pageCount = redisPageUtil.getPageCount();
        LOGGER.info("pageCount：{}", pageCount);
        for (int i = 0; i < pageCount; i++) {
            if (i == pageCount - 1) {
                userIdSet = RedisUtil.zsetOps().zrange(recipientCacheTopushKey, 0L, RedisUtil.zsetOps().zcard(recipientCacheTopushKey) - 1L);
            } else {
                userIdSet = RedisUtil.zsetOps().zrange(recipientCacheTopushKey, 0L, NOTICE_GROUP_SEND_USERS_PAGESIZE);
            }
            this.pushInBatches(userIdSet, pushParams);
        }
        if (RedisUtil.zsetOps().zcard(recipientCacheTopushKey).equals(0L)) {
            RedisUtil.setOps().srem(PropertyValueConstants.GROUP_PUSH_RESEND_KEY, repushval);
        }
    }

    /**
     * 分页push
     * PushServiceImpl.pushInBatches()
     * @param userIdSet               某一页用户Ids
     * @param userType                用户类型
     * @param pushMessage             push消息内容
     * @param linkUrl                 内容中链接地址
     * @param recipientCacheTopushKey 待发送用户集合缓存key
     * @Author Ldl
     * @Date 2017年12月6日
     * @since 1.0.0
     */
    private void pushInBatches(Set<String> userIdSet, PushParams pushParams) throws BusinessException {
        String sendUrl = null;
        List<String> remUserIdList = null;
        String recipientCacheTopushKey = pushParams.getRecipientCacheTopushKey();
        String message = pushParams.getMessage();
        /*
         * 查询用户当前使用设备信息,并根据不同设备进行分别分批发送
         */
        List<UserDeviceVo> android0DeviceList = deviceTokenService.findToPushUsers(userIdSet, PropertyValueConstants.DEVICE_SOUND_OPEN,
                PropertyValueConstants.PLATFORM_ANDRIOD,
                PropertyValueConstants.USER_TYPE_DOCTOR);// 安卓--s:0
        if (android0DeviceList != null && android0DeviceList.size() > 0) {
            pushParams.setMessage(message);
            pushParams.setSoundType(PropertyValueConstants.DEVICE_SOUND_OPEN);
            this.pushBatchAndroidDevice(android0DeviceList, recipientCacheTopushKey, pushParams);
        }

        List<UserDeviceVo> android1DeviceList = deviceTokenService.findToPushUsers(userIdSet, PropertyValueConstants.DEVICE_SOUND_OFF,
                PropertyValueConstants.PLATFORM_ANDRIOD,
                PropertyValueConstants.USER_TYPE_DOCTOR);// 安卓--s:1
        if (android1DeviceList != null && android1DeviceList.size() > 0) {
            pushParams.setMessage(message);
            pushParams.setSoundType(PropertyValueConstants.DEVICE_SOUND_OFF);
            this.pushBatchAndroidDevice(android1DeviceList, recipientCacheTopushKey, pushParams);
        }

        List<UserDeviceVo> ios0DeviceList = deviceTokenService.findToPushUsers(userIdSet, PropertyValueConstants.DEVICE_SOUND_OPEN,
                PropertyValueConstants.PLATFORM_IOS, PropertyValueConstants.USER_TYPE_DOCTOR);// ios--s:0

        if (ios0DeviceList != null && ios0DeviceList.size() > 0) {
            pushParams.setPushMessage(pushParams.getSoundOpenMessage());
            try {
                this.pushBatchIosDevice(pushParams, ios0DeviceList, recipientCacheTopushKey);
            }catch (BusinessException e){
                LOGGER.error("pushBatchIosDevice 批量发送ios push异常,ios0DeviceList:{},pushParams:{}",ios0DeviceList.size(),pushParams);
            }
        }

        List<UserDeviceVo> ios1DeviceList = deviceTokenService.findToPushUsers(userIdSet, PropertyValueConstants.DEVICE_SOUND_OFF,
                PropertyValueConstants.PLATFORM_IOS, PropertyValueConstants.USER_TYPE_DOCTOR);// ios--s:0
        if (ios1DeviceList != null && ios1DeviceList.size() > 0) {
            pushParams.setPushMessage(pushParams.getSoundClosedMessage());
            try {
                this.pushBatchIosDevice(pushParams, ios1DeviceList, recipientCacheTopushKey);
            }catch (BusinessException e){
                LOGGER.error("pushBatchIosDevice 批量发送ios push异常,ios1DeviceList:{},pushParams:{}",ios1DeviceList.size(),pushParams);
            }
        }
    }

    private void pushBatchAndroidDevice(List<UserDeviceVo> androidDeviceList, String recipientCacheTopushKey, PushParams pushParams) {
        List<String> remUserIdList = null;
        try {
            ArrayList<String> groupPushDeviceList = new ArrayList<>();
            remUserIdList = new ArrayList<>();
            for (UserDeviceVo userDeviceVo : androidDeviceList) {
                Integer status = userDeviceVo.getStatus();
                if (status != null && status == 0) {
                    groupPushDeviceList.add(userDeviceVo.getToken());
                    remUserIdList.add(String.valueOf(userDeviceVo.getUserId()));
                }
            }
            // 使用post请求发送请求
            LOGGER.info("批量push发送接口，android 用户数量:{}", androidDeviceList.size());
            int batchPushResult = channelPushHandler.getChannelPushService(PropertyValueConstants.PLATFORM_ANDRIOD)
                                                    .batchSendDevice(pushParams, groupPushDeviceList);
        } catch (BusinessException e) {
            LOGGER.error("pushBatchAndroidDevice 请求push发送接口，pushParams:{}", pushParams);
        }
        if(remUserIdList!=null && remUserIdList.size()>0){
            /*
            从待发送缓存中删除该批次已发送的用户
            */
            RedisUtil.zsetOps().zrem(recipientCacheTopushKey, remUserIdList.stream().toArray(String[]::new));
        }
    }

    private void pushBatchIosDevice(PushParams pushParams, List<UserDeviceVo> iosDeviceList, String recipientCacheTopushKey) throws BusinessException {
        List<String> remUserIdList = new ArrayList<>();
        List<GroupPushDeviceParamsBO> groupPushDeviceList = new ArrayList<>();
        for (UserDeviceVo userDeviceVo : iosDeviceList) {
            Integer status = userDeviceVo.getStatus();
            if (status != null && status == 0) {
                GroupPushDeviceParamsBO groupPushDevice = new GroupPushDeviceParamsBO();
                groupPushDevice.setUserId(String.valueOf(userDeviceVo.getUserId()));
                groupPushDevice.setToken(userDeviceVo.getToken());
                groupPushDeviceList.add(groupPushDevice);
                remUserIdList.add(String.valueOf(userDeviceVo.getUserId()));
            }
        }
        String groupDeviceParams = JSON.toJSONString(groupPushDeviceList);
        pushParams.setGroupPushDeviceParams(groupDeviceParams);
        LOGGER.info("请求push发送接口，发送用户数量:{}", groupPushDeviceList.size());
        channelPushHandler.getChannelPushService(PropertyValueConstants.PLATFORM_IOS).batchSendDevice(pushParams, null);
        /*
        从待发送缓存中删除该批次已发送的用户
         */
        RedisUtil.zsetOps().zrem(recipientCacheTopushKey, remUserIdList.stream().toArray(String[]::new));
    }


    /**
     * PushServiceImpl.handleMessage()
     * @param userType
     * @param pushMessage
     * @param sound
     * @param linkUrl
     * @return
     * @throws BusinessException
     * @Author chenlin
     * @Date 2016年4月5日
     * @since 1.0.0
     */
    private String handleMessage(String message, String sound, String linkUrl) throws BusinessException {
        int remainLength = maxRemainLength(sound, linkUrl);
        if (remainLength <= PUSH_CONTENT_MIN_BYTE_LENGTH) {
            LOGGER.info("推送消息超过256个字节， message {} ", message);
            throw new BusinessException(ExceptionCodes.PUSH_MESSAGE_TOO_LONG);
        }
        if (message != null && message.length() > remainLength / ZH_CN_LENGTH_UNIT) {
            message = message.substring(0, remainLength / 3 - 1) + "...";
        }
        String sendMessage = null;
        if (sound == null) {
            sendMessage = this.createAPNSDefulatJson(message, linkUrl);
        } else {
            sendMessage = this.createAPNSJSON(message, sound, linkUrl);
        }
        if (sendMessage == null || !JacksonMapper.isJSONValid(sendMessage)) {
            LOGGER.info("推送消息为空获取没有已{}开头， userType {}  sendMessage {} ", PropertyValueConstants.USER_TYPE_DOCTOR, sendMessage);
            throw new BusinessException(ExceptionCodes.PUSH_MESSAGE_NULL);
        }
        return sendMessage;
    }

    /**
     * 根据消息,声音,链接地址构造发送push的内容
     * @param message 消息内容
     * @param sound   声音设置
     * @param linkUrl 跳转协议
     * @return
     */
    private String createAPNSJSON(String message, String sound, String linkUrl) {
        return PushJsonVo.defaultMessage(message, sound, linkUrl);
    }

    /**
     * 根据消息,链接地址构造发送push的内容
     * @param message 消息内容
     * @param linkUrl 跳转协议
     * @return
     */
    private String createAPNSDefulatJson(String message, String linkUrl) {
        return PushJsonVo.defaultMessage(message, "default", linkUrl);
    }

    /**
     * 获取可填充message 字节长度
     * @param sound   声音配置
     * @param linkUrl 跳转协议
     * @return
     */
    private static int maxRemainLength(String sound, String linkUrl) {
        sound = sound == null ? "default" : sound;
        int soundLength = PushJsonVo.defaultMessage("", sound, linkUrl).getBytes().length;
        return soundLength >= 256 ? 0 : 256 - soundLength;
    }


    @Override
    public void reSendPushByGroup() {
        Set<String> repushPushKeys = RedisUtil.setOps().smembers(PropertyValueConstants.GROUP_PUSH_RESEND_KEY);// 查询出缓存中待补发的数据
        PushParams pushParams = new PushParams();
        for (String repushval : repushPushKeys) {
            LOGGER.info("待补发push str：{}", repushPushKeys);
            GroupResendPushVO groupResendPushVo = JSON.parseObject(repushval, GroupResendPushVO.class);
            String recipientCacheTopushKey = groupResendPushVo.getRecipientCacheTopushKey();
            String message = groupResendPushVo.getPushMessage();
            String linkUrl = groupResendPushVo.getLinkUrl();
            pushParams.setMessage(message);
            String soundOpenMessage = null;
            try {
                soundOpenMessage = this.handleMessage(message, SOUND_TYPE_DEFAULT, linkUrl);
            } catch (BusinessException e) {
                LOGGER.warn("格式化消息内容异常,message:{},linkUrl:{},soundtype:{},e:{}", message, linkUrl, SOUND_TYPE_DEFAULT, e);
            }
            String soundClosedMessage = null;
            try {
                soundClosedMessage = this.handleMessage(message, null, linkUrl);
            } catch (BusinessException e) {
                LOGGER.warn("格式化消息内容异常,message:{},linkUrl:{},soundtype:{},e:{}", message, linkUrl, null, e);
            }
            pushParams.setSoundOpenMessage(soundOpenMessage);
            pushParams.setSoundClosedMessage(soundClosedMessage);
            pushParams.setLinkUrl(linkUrl);
            pushParams.setRecipientCacheTopushKey(recipientCacheTopushKey);
            /*
             * 只补发当前时间之前的push
             */
            if (!RedisUtil.keyOps().existsKey(recipientCacheTopushKey) || RedisUtil.zsetOps().zcard(recipientCacheTopushKey).equals(0L)) {
                RedisUtil.setOps().srem(PropertyValueConstants.GROUP_PUSH_RESEND_KEY, repushval);
                LOGGER.info("已全部投递，无需补发:{}", repushval);
                continue;
            }

            /*
             * 分页push
             */
            Set<String> userIdSet = null;
            RedisSetPageUtil redisPageUtil = new RedisSetPageUtil(recipientCacheTopushKey, NOTICE_GROUP_SEND_USERS_PAGESIZE);
            int pageCount = redisPageUtil.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                if (i == pageCount - 1) {
                    userIdSet = RedisUtil.zsetOps().zrange(recipientCacheTopushKey, 0L, (RedisUtil.zsetOps().zcard(recipientCacheTopushKey) - 1L));
                    try {
                        this.pushInBatches(userIdSet, pushParams);
                    } catch (BusinessException e) {
                        LOGGER.warn("补发push异常,pushMessage:{},linkUrl:{},recipientCacheTopushKey:{}", message, linkUrl, recipientCacheTopushKey);
                    }
                } else {
                    userIdSet = RedisUtil.zsetOps().zrange(recipientCacheTopushKey, 0L, NOTICE_GROUP_SEND_USERS_PAGESIZE);
                    try {
                        this.pushInBatches(userIdSet, pushParams);
                    } catch (BusinessException e) {
                        LOGGER.warn("补发push异常,pushMessage:{},linkUrl:{},recipientCacheTopushKey:{}", message, linkUrl, recipientCacheTopushKey);
                    }
                }
            }
            /*
             * 检查如果缓存中待Push的缓存数据已经全部发完则删除当前push对应的key
             */
            if (!RedisUtil.keyOps().existsKey(recipientCacheTopushKey) || RedisUtil.zsetOps().zcard(recipientCacheTopushKey).equals(0L)) {
                RedisUtil.setOps().srem(PropertyValueConstants.GROUP_PUSH_RESEND_KEY, repushval);
            }
        }
    }

    @Override
    public void sendBatchMessage(String pushBatchId) throws BusinessException {

    }


    @Override
    public GroupMessageVO getGroupMessageInCache() {
        GroupMessageVO groupMessageVo = new GroupMessageVO();
        groupMessageVo.setGroupResendNoticeSet(RedisUtil.setOps().smembers(PropertyValueConstants.GROUP_NOTICE_RESEND_KEY));
        groupMessageVo.setGroupNoticeSize(groupMessageVo.getGroupResendNoticeSet().size());
        groupMessageVo.setGroupResendPushSet(RedisUtil.setOps().smembers(PropertyValueConstants.GROUP_PUSH_RESEND_KEY));
        groupMessageVo.setGroupPushSize(groupMessageVo.getGroupResendPushSet().size());
        return groupMessageVo;
    }
}
