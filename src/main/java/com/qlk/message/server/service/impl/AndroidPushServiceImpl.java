package com.qlk.message.server.service.impl;

import io.lettuce.core.pubsub.PubSubCommandHandler;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.baymax.common.utils.biz.AppJumpProtocol;
import com.qlk.baymax.common.utils.date.DateUtil;
import com.qlk.baymax.common.utils.lang.ConfigUtil;
import com.qlk.message.server.bean.FailPushRecord;
import com.qlk.message.server.bean.PushParams;
import com.qlk.message.server.bean.PushRequest;
import com.qlk.message.server.dao.mongo.FailPushRecordDao;
import com.qlk.message.server.service.IChannelPushService;
import com.qlk.message.server.utils.AndroidPushUtil;
import com.tencent.xinge.bean.Browser;
import com.tencent.xinge.bean.ClickAction;
import com.tencent.xinge.bean.Message;
import com.tencent.xinge.bean.MessageAndroid;
import com.tencent.xinge.push.app.PushAppRequest;
import com.tencent.xinge.push.app.PushAppResponse;

/**
 * Android push 推送服务
 * @author Ldl
 * @date 2018/11/13 11:11
 * @since 1.0.0
 */
@Component("androidPushService")
public class AndroidPushServiceImpl implements IChannelPushService {

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(AndroidPushServiceImpl.class);

    @Autowired
    private FailPushRecordDao failPushRecordDao;

    /**
     * android 跳转协议intent配置地址
     */
    private static final String XINGE_PUSH_ANDROID_INTENT_REDIRECT_URL = ConfigUtil.getString("android.push.xinge.redirect.protocol");

    /**
     * 根据设备token向某个设备发送push
     * @param token
     * @param message
     * @param linkUrl
     */
    @Override
    public void sendSingleDevice(PushParams pushParams) throws BusinessException {
        Message pushMessage = this.getAndroidPushMessage(pushParams);
        PushAppRequest pushAppRequest = new PushAppRequest();
        pushAppRequest.setMessage(pushMessage);
        ArrayList<String> tokenList = new ArrayList<>(1);
        tokenList.add(pushParams.getToken());
        pushAppRequest.setToken_list(tokenList);
        pushAppRequest.setPush_id(pushParams.getUserId() == null ? null : pushParams.getUserId().toString());
        pushAppRequest.setStat_tag(pushParams.getPushTag());
        /*
        设置消息离线存储时间（单位为秒）
        */
        pushAppRequest.setExpire_time(pushParams.getExpireTime() == null ? 0 : pushParams.getExpireTime());
        pushAppRequest.setSend_time(com.qlk.baymax.common.utils.date.DateUtil.formatDate(new Date(System.currentTimeMillis()),
                DateUtil.DATE_TIME_FORMAT));

        PushAppResponse pushResult = AndroidPushUtil.pushSingleToken(pushAppRequest);
        if (pushResult != null && pushResult.getRet_code() == AndroidPushUtil.XINGE_SERVER_RESULT_SUCCESS) {
            LOGGER.debug("push single 发送成功,pushResult:{}", pushResult);
            return;
        }
        if (pushParams.getReSend() == null || !pushParams.getReSend()) {
            /*
             * 发送失败 记录失败发送记录
             */
            FailPushRecord failPushRecord = new FailPushRecord();
            failPushRecord.setCreateAt(System.currentTimeMillis());
            failPushRecord.setPushAppRequest(pushAppRequest);
            failPushRecord.setPushAppResponse(pushResult);
            failPushRecord.setResultCode(pushResult.getResult());
            failPushRecord.setPushId(pushAppRequest.getPush_id());
            failPushRecord.setPushSeq(pushAppRequest.getSeq());
            failPushRecord.setToken(pushParams.getToken());
            failPushRecord.setUserId(pushParams.getUserId());
            failPushRecord.setPushParams(pushParams);
            failPushRecord.setQuantityType(FailPushRecord.QUANTITY_TYPE_SINGLE);
            failPushRecordDao.saveFailPushRecord(failPushRecord);
        }

    }

    @Override
    public void sendAllDevice(PushParams pushParams) throws BusinessException {
        Message pushMessage = this.getAndroidPushMessage(pushParams);
        PushAppRequest pushAppRequest = new PushAppRequest();
        pushAppRequest.setMessage(pushMessage);
        pushAppRequest.setPush_id(pushParams.getUserId() == null ? null : pushParams.getUserId().toString());
        pushAppRequest.setStat_tag(pushParams.getPushTag());
        /*
        设置消息离线存储时间（单位为秒）
        */
        pushAppRequest.setExpire_time(pushParams.getExpireTime() == null ? 0 : pushParams.getExpireTime());

        PushAppResponse pushAppResponse = AndroidPushUtil.pushBatchAndroidDevice(pushAppRequest);

        if (pushAppResponse != null && pushAppResponse.getRet_code() == AndroidPushUtil.XINGE_SERVER_RESULT_SUCCESS) {
            LOGGER.debug("push all 发送成功,pushResult:{}", pushAppResponse);
            return;
        }
        if (pushParams.getReSend() == null || !pushParams.getReSend()) {
            /*
             * 发送失败 记录失败发送记录
             */
            FailPushRecord failPushRecord = new FailPushRecord();
            failPushRecord.setCreateAt(System.currentTimeMillis());
            failPushRecord.setPushAppRequest(pushAppRequest);
            failPushRecord.setPushAppResponse(pushAppResponse);
            failPushRecord.setPushId(pushAppRequest.getPush_id());
            failPushRecord.setQuantityType(FailPushRecord.QUANTITY_TYPE_ALL);
            failPushRecord.setPushParams(pushParams);
            failPushRecordDao.saveFailPushRecord(failPushRecord);
        }

    }

    @Override
    public int batchSendDevice(PushParams pushParams, ArrayList<String> deviceList) throws BusinessException {
        Message pushMessage = this.getAndroidPushMessage(pushParams);
        PushAppRequest pushAppRequest = new PushAppRequest();
        pushAppRequest.setMessage(pushMessage);
        pushAppRequest.setPush_id(pushParams.getUserId() == null ? null : pushParams.getUserId().toString());
        pushAppRequest.setStat_tag(pushParams.getPushTag());
        /*
        设置消息离线存储时间（单位为秒）
        */
        pushAppRequest.setExpire_time(pushParams.getExpireTime() == null ? 0 : pushParams.getExpireTime());
        /*
        设置发送目标
         */
        pushAppRequest.setToken_list(deviceList);
        PushAppResponse pushAppResponse = AndroidPushUtil.pushBatchAndroidDevice(pushAppRequest);
        if (pushAppResponse != null && pushAppResponse.getRet_code() == AndroidPushUtil.XINGE_SERVER_RESULT_SUCCESS) {
            LOGGER.debug("push batch 发送成功,pushResult:{}", pushAppResponse);
            return AndroidPushUtil.XINGE_SERVER_RESULT_SUCCESS;
        }
        if (pushParams.getReSend() == null || !pushParams.getReSend()) {
            /*
             * 发送失败 记录失败发送记录
             */
            FailPushRecord failPushRecord = new FailPushRecord();
            failPushRecord.setCreateAt(System.currentTimeMillis());
            failPushRecord.setPushAppRequest(pushAppRequest);
            failPushRecord.setPushAppResponse(pushAppResponse);
            failPushRecord.setPushId(pushAppRequest.getPush_id());
            failPushRecord.setQuantityType(FailPushRecord.QUANTITY_TYPE_BATCH);
            failPushRecord.setPushParams(pushParams);
            if (pushAppResponse != null) {
                LOGGER.debug("发送结果明细:{}", pushAppResponse.getResult());
            }
            failPushRecordDao.saveFailPushRecord(failPushRecord);
        }
        return AndroidPushUtil.XINGE_SERVER_RESULT_FAIL;
    }


    /**
     * 根据linkUrl给push添加点击事件
     * @param linkUrl
     * @return
     * @author liudelong
     */
    private void addClickActionOnPush(String linkUrl, MessageAndroid messageAndroid) {
        JsonNode node = null;
        try {
            node = com.qlk.baymax.common.utils.beans.JacksonMapper.toNode(linkUrl);
            ClickAction clickAction = new ClickAction();
            if (node == null) {
                clickAction.setAction_type(ClickAction.TYPE_ACTIVITY);
                return;
            }
            String linkK = node.get("K").asText();
            String linkV = node.get("V") == null ? null : node.get("V").asText();
            messageAndroid.setCustom_content(node.toString());
            /*
                根据linkUrl的跳转协议区分Push点击事件
             */
            if (AppJumpProtocol.DR_SAFARI.getK().equals(linkK)) {
                clickAction.setAction_type(ClickAction.TYPE_URL);
                Browser browser = new Browser();
                browser.setUrl(linkV);
                browser.setConfirm(AndroidPushUtil.CLICK_ACTION_BROWSER_JUMP_CONFIRM_TRUE);
                clickAction.setBrowser(browser);
            } else {
                clickAction.setAction_type(ClickAction.TYPE_INTENT);
                clickAction.setIntent(String.format(XINGE_PUSH_ANDROID_INTENT_REDIRECT_URL, URLEncoder.encode(node.toString(),
                        "UTF-8")));
                LOGGER.debug("INTENT:{}", clickAction.getIntent());
            }
            messageAndroid.setAction(clickAction);
        } catch (IOException e) {
            LOGGER.warn("linkUrl转换异常:{},e:{}", linkUrl, e);
        }
    }

    /**
     * 获取最终格式的android Push消息
     * @param message
     * @param linkUrl
     * @return
     */
    private Message getAndroidPushMessage(PushParams pushParams) {
        Message pushMessage = new Message();
        /*
            通知消息标题
         */
        pushMessage.setTitle(ConfigUtil.getString("android.push.xinge.commontitle"));
        /*
         * 通知消息内容
         */
        pushMessage.setContent(pushParams.getMessage());
        MessageAndroid messageAndroid = new MessageAndroid();
        messageAndroid.setRing(pushParams.getSoundType() == null ? AndroidPushUtil.ALERT_RING_ON : pushParams.getSoundType());
        /*
            根据linkUrl给push添加点击事件
         */
        this.addClickActionOnPush(pushParams.getLinkUrl(), messageAndroid);
        pushMessage.setAndroid(messageAndroid);
        return pushMessage;
    }
}
