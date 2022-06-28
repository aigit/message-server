package com.qlk.message.server.listence;

import org.slf4j.Logger;

import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.message.server.service.IUserPushService;

public class TaskPush extends Thread {
    private static final Logger LOGGER = CommonLoggerFactory.getLogger(TaskPush.class);
    private String taskId;
    private IUserPushService pushService;

    public TaskPush(String taskId, IUserPushService pushService) {
        this.taskId = taskId;
        this.pushService = pushService;
    }

    @Override
    public void run() {
        try {
            pushService.saveTaskPush(taskId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
