package com.qlk.message.server.bean;

import java.util.StringJoiner;

/**
 * push请求的属性设置
 * @author Ldl
 * @date 2018/11/1 14:17
 * @since 1.0.0
 */
public class PushRequest {

    /**
     * 此次推送的标签，用于统计
     */
    private String pushTag;


    private String pushId;

    private Integer sequence;

    /**
     * 消息离线存储时间（单位为秒）
     */
    private Integer expireTime;

    public String getPushTag() {
        return pushTag;
    }

    public void setPushTag(String pushTag) {
        this.pushTag = pushTag;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", PushRequest.class.getSimpleName() + "[", "]")
                .add("pushTag='" + pushTag + "'")
                .add("pushId='" + pushId + "'")
                .add("sequence=" + sequence)
                .add("expireTime=" + expireTime)
                .toString();
    }
}
