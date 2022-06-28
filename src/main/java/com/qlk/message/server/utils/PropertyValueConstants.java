package com.qlk.message.server.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 属性常量文件(公共属性)
 * @author guoyongxiang E-mail: xianshu@qlk.com
 * @version 2015-6-23 下午10:43:49 by guoyongxiang
 */
public abstract class PropertyValueConstants {

    /**
     * 移动设备平台and
     */
    public static final Integer PLATFORM_ANDRIOD = 1;
    /**
     * 移动设备平台ios
     */
    public static final Integer PLATFORM_IOS = 2;
    // /**
    // * 移动设备平台and(im)
    // */
    // public static final Integer PLATFORM_ANDRIOD_IM = 3;

    /**
     * 用户类型区分:医生端d
     */
    public static final String USER_TYPE_DOCTOR = "d";
    /**
     * 用户类型区分:患者端p
     */
    public static final String USER_TYPE_PATIENT = "p";
    /**
     * 发送push前缀
     */
    public static final String SEND_PUSH_URL_PRE = "send.push.";
    /**
     * task push 通知前缀
     */
    public static final String NOTICE_PUSH_URL_PRE = "task.push.";
    /**
     * 公共push前缀
     */
    public static final String PUBLIC_PUSH_URL_PRE = "pub.push.";

    /**
     * 分组push地址前缀
     */
    public static final String GROUP_PUSH_URL_PRE = "group.push.";
    /**
     * redis key 模板地址
     */
    public static final String PUSH_CONTENT_CACHE = "baymax-msgSv|template_";

    /** 客户端声音开关类型：开 */
    public static final int DEVICE_SOUND_OPEN = 0;
    /** 客户端声音开关：关 */
    public static final int DEVICE_SOUND_OFF = 1;

    /**
     * 分组公告待重发缓存KEY
     */
    public static final String GROUP_NOTICE_RESEND_KEY = "group_notice_resend";

    /**
     * 分组push待重发缓存KEY
     */
    public static final String GROUP_PUSH_RESEND_KEY = "group_push_resend";

    /**
     * 石榴云医android APP首页
     */
    public static final String ANDROID_MAIN_ACTIVITY = "JS_MainActivity";

    /**
     * push消息点击跳转页映射
     */
    public static final Map<String,String> PUSH_CLICK_ACTION_MAP = new HashMap<>(50);

    static {
        /**
         * 公告
         */
        PUSH_CLICK_ACTION_MAP.put("web","JS_MainActivity");
        /**
         * 首页
         */
        PUSH_CLICK_ACTION_MAP.put("home","JS_MainActivity");
        /**
         * 积分明细
         */
        PUSH_CLICK_ACTION_MAP.put("jifenDetail","JS_MainActivity");

    }

    /**
     * 批量发送push成功code
     */
    public static final int PUSH_BATCH_RESULT_SUCCESS = 1;

    public static final String[] PUSH_SINGLE_ERROR_CODES = { "15", "76" };

}