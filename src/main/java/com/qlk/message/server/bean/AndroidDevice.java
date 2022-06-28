package com.qlk.message.server.bean;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * Android用户设备信息
 * @author chenlin
 * @since 1.0.0
 */
public class AndroidDevice implements Serializable {

    private static final long serialVersionUID = 4888178427752521757L;

    @Id
    private String id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 设备唯一号
     */
    private String deviceToken;
    /**
     * 用户类型(医生端:d,患者端:p)
     */
    private String userType;
    /**
     * App平台(phone/pad)
     */
    private String appPlatform;
    /**
     * App版本(2.0.1)
     */
    private String appVersion;
    /**
     * 机型(如:华为荣耀7)
     */
    private String model;
    /**
     * 设备操作系统版本(如:5.4.1)
     */
    private String operateVersion;
    /**
     * 声音开关类型(0,开；1,关)
     */
    private Integer soundType;
    /**
     * 状态(0:正常，1禁止)
     */
    private Integer status;
    /**
     * 登录ip
     */
    private String ip;
    /**
     * 登录地
     */
    private String loginAddr;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date changedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAppPlatform() {
        return appPlatform;
    }

    public void setAppPlatform(String appPlatform) {
        this.appPlatform = appPlatform;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOperateVersion() {
        return operateVersion;
    }

    public void setOperateVersion(String operateVersion) {
        this.operateVersion = operateVersion;
    }

    public Integer getSoundType() {
        return soundType;
    }

    public void setSoundType(Integer soundType) {
        this.soundType = soundType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLoginAddr() {
        return loginAddr;
    }

    public void setLoginAddr(String loginAddr) {
        this.loginAddr = loginAddr;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

}
