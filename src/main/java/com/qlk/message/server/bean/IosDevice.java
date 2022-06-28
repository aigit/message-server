package com.qlk.message.server.bean;

// default package

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * IOS用户设备信息
 * @author chenlin
 * @since 1.0.0
 */
public class IosDevice implements Serializable {

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
     * 状态(0:正常，1禁止)
     */
    private Integer status;
    /**
     * App版本(2.0.1)
     */
    private String appVersion;
    /**
     * App平台(phone/pad)
     */
    private String appPlatform;
    /**
     * 设备操作系统版本(如:5.4.1)
     */
    private String operateVersion;
    /**
     * 机型(如:华为荣耀7)
     */
    private String model;// 机型
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
    /**
     * 通知类型(0生产;1环境)
     */
    private Integer noticeType;
    /**
     * 声音开关类型(0,开；1,关)
     */
    private Integer soundType;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppPlatform() {
        return appPlatform;
    }

    public void setAppPlatform(String appPlatform) {
        this.appPlatform = appPlatform;
    }

    public String getOperateVersion() {
        return operateVersion;
    }

    public void setOperateVersion(String operateVersion) {
        this.operateVersion = operateVersion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public Integer getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(Integer noticeType) {
        this.noticeType = noticeType;
    }

    public Integer getSoundType() {
        return soundType;
    }

    public void setSoundType(Integer soundType) {
        this.soundType = soundType;
    }

}
