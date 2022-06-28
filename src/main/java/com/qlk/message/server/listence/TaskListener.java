package com.qlk.message.server.listence;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.message.server.service.IUserPushService;
import com.qlk.message.server.utils.DateUtil;
import com.qlk.message.server.vo.TaskVo;

/**
 * 客户端状态监听类
 * 改类主要的功能是监控客户端的连接、关闭、意外关闭、登出等事件类型
 * 并根据相关的类型进行相应操作
 * <P>File name : ClientListener.java </P>
 * <P>Author : zhouyanxin </P>
 * <P>Date : 2015年6月24日 </P>
 */
@Component
public class TaskListener extends Thread implements InitializingBean {
    private static final Logger LOGGER = CommonLoggerFactory.getLogger(TaskListener.class);

    private final Integer SLEEP_TIME = 60 * 1000;

    private static final String FILE_PATH = "/config/push_task.xml";

    @Resource(name = "doctorService")
    private IUserPushService pushService;

    @Override
    public void afterPropertiesSet() throws Exception {
        // if (scanTask) {
        // logger.info("=========执行定时批量发送push==============");
        // this.start();
        // }
    }

    @Override
    public void run() {
        while (true) {
            LOGGER.info("扫描task配置文件");
            // 加载配置文件，判读是否有自动任务需要执行
            List<String> result = this.readAndExceuteTask();
            LOGGER.info("需要执行的task配置文件" + result);
            // 启动线程
            for (final String taskId : result) {
                new TaskPush(taskId, this.pushService).start();
            }

            // 启动任务线程处理数据
            try {
                Thread.sleep(this.SLEEP_TIME);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * <加载task配置文件，判断是否有task需要执行>
     *
     * @return boolean <返回值描述>
     *
     * @Throws 异常信息
     * @History 2015年7月21日 下午2:46:22 by chenlin
     */
    public List<String> readAndExceuteTask() {
        long now = System.currentTimeMillis();
        List<String> result = new ArrayList<>();
        List<TaskVo> taskList = this.readTaskXml();
        for (TaskVo vo : taskList) {
            String sendTime = vo.getSendTime();
            if (!this.excuteTask(now, sendTime)) {
                continue;
            }
            result.add(String.valueOf(vo.getId()));
        }
        return result;
    }

    /**
     * <加载task配置文件>
     *
     * @return boolean <返回值描述>
     *
     * @Throws 异常信息
     * @History 2015年7月21日 下午2:46:22 by chenlin
     */
    public List<TaskVo> readTaskXml() {
        List<TaskVo> result = new ArrayList<>();
        // 解析XML
        Document doc;
        SAXBuilder builder = new SAXBuilder();
        try {
            doc = builder.build(TaskListener.class.getResourceAsStream(FILE_PATH));
            Element root = doc.getRootElement();
            List<Element> codes = root.getChildren("task");
            Iterator<Element> iter = codes.iterator();
            while (iter.hasNext()) {
                Element task = iter.next();
                TaskVo vo = new TaskVo();
                vo.setId(Integer.parseInt(task.getChild("id").getText()));
                vo.setSendTime(task.getChild("sendTime").getValue());
                vo.setContent(task.getChild("content").getValue());
                vo.setLinkUrl(task.getChild("linkUrl").getText());
                result.add(vo);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    private boolean excuteTask(long now, String sendTime) {
        String dateTime = DateUtil.getCurrentFormatDate();
        String date = dateTime + " " + sendTime;
        Date sendDate = DateUtil.parseStrToDateTime(date);
        long send = sendDate.getTime();
        if (send > now && send - now <= this.SLEEP_TIME) {
            return true;
        }
        return false;
    }

}
