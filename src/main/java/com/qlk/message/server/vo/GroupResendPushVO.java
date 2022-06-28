/**
 * 
 */
package com.qlk.message.server.vo;

import java.util.StringJoiner;

/**
 * @author Ldl 
 * @since 1.0.0
 */
public class GroupResendPushVO {

    /**
     * 构造函数
     */
    public GroupResendPushVO() {
    }

    /**
     * 构造函数
     * @param userType
     * @param pushMessage
     * @param linkUrl
     * @param recipientCacheTopushKey
     */
    public GroupResendPushVO(String userType, String pushMessage, String linkUrl, String recipientCacheTopushKey) {
        super();
        this.userType = userType;
        this.pushMessage = pushMessage;
        this.linkUrl = linkUrl;
        this.recipientCacheTopushKey = recipientCacheTopushKey;
    }

    private String userType;
    private String pushMessage;
    private String linkUrl;
    private String recipientCacheTopushKey;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getRecipientCacheTopushKey() {
        return recipientCacheTopushKey;
    }

    public void setRecipientCacheTopushKey(String recipientCacheTopushKey) {
        this.recipientCacheTopushKey = recipientCacheTopushKey;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", GroupResendPushVO.class.getSimpleName() + "[", "]")
                .add("userType='" + userType + "'")
                .add("pushMessage='" + pushMessage + "'")
                .add("linkUrl='" + linkUrl + "'")
                .add("recipientCacheTopushKey='" + recipientCacheTopushKey + "'")
                .toString();
    }
}
