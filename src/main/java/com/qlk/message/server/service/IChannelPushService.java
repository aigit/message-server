package com.qlk.message.server.service;

import java.util.ArrayList;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.message.server.bean.PushParams;

/**
 * 通知推送接口
 * @author Ldl
 * @date 2018/11/13 11:37
 * @since 1.0.0
 */
public interface IChannelPushService {

    /**
     * 根据设备token向某个设备发送push
     * @param token
     * @param message
     * @param linkUrl
     */
    void sendSingleDevice(PushParams pushParams) throws BusinessException;

    /**
     * 向所有设备发送push
     * @param pushParams
     * @throws BusinessException
     */
    void sendAllDevice(PushParams pushParams) throws BusinessException;

    /**
     * 向终端设备批量Push
     * @param pushParams
     * @param deviceList
     * @throws BusinessException
     */
    int batchSendDevice(PushParams pushParams, ArrayList<String> deviceList) throws BusinessException;
}
