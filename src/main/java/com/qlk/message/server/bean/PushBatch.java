package com.qlk.message.server.bean;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
 * 批量化消息
 * @author chenlin
 * @since 1.0.0
 */
public class PushBatch implements Serializable {
    /**
     * 字段或域定义：<code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -8848605101898148085L;

    @Id
    private String id;
    /** 医生d */
    private String userType;
    /** 用户ID */
    private Long userId;
    /** 消息内容 */
    private String message;
    /** 跳转url */
    private String linkUrl;
    /** 批量ID */
    private String batchId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

}
