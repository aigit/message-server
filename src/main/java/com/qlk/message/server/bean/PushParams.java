package com.qlk.message.server.bean;

import java.util.StringJoiner;

/**
 * @author Ldl
 * @date 2018/11/13 10:25
 * @since 1.0.0
 */
public class PushParams {

    /**
     * 用户类型 d-医生 p-患者
     */
    private String userType;
    /**
     * 设备token
     */
    private String token;
    /**
     * 消息主要内容
     */
    private String message;
    /**
     * 跳转协议
     */
    private String linkUrl;
    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 最终推送的消息
     */
    private String pushMessage;


    /**
     * push缓存key
     */
    private String recipientCacheTopushKey;

    /**
     * iOS批量发送设备信息
     */
    private String groupPushDeviceParams;

    /**
     * 提示音类型
     */
    private Integer soundType;

    /**
     * 是否需要补发
     */
    private Boolean needResend;

    /**
     * push标签，用于统计Push请求
     */
    private String pushTag;

    /**
     * 消息离线存储时间 单位:秒
     */
    private Integer expireTime;

    /**
     * 是否补发
     */
    private Boolean reSend;

    /**
     * 平台类型
     */
    private Integer platType;

    /**
     * IOS开提示音的消息格式
     */
    private String soundOpenMessage;
    /**
     * IOS无提示音的消息格式
     */
    private String soundClosedMessage;


    public PushParams() {

    }

    public PushParams(String userType, String token, String message, String linkUrl, Long userId, String pushMessage) {
        this.userType = userType;
        this.token = token;
        this.message = message;
        this.linkUrl = linkUrl;
        this.userId = userId;
        this.pushMessage = pushMessage;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }


    public String getRecipientCacheTopushKey() {
        return recipientCacheTopushKey;
    }

    public void setRecipientCacheTopushKey(String recipientCacheTopushKey) {
        this.recipientCacheTopushKey = recipientCacheTopushKey;
    }

    public String getGroupPushDeviceParams() {
        return groupPushDeviceParams;
    }

    public void setGroupPushDeviceParams(String groupPushDeviceParams) {
        this.groupPushDeviceParams = groupPushDeviceParams;
    }

    public Integer getSoundType() {
        return soundType;
    }

    public void setSoundType(Integer soundType) {
        this.soundType = soundType;
    }

    public Boolean getNeedResend() {
        return needResend;
    }

    public void setNeedResend(Boolean needResend) {
        this.needResend = needResend;
    }

    public String getPushTag() {
        return pushTag;
    }

    public void setPushTag(String pushTag) {
        this.pushTag = pushTag;
    }

    public Integer getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }

    public Boolean getReSend() {
        return reSend;
    }

    public void setReSend(Boolean reSend) {
        this.reSend = reSend;
    }

    public Integer getPlatType() {
        return platType;
    }

    public void setPlatType(Integer platType) {
        this.platType = platType;
    }

    public String getSoundOpenMessage() {
        return soundOpenMessage;
    }

    public void setSoundOpenMessage(String soundOpenMessage) {
        this.soundOpenMessage = soundOpenMessage;
    }

    public String getSoundClosedMessage() {
        return soundClosedMessage;
    }

    public void setSoundClosedMessage(String soundClosedMessage) {
        this.soundClosedMessage = soundClosedMessage;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", PushParams.class.getSimpleName() + "[", "]")
                .add("userType='" + userType + "'")
                .add("token='" + token + "'")
                .add("message='" + message + "'")
                .add("linkUrl='" + linkUrl + "'")
                .add("userId=" + userId)
                .add("pushMessage='" + pushMessage + "'")
                .add("recipientCacheTopushKey='" + recipientCacheTopushKey + "'")
                .add("groupPushDeviceParams='" + groupPushDeviceParams + "'")
                .add("soundType=" + soundType)
                .add("needResend=" + needResend)
                .add("pushTag='" + pushTag + "'")
                .add("expireTime=" + expireTime)
                .add("reSend=" + reSend)
                .add("platType=" + platType)
                .add("soundOpenMessage='" + soundOpenMessage + "'")
                .add("soundClosedMessage='" + soundClosedMessage + "'")
                .toString();
    }
}
