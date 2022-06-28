package com.qlk.message.server.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.BagUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.message.server.bean.FailPushRecord;
import com.qlk.message.server.bean.PushParams;
import com.qlk.message.server.bean.TaskRecord;
import com.qlk.message.server.dao.mongo.FailPushRecordDao;
import com.qlk.message.server.dao.mongo.TaskMessageDao;
import com.qlk.message.server.dao.mongo.TaskRecordDao;
import com.qlk.message.server.service.IChannelPushService;
import com.qlk.message.server.service.ITaskRecordService;
import com.qlk.message.server.utils.PropertyValueConstants;
import com.tencent.xinge.push.app.PushAppResponse;

/**
 * push发送通知数据
 * @author chenlin
 * @since 1.0.0
 */
@Service
public class TaskRecordServiceImpl implements ITaskRecordService {

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(TaskRecordServiceImpl.class);

    // push转化持久化服务
    @Autowired
    private TaskMessageDao taskMessageDao;
    // puhs发送记录持久化服务
    @Autowired
    private TaskRecordDao taskRecordDao;

    @Autowired
    private FailPushRecordDao failPushRecordDao;

    @Autowired
    @Qualifier("androidPushService")
    private IChannelPushService androidPushService;

    /**
     * 十分钟毫秒数
     */
    private static final long TEN_MINUETE = 10 * 60 * 1000L;

    @Override
    public void removeCompleted() {
        taskRecordDao.removeCompleted();
    }

    @Override
    public void handleStatus() {
        // 查询所有发送完毕的任务
        List<TaskRecord> records = taskRecordDao.findSendNotCompleted();
        for (TaskRecord record : records) {
            Integer imStatus = record.getImStatus();
            if (imStatus != null && imStatus == 2) {
                taskMessageDao.removeMessageByBatchId(record.getBatchId());
                record.setImStatus(3);
                record.setRmRecordDate(new Date());
                taskRecordDao.updateRecord(record);
            }
        }
    }

    @Override
    public void reSendFailedPush(Integer batchSize) {
        /*
         *十分钟前的时间点
         */
        Long tenMinueteBefore = System.currentTimeMillis() - TEN_MINUETE;

        /*
            检测android单个push发送失败记录
         */
        String[] errorCodesArr = PropertyValueConstants.PUSH_SINGLE_ERROR_CODES;
        List<FailPushRecord> singleFailPushRecordList = failPushRecordDao.findFailSinglePushRecordToReSend(tenMinueteBefore,
                null, FailPushRecord.PUSH_PLATFORM_ANDROID, errorCodesArr,batchSize);
        if (singleFailPushRecordList != null && singleFailPushRecordList.size() > 0) {
            for (FailPushRecord failPushRecord : singleFailPushRecordList) {
                try {
                    this.reSendAndroidSinglePush(failPushRecord);
                } catch (BusinessException e) {
                    LOGGER.error("补发失败,failPushRecord:{},e:{}", failPushRecord, e);
                }
            }
        }

        /*
            检测android批量push发送失败记录
         */
        List<FailPushRecord> batchFailPushRecordList = failPushRecordDao.findFailBatchPushRecordToReSend(tenMinueteBefore, null,
                FailPushRecord.PUSH_PLATFORM_ANDROID,batchSize);
        if (batchFailPushRecordList != null && batchFailPushRecordList.size() > 0) {
            for (FailPushRecord failPushRecord : batchFailPushRecordList) {
                try {
                    this.reSendAndroidBatchPush(failPushRecord);
                } catch (BusinessException e) {
                    LOGGER.error("补发失败,failPushRecord:{},e:{}", failPushRecord, e);
                }
            }
        }
    }

    /**
     * 单个android补发
     * @param failPushRecord
     */
    private void reSendAndroidSinglePush(FailPushRecord failPushRecord) throws BusinessException {
        PushParams pushParams = failPushRecord.getPushParams();
        androidPushService.sendSingleDevice(pushParams);
    }

    /**
     * 补发批量失败的android
     * @param failPushRecord
     * @throws BusinessException
     */
    private void reSendAndroidBatchPush(FailPushRecord failPushRecord) throws BusinessException {
        PushParams pushParams = failPushRecord.getPushParams();
        PushAppResponse pushAppResponse = failPushRecord.getPushAppResponse();
        /*
          获取首次发送的 所有token
         */
        ArrayList<String> allDeviceList = failPushRecord.getPushAppRequest().getToken_list();
        /*
            放入一个有编号的 Map
         */
        Map<Integer, String> devicesMap = new HashMap<>();
        for (int i = 0; i < allDeviceList.size(); i++) {
            devicesMap.put(i, allDeviceList.get(i));
        }
        /*
            分离出发送结果编码
         */
        String responseArrStr = pushAppResponse.getResult();
        responseArrStr.substring(responseArrStr.indexOf("[") + 1, responseArrStr.indexOf("]"));
        String[] resultCodesArr = responseArrStr.split(",");
        ArrayList resultCodesList = new ArrayList(Arrays.asList(resultCodesArr));
        /*
         *配置的需要补发的状态码
         */
        ArrayList errorCode = new ArrayList(Arrays.asList(PropertyValueConstants.PUSH_SINGLE_ERROR_CODES));
        ArrayList needRePushTokenList = new ArrayList();
        /*
            遍历返回的状态码，分离出需要补发的token
         */
        for (int i = 0; i < resultCodesList.size(); i++) {
            if (!errorCode.contains(resultCodesList.get(i))) {
                needRePushTokenList.add(devicesMap.get(i));
            }
        }
        if (needRePushTokenList.size() > 0) {
            androidPushService.batchSendDevice(pushParams, needRePushTokenList);
        }
    }
}
