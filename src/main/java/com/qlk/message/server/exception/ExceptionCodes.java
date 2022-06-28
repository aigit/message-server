package com.qlk.message.server.exception;

import com.qlk.baymax.common.exception.BaseExceptionCodes;

/**
 * 消息服务异常
 * 范围：1204xxxx(维护后四位)
 * 规则：[1-2位]大类 [3-4位]业务 [5-6位]模块 [7-8位]顺序错误码
 * @author guoyongxiang
 *
 */
public interface ExceptionCodes extends BaseExceptionCodes {

    /**  推送消息为空 */
    int PUSH_MESSAGE_NULL = 12040101;
    
    /** 推送消息超过长度限制，无法发送 */
    int PUSH_MESSAGE_TOO_LONG = 12040102;
    
    /** 远程推送服务返回异常 */
    int REMOTE_PUSH_MESSAGE_ERROR = 12040103;
    
    /** 推送任务不存在或者模板不存在 */
    int PUSH_MESSAGE_TASK_NULL = 12040104;
    
    /** 推送消息设备号没有找到 */
    int PUSH_MESSAGE_DEVICE_NULL = 12040105;
    
    /** 用户主动禁止接收推送 */
    int PUSH_MESSAGE_DEVICE_NORECEIVE = 12040106;
    /**
     * 信鸽push失败
     */
    int XINGE_PUSH_FAIL = 12040107;

    /**
     * 给多个设备设置同一个标签失败
     */
    int XINGE_SET_SINGLE_TAG_TO_MULT_FAIL = 12040108;

    /**
     * 删除多个设备的某个标签失败
     */
    int XINGE_DEL_SINGLE_TAG_FROM_MULT_FAIL = 12040109;

    /**
     * 向所有设备发送push失败
     */
    int XINGE_PUSH_ALL_DEVICE_FAIL = 12040110;

    /**
     * 向单个TAG发送PUSH 失败
     */
    int XINGE_PUSH_SINGLE_TAG_FAIL = 12040111;

    /**
     * 格式化Push内容异常
     */
    int PUSH_MESSAGE_FORMAT_ERROR = 12040112;
    
}
