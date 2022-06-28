package com.qlk.message.server.dao.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.qlk.message.server.bean.PushBatch;

@Repository
public class PushBatchDao extends MongoBaseDao {

    /**
     * 消息记录的collectionName
     */
    private static final String COLLECTION_NAME = "m_push_batch";

    private static final Integer READ_SIZE = 5000;

    /**
     * <查询符合条件的，每次处理5000条>
     * @return List<Message> <返回值描述>
     * @Throws 异常信息
     * @History 2015年7月21日 下午6:03:55 by chenlin
     */
    public List<PushBatch> getAndRemovePushBatch(String batchId) {
        Query query = Query.query(Criteria.where("batchId").is(batchId)).limit(READ_SIZE);
        return findAndRemove(query, PushBatch.class, COLLECTION_NAME);
    }

    /**
     * <查询符合条件的，每次处理5000条>
     * @return List<Message> <返回值描述>
     * @Throws 异常信息
     * @History 2015年7月21日 下午6:03:55 by chenlin
     */
    public long getCountAndRemovePushBatch(String batchId) {
        Query query = Query.query(Criteria.where("batchId").is(batchId));
        return super.queryCount(query, COLLECTION_NAME);
    }

    /**
     * <查询分页加载数量>
     * @return long <返回值描述>
     * @Throws 异常信息
     * @History 2015年7月22日 上午10:34:58 by chenlin
     */
    public long getPageNum(String batchId) {
        long total = getCountAndRemovePushBatch(batchId);
        long pageSize = total / READ_SIZE;
        if (total % READ_SIZE != 0) {
            pageSize++;
        }
        return pageSize;
    }

    public void saveMessage(List<PushBatch> message) {
        super.saveBatch(message, COLLECTION_NAME);
    }

}
