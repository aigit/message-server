package com.qlk.message.server.dao.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.qlk.message.server.bean.PushTask;

@Repository
public class PushTaskDao extends MongoBaseDao {

    /**
     * 消息记录的collectionName
     */
    private static final String COLLECTION_NAME = "m_push_task";

    private static final Integer READ_SIZE = 5000;

    /**
     * <查询符合条件的，每次处理5000条>
     * @return List<Message> <返回值描述>
     * @Throws 异常信息
     * @History 2015年7月21日 下午6:03:55 by chenlin
     */
    public List<PushTask> getAndRemovePushTasks(String taskId) {
        Query query = Query.query(Criteria.where("taskId").is(taskId)).limit(READ_SIZE);
        return findAndRemove(query, PushTask.class, COLLECTION_NAME);
    }

    /**
     * <查询符合条件的，每次处理5000条>
     * @return List<Message> <返回值描述>
     * @Throws 异常信息
     * @History 2015年7月21日 下午6:03:55 by chenlin
     */
    public long getCountAndRemovePushTasks(String taskId) {
        Query query = Query.query(Criteria.where("taskId").is(taskId));
        return super.queryCount(query, COLLECTION_NAME);
    }

    /**
     * <查询分页加载数量>
     * @return long <返回值描述>
     * @Throws 异常信息
     * @History 2015年7月22日 上午10:34:58 by chenlin
     */
    public long getPageNum(String taskId) {
        long total = getCountAndRemovePushTasks(taskId);
        long pageSize = total / READ_SIZE;
        if (total % READ_SIZE != 0) {
            pageSize++;
        }
        return pageSize;
    }

    public void saveMessage(List<PushTask> message) {
        super.saveBatch(message, COLLECTION_NAME);
    }

}
