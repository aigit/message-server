package com.qlk.message.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.baymax.common.utils.lang.ConfigUtil;
import com.qlk.baymax.common.utils.net.RpcHttpUtils;
import com.qlk.baymax.common.utils.thread.ThreadPoolUtil;
import com.qlk.message.server.bean.PushParams;
import com.qlk.message.server.exception.ExceptionCodes;
import com.qlk.message.server.service.IChannelPushService;
import com.qlk.message.server.utils.HttpRequestUtils;
import com.qlk.message.server.utils.PropertyValueConstants;

/**
 * IOSpush服务
 * @author Ldl
 * @date 2018/11/12 19:00
 * @since 1.0.0
 */
@Component("iosPushService")
public class IosPushServiceImpl implements IChannelPushService {

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(IosPushServiceImpl.class);

    /**
     * 根据设备token向某个设备发送push
     * @param token
     * @param message
     * @param linkUrl
     */
    @Override
    public void sendSingleDevice(PushParams pushParams) throws BusinessException {
        Map<String, String> requsetParams = new HashMap<>(10);
        requsetParams.put("token", pushParams.getToken());
        // 获取发送push的消息url
        String sendUrl = ConfigUtil.getString(PropertyValueConstants.SEND_PUSH_URL_PRE + PropertyValueConstants.PLATFORM_IOS);
        requsetParams.put("userType", pushParams.getUserType());
        requsetParams.put("userId", String.valueOf(pushParams.getUserId()));
        requsetParams.put("message", pushParams.getPushMessage());
        // 使用post请求发送请求
        LOGGER.info("ios请求ipns push发送接口，发送push的sendUrl=" + sendUrl + "请求参数:" + requsetParams);
        String responseStr = HttpRequestUtils.doPost(sendUrl, requsetParams);
        LOGGER.info("请求push发送接口，发送push的返回值=" + responseStr);
        if (StringUtils.isEmpty(responseStr)) {
            LOGGER.error("remote invoke metod {} failure, param is {}", sendUrl, requsetParams);
            throw new BusinessException(ExceptionCodes.REMOTE_PUSH_MESSAGE_ERROR);
        }
    }

    @Override
    public void sendAllDevice(PushParams pushParams) throws BusinessException {
        /*
            ios请求远程接口，发送push
         */
        String sendIUrlKey = PropertyValueConstants.PUBLIC_PUSH_URL_PRE + PropertyValueConstants.PLATFORM_IOS;
        String sendIUrl = String.format(ConfigUtil.getString(sendIUrlKey));
        Map<String, String> requsetParams = new HashMap<>(10);
        requsetParams.put("userType", pushParams.getUserType());
        requsetParams.put("message", pushParams.getPushMessage());
        LOGGER.info("请求push发送接口，发送push的sendIUrl=" + sendIUrl + "请求参数:" + requsetParams);
        RpcHttpUtils.asyncCallPost(sendIUrl, requsetParams);
        LOGGER.info("请求push发送接口，发送push的sendIUrl结束");
    }

    @Override
    public int batchSendDevice(PushParams pushParams, ArrayList<String> deviceList) throws BusinessException {
        String groupDeviceParams = JSON.toJSONString(deviceList);
        // 构造请求远程的参数
        Map<String, String> requsetParams = new HashMap<>(10);
        requsetParams.put("userType", pushParams.getUserType());
        requsetParams.put("groupPushDeviceParams", pushParams.getGroupPushDeviceParams());
        requsetParams.put("message", pushParams.getPushMessage());
        String sendUrl = ConfigUtil.getString(PropertyValueConstants.GROUP_PUSH_URL_PRE + PropertyValueConstants.PLATFORM_IOS);
        // 使用post请求发送请求
        LOGGER.info("请求ios 批量push发送接口，pushMessage:{}", pushParams.getPushMessage());
        RpcHttpUtils.asyncCallPost(sendUrl, requsetParams);
        return PropertyValueConstants.PUSH_BATCH_RESULT_SUCCESS;
    }
}
