package com.qlk.message.server.bean;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
 * 批量任务消息(由批量消息/定时消息转化为发送方需要的json消息对象)
 * @author chenlin
 * @since 1.0.0
 */
public class TaskMessage implements Serializable {

    /**
     * 字段或域定义：<code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 7975222923207811935L;

    @Id
    private String id;
    /**
     * 用户消息
     */
    private User user;
    /**
     * 批量消息
     */
    private String batchId;
    /**
     * 消息json
     */
    private String message;
    /**
     * 发送状态
     */
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskMessage [id=" + id + ", user=" + user + ", batchId=" + batchId + ", message=" + message + ", status=" + status + "]";
    }

}
