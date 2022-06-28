/**
 * 
 */
package com.qlk.message.server.bean;

import java.io.Serializable;
import java.util.Date;

/** 
 * @Description PUSH数据
 * @author guoyongxian
 * @E-mail: xianshu@qlk.com
 * @version 2015-7-21 下午5:23:49 by guoyongxiang
 */
public class PushTask implements Serializable{

    private static final long serialVersionUID = 3445469257739658930L;
    private String id;
    /** 医生d */
    private String userType;
    /** 用户ID */
    private Long userId;
    /** 用户名称 */
    private String name;
    /** 消息参数 */
    private String messageParams;
    /** 任务ID */
    private String taskId;
    /** 创建时间 */
    private Date createdAt;
    
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMessageParams() {
        return messageParams;
    }
    public void setMessageParams(String messageParams) {
        this.messageParams = messageParams;
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
