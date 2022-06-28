package com.qlk.message.server.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息发送记录
 * @author chenlin
 * @since 1.0.0
 */
public class TaskRecord implements Serializable {
    /**
     * 字段或域定义：<code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -9055250823196778126L;

    private String id;
    /**
     * 批次号
     */
    private String batchId;
    /**
     * 1.数据准备完毕,2.push发送执行完毕,3.基础数据表数据已经清除
     */
    private Integer imStatus;
    /**
     * 1.数据准备完毕,2.push发送执行完毕,3.基础数据表数据已经清除
     */
    // private Integer apnsStatus;
    /**
     * 基础数据创建时间
     */
    private Date createdAt;
    /**
     * 基础数据删除时间
     */
    private Date rmRecordDate;

    public TaskRecord() {

    }

    // 当基础数据创建完毕，通知发送方发送消息时，记录的基础数据
    public TaskRecord(String batchId) {
        this.batchId = batchId;
        this.imStatus = 1;
        // this.apnsStatus = 1;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Integer getImStatus() {
        return imStatus;
    }

    public void setImStatus(Integer imStatus) {
        this.imStatus = imStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getRmRecordDate() {
        return rmRecordDate;
    }

    public void setRmRecordDate(Date rmRecordDate) {
        this.rmRecordDate = rmRecordDate;
    }

}
