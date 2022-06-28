package com.qlk.message.server.service;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.message.server.bean.PushParams;
import com.qlk.message.server.vo.GroupMessageVO;

/**
 * @author Ldl Modify on 2018-10-31
 */
public interface IUserPushService {

    /**
     * <发送定时任务push消息>
     *
     * @param taskId
     *
     * @throws BusinessException void <返回值描述>
     * @Throws 异常信息
     * @History 2015年7月22日 上午10:27:24 by chenlin
     */
    void saveTaskPush(String taskId) throws BusinessException;

    /**
     * <发送实时消息>
     *
     * @param userId
     * @param userType
     * @param message
     * @param linkUrl
     *
     * @throws BusinessException void <返回值描述>
     * @Throws 异常信息sendSingleDevice
     * @History 2015年7月22日 上午10:26:43 by chenlin
     */
    void sendSinglePushMessage(PushParams pushParams) throws BusinessException;

    /**
     * IUserPushService.sendMultiPushMessage()
     *
     * @param userType    用户类型
     * @param pushMessage push内容
     * @param linkUrl     跳转地址
     *
     * @throws BusinessException
     * @Author chenlin
     * @Date 2016年3月29日
     * @since 1.0.0
     */
    void pushToAllUser(String message, String linkUrl) throws BusinessException;

    /**
     * 按用户标签批量发送私人push
     * IUserPushService.batchPushOnPrivate()
     * @param userType
     * @param pushMessage
     * @param linkUrl
     * @param recipientCacheTopushKey
     * @Author Ldl
     * @Date 2017年11月29日
     * @since 1.0.0
     */
    void batchPushByGroup(String message, String linkUrl, String recipientCacheTopushKey) throws BusinessException;

    /**
     * 批量向用户发送差异化push
     * IUserPushService.sendBatchMessage()
     *
     * @param userType    用户类型
     * @param pushBatchId push批次号
     *
     * @throws BusinessException
     * @Author chenlin
     * @Date 2016年3月29日
     * @since 1.0.0
     */
    void sendBatchMessage(String pushBatchId) throws BusinessException;

    /**
     * 补发分组push
     * IUserPushService.ReSendPushByGroup()
     *
     * @throws BusinessException
     * @Author Ldl
     * @Date 2017年12月5日
     * @since 1.0.0
     */
    void reSendPushByGroup();

    /**
     * 查询缓存中待分组发送的公告、push
     * IUserPushService.getGroupMessageInCache()
     *
     * @return
     *
     * @Author Ldl
     * @Date 2017年12月26日
     * @since 1.0.0
     */
    GroupMessageVO getGroupMessageInCache();


}
