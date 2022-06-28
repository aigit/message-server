package com.qlk.message.server.listence;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.message.server.bean.TaskRecord;
import com.qlk.message.server.controller.PushController;
import com.qlk.message.server.dao.mongo.TaskMessageDao;
import com.qlk.message.server.dao.mongo.TaskRecordDao;

/**
 * 每30分钟定时清理一次发送完毕的taskMessage信息
 *
 * @author chenlin
 * @since 1.0.0
 */
@Component
public class TaskRecordListener extends Thread implements InitializingBean {

    private final Integer SLEEP_TIME = 30 * 60 * 1000;
    private static final Logger LOGGER = CommonLoggerFactory.getLogger(PushController.class);

    @Autowired
    private TaskMessageDao taskMessageDao;
    @Autowired
    private TaskRecordDao taskRecordDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        // logger.info("=========执行定时批量发送push==============");
        // this.start();
    }

    @Override
    public void run() {
        while (true) {
            LOGGER.info("扫描taskRecord表的状态");
            // 查询所有发送完毕的任务
            List<TaskRecord> records = this.taskRecordDao.findSendNotCompleted();
            for (TaskRecord record : records) {
                Integer imStatus = record.getImStatus();
                if (imStatus != null && imStatus == 2) {
                    this.taskMessageDao.removeMessageByBatchId(record.getBatchId());
                    record.setImStatus(3);
                    record.setRmRecordDate(new Date());
                    this.taskRecordDao.updateRecord(record);
                }
            }
            // 删除已经发送完毕的发送记录
            this.taskRecordDao.removeCompleted();
            // 线程休眠30分钟
            try {
                Thread.sleep(this.SLEEP_TIME);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
