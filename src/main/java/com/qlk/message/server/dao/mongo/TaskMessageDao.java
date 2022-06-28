package com.qlk.message.server.dao.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.qlk.message.server.bean.TaskMessage;
import com.qlk.message.server.utils.PropertyValueConstants;

@Repository
public class TaskMessageDao extends MongoBaseDao {

    /**
     * 消息记录的collectionName
     */
    private static final String COLLECTION_NAME = "m_task_message";

    /**
     * 保存消息
     * MessageDao.save()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2015年6月28日 </P>
     * @param message
     */
    public void saveMessage(List<TaskMessage> message) {
        super.saveBatch(message, COLLECTION_NAME);
    }

    /**
     * 清除android用户的taskMessage消息。ios用户自己在发送完毕，自动清除
     * TaskMessageDao.removeMessageByBatchId()
     * @Author chenlin
     * @Date 2016年3月30日
     * @since 1.0.0
     * @param batchId
     */
    public void removeMessageByBatchId(String batchId) {
        Criteria cri = Criteria.where("batchId").is(batchId).and("user.platType")
                .is(PropertyValueConstants.PLATFORM_ANDRIOD);
        Query query = Query.query(cri);
        super.removeByQuery(query, COLLECTION_NAME);
    }

}
