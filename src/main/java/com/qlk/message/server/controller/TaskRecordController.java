package com.qlk.message.server.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qlk.baymax.common.exception.PublicService;
import com.qlk.baymax.common.exception.ResponseVO;
import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.baymax.common.utils.thread.ThreadPoolUtil;
import com.qlk.message.server.exception.ExceptionCodes;
import com.qlk.message.server.service.ITaskRecordService;
import com.qlk.message.server.service.handler.UserPushHandler;
import com.qlk.message.server.service.impl.TaskRecordServiceImpl;

/**
 * push发送控制类
 * @author chenlin modify by dongiqng
 * @since 1.0.0
 */
@Controller
@RequestMapping("taskRecord")
public class TaskRecordController {

    private static Logger logger = CommonLoggerFactory.getLogger(TaskRecordController.class);

    @Autowired
    private ITaskRecordService taskRecordService;
    @Autowired
    private UserPushHandler userPushHandler;

    /**
     * 每次检索失败记录默认记录数
     */
    private static final Integer FETCH_FAILRECORD_DEFAULT_SIZE=50;

    @RequestMapping("remove")
    public @ResponseBody String remove() {
        // 删除已经发送完毕的发送记录
        try {
            logger.info("start remove completed push task record ...");
            this.taskRecordService.removeCompleted();
            logger.info("end remove completed push task record ...");
        } catch (Exception e) {
            logger.error("delete send push task record error", e);
            return PublicService.returnValue(ExceptionCodes.FAILED);
        }
        return PublicService.returnValue(ExceptionCodes.SUCCESS);
    }

    @RequestMapping("handleStatus")
    public @ResponseBody String handleStatus() {
        try {
            logger.info("start handle im push task status ands remove source push");
            this.taskRecordService.handleStatus();
            logger.info("end handle im push task status ands remove source push");
        } catch (Exception e) {
            logger.error("handle send push task record error", e);
            return PublicService.returnValue(ExceptionCodes.FAILED);
        }
        return PublicService.returnValue(ExceptionCodes.SUCCESS);
    }

    /**
     * 补发,定时任务调用
     * @return
     */
    @RequestMapping("push/resend")
    @ResponseBody
    public ResponseVO<Void> reSendFailedPush(Integer batchSize) {
        final Integer querySize= batchSize==null?FETCH_FAILRECORD_DEFAULT_SIZE:batchSize;
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                /*
                 补发android
                 */
                taskRecordService.reSendFailedPush(querySize);

                /*
                  补发ios
                 */
                userPushHandler.reSendPushByGroup();
            }
        });
        return PublicService.returnResponseVO(ExceptionCodes.SUCCESS);
    }
}
