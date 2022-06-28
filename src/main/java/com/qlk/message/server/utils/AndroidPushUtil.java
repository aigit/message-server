package com.qlk.message.server.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.baymax.common.utils.beans.JacksonMapper;
import com.qlk.baymax.common.utils.date.DateUtil;
import com.qlk.baymax.common.utils.lang.ConfigUtil;
import com.qlk.message.server.bean.FailPushRecord;
import com.qlk.message.server.bean.PushRequest;
import com.qlk.message.server.exception.ExceptionCodes;
import com.tencent.xinge.XingeApp;
import com.tencent.xinge.bean.AudienceType;
import com.tencent.xinge.bean.Message;
import com.tencent.xinge.bean.MessageType;
import com.tencent.xinge.bean.OperatorType;
import com.tencent.xinge.bean.Platform;
import com.tencent.xinge.bean.TagTokenPair;
import com.tencent.xinge.device.tag.DeviceTagRequest;
import com.tencent.xinge.push.app.PushAppRequest;
import com.tencent.xinge.push.app.PushAppResponse;

/**
 * Android Push 工具类
 * @author Ldl
 * @date 2018/11/1 18:55
 * @since 1.0.0
 */
public class AndroidPushUtil {

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(AndroidPushUtil.class);

    private static final String XINGE_APP_ID = ConfigUtil.getString("android.push.xinge.appid");
    private static final String XINGE_SECRET_KEY = ConfigUtil.getString("android.push.xinge.secretkey");

    /**
     * 信鸽push 批量设备临时标签
     */
    public static final String XINGE_PUSH_TEMP_TAG_PREFIX = ConfigUtil.getString("android.push.xinge.temptag.prefix");

    /**
     * PUSH 外链点击跳转确认
     */
    public static final Integer CLICK_ACTION_BROWSER_JUMP_CONFIRM_TRUE = 1;
    /**
     * PUSH 外链点击跳转不确认
     */
    public static final Integer CLICK_ACTION_BROWSER_JUMP_CONFIRM_FALSE = 0;

    /**
     * android 提示音开
     */
    public static final int ALERT_RING_ON = 1;

    /**
     * android 提示音关
     */
    public static final int ALERT_RING_OFF = 0;

    /**
     * 信鸽Push 业务执行结果解析名称
     */
    public static final String XINGE_SERVER_RESULT_FLAG_NAME = "result";
    /**
     * 信鸽服务返回业务执行结果状态码 0-成功
     */
    public static final Integer XINGE_SERVER_RESULT_SUCCESS = 0;

    /**
     * 信鸽服务返回业务执行结果状态码 1-失败
     */
    public static final Integer XINGE_SERVER_RESULT_FAIL = 1;

    private static class XingeAndroidPush{
        private static XingeApp xingeApp = new XingeApp(XINGE_APP_ID,XINGE_SECRET_KEY);
    }
    public static XingeApp getXingeApp(){
        return XingeAndroidPush.xingeApp;
    }

    /**
     * 对多个设备设置同一个标签
     *
     * @param tag       标签
     * @param tokenList 设备tokenlist
     *
     * @throws BusinessException
     */
    public static void setSingleTagToMultToken(String tag, ArrayList<String> tokenList) throws BusinessException {
        List<TagTokenPair> tagTokenPairList = new ArrayList<>();
        for (String token : tokenList) {
            TagTokenPair tagTokenPair = new TagTokenPair(tag, token);
            tagTokenPairList.add(tagTokenPair);
        }
        XingeApp xingeApp = AndroidPushUtil.getXingeApp();

        DeviceTagRequest deviceTagRequest = new DeviceTagRequest();
        deviceTagRequest.setOperator_type(OperatorType.ADD_SINGLE_TAG_MULT.getType());
        deviceTagRequest.setPlatform(Platform.android);
        deviceTagRequest.setToken_list(tokenList);
        ArrayList tagList = new ArrayList(1);
        tagList.add(tag);
        deviceTagRequest.setTag_list(tagList);
        long startTime = System.currentTimeMillis();
        JSONObject setTagResultJson = xingeApp.deviceTag(deviceTagRequest.toString());
        LOGGER.info("setSingleTagToMultToken 批量设置标签 成功,tag:{},costtime:{}", tag, System.currentTimeMillis() - startTime);
    }

    /**
     * 针对批量设备删除某个标签
     *
     * @param tag
     * @param tokenList
     *
     * @throws BusinessException
     */
    public static void delSingleTagFromMultToken(String tag, ArrayList<String> tokenList) throws BusinessException {
        XingeApp xingeApp = AndroidPushUtil.getXingeApp();
        DeviceTagRequest deviceTagRequest = new DeviceTagRequest();
        deviceTagRequest.setPlatform(Platform.android);
        ArrayList<String> taglist = new ArrayList<>(1);
        taglist.add(tag);
        deviceTagRequest.setTag_list(taglist);
        deviceTagRequest.setToken_list(tokenList);
        deviceTagRequest.setOperator_type(OperatorType.DELE_SINGLE_TAG_MULT.getType());
        JSONObject jsonObject = xingeApp.deviceTag(deviceTagRequest.toString());

        LOGGER.info("delSingleTagFromMultToken 删除多个设备设置标签 成功,tag:{}", tag);
    }

    /**
     * 向单个标签发送push
     * @param tag
     * @param custompushRequest
     * @param message
     */
    public static void pushSingleTag(String tag, PushRequest custompushRequest, Message message) throws BusinessException {
        /*
        创建一次push请求
         */
        PushAppRequest pushAppRequest = new PushAppRequest();
        pushAppRequest.setAudience_type(AudienceType.tag);
        pushAppRequest.setPlatform(Platform.android);
        pushAppRequest.setMessage_type(MessageType.notify);
        pushAppRequest.setMessage(message);
        if (custompushRequest != null) {
            String pushId = custompushRequest.getPushId();
            pushAppRequest.setPush_id(pushId == null ? "1" : pushId);
            Integer sequence = custompushRequest.getSequence();
            pushAppRequest.setSeq(sequence == null ? 1 : sequence);
            pushAppRequest.setStat_tag(custompushRequest.getPushTag());
            /*
            设置消息离线存储时间（单位为秒）
            */
            pushAppRequest.setExpire_time(custompushRequest.getExpireTime());
        }
        pushAppRequest.setSend_time(com.qlk.baymax.common.utils.date.DateUtil.formatDate(new Date(System.currentTimeMillis()),
                DateUtil.DATE_TIME_FORMAT));
        long pushStartTime = System.currentTimeMillis();
        XingeApp xingeApp = AndroidPushUtil.getXingeApp();
        JSONObject jsonObject = xingeApp.pushApp(pushAppRequest.toString());
        LOGGER.info("xinge push 成功,pushId:{},pushSeq:{}", pushAppRequest.getPush_id(), pushAppRequest.getSeq());
    }

    /**
     * 向批量的安卓设备发送Push
     * @param deviceTokenList   设备列表
     * @param custompushRequest 自定义push 请求
     * @param message
     * @throws Exception
     */
    public static PushAppResponse pushBatchAndroidDevice(PushAppRequest pushAppRequest) throws BusinessException {
        /*
        创建一次push请求
         */
        pushAppRequest.setAudience_type(AudienceType.token_list);
        pushAppRequest.setPlatform(Platform.android);
        pushAppRequest.setMessage_type(MessageType.notify);
        pushAppRequest.setSend_time(com.qlk.baymax.common.utils.date.DateUtil.formatDate(new Date(System.currentTimeMillis()),
                DateUtil.DATE_TIME_FORMAT));
        long pushStartTime = System.currentTimeMillis();
        XingeApp xingeApp = AndroidPushUtil.getXingeApp();
        LOGGER.debug("pushBatchAndroidDevice push Request string:{}", pushAppRequest.toString());
        JSONObject jsonObject = null;
        PushAppResponse pushAppResponse = null;
        try {
            jsonObject = xingeApp.pushApp(pushAppRequest);
            pushAppResponse = JacksonMapper.toObj(jsonObject.toString(), PushAppResponse.class);
            if (!XINGE_SERVER_RESULT_SUCCESS.equals(pushAppResponse.getRet_code())) {
                LOGGER.warn("批量推送失败 pushBatchAndroidDevice push Request string:{}", pushAppRequest.toString());
                LOGGER.warn("批量推送失败 pushBatchAndroidDevice push result string:{},costTime:{}", jsonObject,
                        System.currentTimeMillis() - pushStartTime);
                return pushAppResponse;
            }
        } catch (Exception e) {
            LOGGER.warn("解析信鸽返回结果异常pushBatchAndroidDevice push Request string:{}", pushAppRequest.toString());
            LOGGER.warn("解析信鸽返回结果异常pushBatchAndroidDevice push result string:{},costTime:{}", jsonObject, System.currentTimeMillis() - pushStartTime);
            return null;
        }
        LOGGER.debug("推送成功 pushBatchAndroidDevice push result string:{},costTime:{},responseStr:{}", jsonObject,
                System.currentTimeMillis() - pushStartTime, jsonObject);
        LOGGER.debug("xinge push 成功,pushId:{},pushSeq:{}", pushAppRequest.getPush_id(), pushAppRequest.getSeq());
        return pushAppResponse;
    }

    /**
     * 向单个token push
     * @param token
     * @param custompushRequest
     * @param message
     * @throws BusinessException
     */
    public static PushAppResponse pushSingleToken(PushAppRequest pushAppRequest) throws BusinessException {
        /*
        创建一次push请求
         */
        pushAppRequest.setAudience_type(AudienceType.token);
        pushAppRequest.setPlatform(Platform.android);
        pushAppRequest.setMessage_type(MessageType.notify);
        long pushStartTime = System.currentTimeMillis();
        XingeApp xingeApp = AndroidPushUtil.getXingeApp();
        JSONObject jsonObject = null;
        PushAppResponse pushAppResponse = null;
        try {
            jsonObject = xingeApp.pushApp(pushAppRequest);
            pushAppResponse = JacksonMapper.toObj(jsonObject.toString(), PushAppResponse.class);
            if (!XINGE_SERVER_RESULT_SUCCESS.equals(pushAppResponse.getRet_code())) {
                LOGGER.warn("推送单个token失败 pushSingleToken push Request string:{}，result string:{}，costTime:{}", pushAppRequest,
                        jsonObject, System.currentTimeMillis() - pushStartTime);
                return pushAppResponse;
            }
        } catch (Exception e) {
            LOGGER.warn("推送单个token 解析推送结果异常 pushSingleToken push Request string:{}，result string:{}，costTime:{}", pushAppRequest,
                    jsonObject, System.currentTimeMillis() - pushStartTime);
            return null;
        }
        LOGGER.debug("xinge push 成功,pushId:{},pushSeq:{},pushAppRequest:{}", pushAppRequest.getPush_id(), pushAppRequest.getSeq(), pushAppRequest);
        return pushAppResponse;
    }

    /**
     * 向所有已注册信鸽的安卓设备发送push
     *
     * @param custompushRequest
     * @param message
     *
     * @throws Exception
     */
    public static PushAppResponse pushAllAndroidDevice(PushAppRequest pushAppRequest) throws Exception {
         /*
        创建一次push请求
         */
        pushAppRequest.setAudience_type(AudienceType.all);
        pushAppRequest.setPlatform(Platform.android);
        pushAppRequest.setMessage_type(MessageType.notify);
        pushAppRequest.setSend_time(com.qlk.baymax.common.utils.date.DateUtil.formatDate(new Date(System.currentTimeMillis()),
                DateUtil.DATE_TIME_FORMAT));
        long pushStartTime = System.currentTimeMillis();
        XingeApp xingeApp = AndroidPushUtil.getXingeApp();
        LOGGER.debug("pushAllAndroidDevice push Request string:{}", pushAppRequest.toString());
        JSONObject jsonObject = null;
        PushAppResponse pushAppResponse = null;
        try {
            jsonObject = xingeApp.pushApp(pushAppRequest);
            pushAppResponse = JacksonMapper.toObj(jsonObject.toString(), PushAppResponse.class);
            if (!XINGE_SERVER_RESULT_SUCCESS.equals(pushAppResponse.getRet_code())) {
                LOGGER.warn("全量推送失败 pushAllAndroidDevice push Request string:{}", pushAppRequest.toString());
                LOGGER.warn("全量推送失败 pushAllAndroidDevice push result string:{},costTime:{}", jsonObject,
                        System.currentTimeMillis() - pushStartTime);
                return pushAppResponse;
            }
        } catch (Exception e) {
            LOGGER.warn("解析信鸽返回结果异常 pushAllAndroidDevice push Request string:{}", pushAppRequest.toString());
            LOGGER.warn("解析信鸽返回结果异常 pushAllAndroidDevice push result string:{},costTime:{}", jsonObject,
                    System.currentTimeMillis() - pushStartTime);
            return null;
        }
        LOGGER.debug("推送成功 pushAllAndroidDevice push result string:{},costTime:{},responseStr:{}", jsonObject,
                System.currentTimeMillis() - pushStartTime, jsonObject);
        LOGGER.debug("xinge push 成功,pushId:{},pushSeq:{}", pushAppRequest.getPush_id(), pushAppRequest.getSeq());
        return pushAppResponse;
    }
}
