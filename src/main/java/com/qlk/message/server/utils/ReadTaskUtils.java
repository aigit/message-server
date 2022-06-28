package com.qlk.message.server.utils;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;

import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.message.server.listence.TaskListener;
import com.qlk.message.server.vo.TaskVo;

/**
 * 读取task.xml文件中的任务
 *
 * @author chenlin
 * @since 1.0.0
 */
public class ReadTaskUtils {
    private static final Logger LOGGER = CommonLoggerFactory.getLogger(ReadTaskUtils.class);

    private static final String FILE_PATH = "/config/push_task.xml";

    public static List<TaskVo> readTaskXml() {
        List<TaskVo> result = new ArrayList<>();
        // 解析XML
        Document doc;
        SAXBuilder builder = new SAXBuilder();
        try {
            doc = builder.build(TaskListener.class.getResourceAsStream(FILE_PATH));
            Element root = doc.getRootElement();
            List<Element> codes = root.getChildren("task");
            for (Element task : codes) {
                TaskVo vo = new TaskVo();
                vo.setId(Integer.parseInt(task.getChild("id").getText()));
                vo.setSendTime(task.getChild("sendTime").getValue());
                vo.setContent(task.getChild("content").getValue());
                vo.setLinkUrl(task.getChild("linktype").getText());
                result.add(vo);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

}
