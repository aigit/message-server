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
     * ???push??????????????????
     */
    private static final int PUSH_CONTENT_MIN_BYTE_LENGTH = 30;

    /**
     * ?????????????????????
     */
    private static final int ZH_CN_LENGTH_UNIT = 3;

    /**
     * ????????????push???????????????????????????
     */
    private static final int NOTICE_GROUP_SEND_USERS_PAGESIZE = 99;

    /**
     * ??????????????????
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
        // ????????????id?????????type??????????????????android??????ios???????????????
        UserDeviceVo device = deviceTokenService.findUserDevice(pushParams.getUserId(), pushParams.getUserType());
        if (device == null) {
            LOGGER.warn("?????????????????????????????????push Params {} ", pushParams);
            throw new BusinessException(ExceptionCodes.PUSH_MESSAGE_DEVICE_NULL);
        }
        Integer status = device.getStatus();
        if (status != null && status != 0) {
            LOGGER.info("????????????????????????????????????????????????????????? push Params {} ", pushParams);
            throw new BusinessException(ExceptionCodes.PUSH_MESSAGE_DEVICE_NORECEIVE);
        }
        Integer platType = device.getPlatType();

        // ??????????????????
        String sound = device.getSound();
        // ????????????push???json
        String iosMessage = this.handleMessage(pushParams.getMessage(), sound, pushParams.getLinkUrl());
        String token = device.getToken();
        pushParams.setToken(token);
        pushParams.setPushMessage(iosMessage);
        pushParams.setPlatType(platType);
        channelPushHandler.sendSingleDevice(pushParams);
    }

    @Override
    public void pushToAllUser(String message, String linkUrl) throws BusinessException {
        // ??????push??????
        String pushMessage = null;
        try {
            pushMessage = this.handleMessage(message, null, linkUrl);
        } catch (BusinessException e) {
            throw new BusinessException(ExceptionCodes.PUSH_MESSAGE_FORMAT_ERROR);
        }
        PushParams pushParams = new PushParams(PropertyValueConstants.USER_TYPE_DOCTOR, null, message, linkUrl, null, pushMessage);
        /*
         ?????????android
         */
        channelPushHandler.getChannelPushService(PropertyValueConstants.PLATFORM_ANDRIOD).sendAllDevice(pushParams);
        /**
         * ?????????ios
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
            LOGGER.error("batchPushByGroup ???Push?????????????????????,recipientCacheTopushKey:{},userType:{},pushMessage:{}", recipientCacheTopushKey,
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
        LOGGER.info("batchPushByGroup,??????push??????:{}", repushval);
        /*
         *??????????????????
         */
        RedisUtil.setOps().sadd(PropertyValueConstants.GROUP_PUSH_RESEND_KEY, repushval);
        Set<String> userIdSet = null;
        RedisSetPageUtil redisPageUtil = new RedisSetPageUtil(recipientCacheTopushKey, NOTICE_GROUP_SEND_USERS_PAGESIZE);
        int pageCount = redisPageUtil.getPageCount();
        LOGGER.info("pageCount???{}", pageCount);
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
     * ??????push
     * PushServiceImpl.pushInBatches()
     * @param userIdSet               ???????????????Ids
     * @param userType                ????????????
     * @param pushMessage             push????????????
     * @param linkUrl                 ?????????????????????
     * @param recipientCacheTopushKey ???????????????????????????key
     * @Author Ldl
     * @Date 2017???12???6???
     * @since 1.0.0
     */
    private void pushInBatches(Set<String> userIdSet, PushParams pushParams) throws BusinessException {
        String sendUrl = null;
        List<String> remUserIdList = null;
        String recipientCacheTopushKey = pushParams.getRecipientCacheTopushKey();
        String message = pushParams.getMessage();
        /*
         * ????????????????????????????????????,?????????????????????????????????????????????
         */
        List<UserDeviceVo> android0DeviceList = deviceTokenService.findToPushUsers(userIdSet, PropertyValueConstants.DEVICE_SOUND_OPEN,
                PropertyValueConstants.PLATFORM_ANDRIOD,
                PropertyValueConstants.USER_TYPE_DOCTOR);// ??????--s:0
        if (android0DeviceList != null && android0DeviceList.size() > 0) {
            pushParams.setMessage(message);
            pushParams.setSoundType(PropertyValueConstants.DEVICE_SOUND_OPEN);
            this.pushBatchAndroidDevice(android0DeviceList, recipientCacheTopushKey, pushParams);
        }

        List<UserDeviceVo> android1DeviceList = deviceTokenService.findToPushUsers(userIdSet, PropertyValueConstants.DEVICE_SOUND_OFF,
                PropertyValueConstants.PLATFORM_ANDRIOD,
                PropertyValueConstants.USER_TYPE_DOCTOR);// ??????--s:1
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
                LOGGER.error("pushBatchIosDevice ????????????ios push??????,ios0DeviceList:{},pushParams:{}",ios0DeviceList.size(),pushParams);
            }
        }

        List<UserDeviceVo> ios1DeviceList = deviceTokenService.findToPushUsers(userIdSet, PropertyValueConstants.DEVICE_SOUND_OFF,
                PropertyValueConstants.PLATFORM_IOS, PropertyValueConstants.USER_TYPE_DOCTOR);// ios--s:0
        if (ios1DeviceList != null && ios1DeviceList.size() > 0) {
            pushParams.setPushMessage(pushParams.getSoundClosedMessage());
            try {
                this.pushBatchIosDevice(pushParams, ios1DeviceList, recipientCacheTopushKey);
            }catch (BusinessException e){
                LOGGER.error("pushBatchIosDevice ????????????ios push??????,ios1DeviceList:{},pushParams:{}",ios1DeviceList.size(),pushParams);
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
            // ??????post??????????????????
            LOGGER.info("??????push???????????????android ????????????:{}", androidDeviceList.size());
            int batchPushResult = channelPushHandler.getChannelPushService(PropertyValueConstants.PLATFORM_ANDRIOD)
                                                    .batchSendDevice(pushParams, groupPushDeviceList);
        } catch (BusinessException e) {
            LOGGER.error("pushBatchAndroidDevice ??????push???????????????pushParams:{}", pushParams);
        }
        if(remUserIdList!=null && remUserIdList.size()>0){
            /*
            ??????????????????????????????????????????????????????
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
        LOGGER.info("??????push?????????????????????????????????:{}", groupPushDeviceList.size());
        channelPushHandler.getChannelPushService(PropertyValueConstants.PLATFORM_IOS).batchSendDevice(pushParams, null);
        /*
        ??????????????????????????????????????????????????????
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
     * @Date 2016???4???5???
     * @since 1.0.0
     */
    private String handleMessage(String message, String sound, String linkUrl) throws BusinessException {
        int remainLength = maxRemainLength(sound, linkUrl);
        if (remainLength <= PUSH_CONTENT_MIN_BYTE_LENGTH) {
            LOGGER.info("??????????????????256???????????? message {} ", message);
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
            LOGGER.info("?????????????????????????????????{}????????? userType {}  sendMessage {} ", PropertyValueConstants.USER_TYPE_DOCTOR, sendMessage);
            throw new BusinessException(ExceptionCodes.PUSH_MESSAGE_NULL);
        }
        return sendMessage;
    }

    /**
     * ????????????,??????,????????????????????????push?????????
     * @param message ????????????
     * @param sound   ????????????
     * @param linkUrl ????????????
     * @return
     */
    private String createAPNSJSON(String message, String sound, String linkUrl) {
        return PushJsonVo.defaultMessage(message, sound, linkUrl);
    }

    /**
     * ????????????,????????????????????????push?????????
     * @param message ????????????
     * @param linkUrl ????????????
     * @return
     */
    private String createAPNSDefulatJson(String message, String linkUrl) {
        return PushJsonVo.defaultMessage(message, "default", linkUrl);
    }

    /**
     * ???????????????message ????????????
     * @param sound   ????????????
     * @param linkUrl ????????????
     * @return
     */
    private static int maxRemainLength(String sound, String linkUrl) {
        sound = sound == null ? "default" : sound;
        int soundLength = PushJsonVo.defaultMessage("", sound, linkUrl).getBytes().length;
        return soundLength >= 256 ? 0 : 256 - soundLength;
    }


    @Override
    public void reSendPushByGroup() {
        Set<String> repushPushKeys = RedisUtil.setOps().smembers(PropertyValueConstants.GROUP_PUSH_RESEND_KEY);// ????????????????????????????????????
        PushParams pushParams = new PushParams();
        for (String repushval : repushPushKeys) {
            LOGGER.info("?????????push str???{}", repushPushKeys);
            GroupResendPushVO groupResendPushVo = JSON.parseObject(repushval, GroupResendPushVO.class);
            String recipientCacheTopushKey = groupResendPushVo.getRecipientCacheTopushKey();
            String message = groupResendPushVo.getPushMessage();
            String linkUrl = groupResendPushVo.getLinkUrl();
            pushParams.setMessage(message);
            String soundOpenMessage = null;
            try {
                soundOpenMessage = this.handleMessage(message, SOUND_TYPE_DEFAULT, linkUrl);
            } catch (BusinessException e) {
                LOGGER.warn("???????????????????????????,message:{},linkUrl:{},soundtype:{},e:{}", message, linkUrl, SOUND_TYPE_DEFAULT, e);
            }
            String soundClosedMessage = null;
            try {
                soundClosedMessage = this.handleMessage(message, null, linkUrl);
            } catch (BusinessException e) {
                LOGGER.warn("???????????????????????????,message:{},linkUrl:{},soundtype:{},e:{}", message, linkUrl, null, e);
            }
            pushParams.setSoundOpenMessage(soundOpenMessage);
            pushParams.setSoundClosedMessage(soundClosedMessage);
            pushParams.setLinkUrl(linkUrl);
            pushParams.setRecipientCacheTopushKey(recipientCacheTopushKey);
            /*
             * ??????????????????????????????push
             */
            if (!RedisUtil.keyOps().existsKey(recipientCacheTopushKey) || RedisUtil.zsetOps().zcard(recipientCacheTopushKey).equals(0L)) {
                RedisUtil.setOps().srem(PropertyValueConstants.GROUP_PUSH_RESEND_KEY, repushval);
                LOGGER.info("??????????????????????????????:{}", repushval);
                continue;
            }

            /*
             * ??????push
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
                        LOGGER.warn("??????push??????,pushMessage:{},linkUrl:{},recipientCacheTopushKey:{}", message, linkUrl, recipientCacheTopushKey);
                    }
                } else {
                    userIdSet = RedisUtil.zsetOps().zrange(recipientCacheTopushKey, 0L, NOTICE_GROUP_SEND_USERS_PAGESIZE);
                    try {
                        this.pushInBatches(userIdSet, pushParams);
                    } catch (BusinessException e) {
                        LOGGER.warn("??????push??????,pushMessage:{},linkUrl:{},recipientCacheTopushKey:{}", message, linkUrl, recipientCacheTopushKey);
                    }
                }
            }
            /*
             * ????????????????????????Push????????????????????????????????????????????????push?????????key
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
