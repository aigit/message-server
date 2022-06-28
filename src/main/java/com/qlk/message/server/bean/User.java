package com.qlk.message.server.bean;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 513987344300395611L;

    private Long userId;
    private String userType;
    private Integer platType;
    private String token;
    private Integer topicType;

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

    public Integer getTopicType() {
        return topicType;
    }

    public void setTopicType(Integer topicType) {
        this.topicType = topicType;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", userType=" + userType + ", platType=" + platType + ", token=" + token + ", topicType=" + topicType + "]";
    }

}
