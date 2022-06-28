package com.qlk.message.server.bean;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.tencent.xinge.push.app.PushAppRequest;
import com.tencent.xinge.push.app.PushAppResponse;

/**
 * push失败记录
 * @author Ldl
 * @date 2018/11/15 11:30
 * @since 1.0.0
 */
public class FailPushRecord {

    /**
     * 发送失败
     */
    public static final int SEND_RESULT_CODE_FAIL = 1;
    /**
     * 未知发送结果
     */
    public static final int SEND_RESULT_CODE_UNKNOW = 2;

    /**
     * 单个发送
     */
    public static final int QUANTITY_TYPE_SINGLE = 1;

    /**
     * 批量发送
     */
    public static final int QUANTITY_TYPE_BATCH = 2;

    /**
     * 全部发送
     */
    public static final int QUANTITY_TYPE_ALL = 3;

    /**
     * android
     */
    public static final int PUSH_PLATFORM_ANDROID = 1;
    /**
     * iOS
     */
    public static final int PUSH_PLATFORM_IOS = 2;

    @Id
    private String id;

    private String pushId;

    private Integer pushSeq;

    /**
     * 发送数量类型 1-单个,2-批量
     */
    private Integer quantityType;

    private String token;

    private Long userId;

    /**
     * 发送失败的tokenList
     */
    private List<String> tokenList;

    private PushAppRequest pushAppRequest;

    private PushAppResponse pushAppResponse;

    /**
     * 发送结果编码
     */
    private String resultCode;

    private Long createAt;

    /**
     * 补发次数
     */
    private Integer reSendTimes;

    /**
     * 失败平台 1-android 2-ios
     */
    private Integer platform;

    private PushParams pushParams;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public Integer getPushSeq() {
        return pushSeq;
    }

    public void setPushSeq(Integer pushSeq) {
        this.pushSeq = pushSeq;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public List<String> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<String> tokenList) {
        this.tokenList = tokenList;
    }

    public PushAppRequest getPushAppRequest() {
        return pushAppRequest;
    }

    public void setPushAppRequest(PushAppRequest pushAppRequest) {
        this.pushAppRequest = pushAppRequest;
    }

    public PushAppResponse getPushAppResponse() {
        return pushAppResponse;
    }

    public void setPushAppResponse(PushAppResponse pushAppResponse) {
        this.pushAppResponse = pushAppResponse;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getQuantityType() {
        return quantityType;
    }

    public void setQuantityType(Integer quantityType) {
        this.quantityType = quantityType;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Integer getReSendTimes() {
        return reSendTimes;
    }

    public void setReSendTimes(Integer reSendTimes) {
        this.reSendTimes = reSendTimes;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public PushParams getPushParams() {
        return pushParams;
    }

    public void setPushParams(PushParams pushParams) {
        this.pushParams = pushParams;
    }
}
