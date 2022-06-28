package com.qlk.message.server.vo;

public class UserDeviceVo {
    
    private Long userId;
    private String userType;
    private Integer platType;
    private String token;
    private String topic;
    private String sound;
    private Integer status;

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }
    public Integer getPlatType() {
        return platType;
    }
    public void setPlatType(Integer platType) {
        this.platType = platType;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public String getSound() {
        return sound;
    }
    public void setSound(String sound) {
        this.sound = sound;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserDeviceVo [userId=" + userId + ", userType=" + userType + ", platType=" + platType + ", token=" + token + ", topic=" + topic
                + ", sound=" + sound + ", status=" + status + "]";
    }
    
}
