package com.qlk.message.server.service;


/**
 * @author Ldl
 */
public interface ITaskRecordService {

    /**
     * 完成push发送已完成的数据清除
     * ITaskRecordController.removeCompleted()
     * @Author chenlin
     * @Date 2016年5月16日
     * @since 1.0.0
     */
    public void removeCompleted();

    /**
     * 查询push发送未完成的数据
     * ITaskRecordService.handleStatus()
     * @Author chenlin
     * @Date 2016年5月16日
     * @since 1.0.0
     */
    public void handleStatus();

    /**
     * 补发发送失败的 Push
     * 一.android补发策略：
     * a.检测10分钟内的且未补发过的异常数据，包括
     * 1.单个发送失败的push；
     * 2.批量发送失败的push记录
     * b.对a中的数据进行补发，补发完成后记录补发标记和补发次数
     */
    public void reSendFailedPush(Integer batchSize);
}
