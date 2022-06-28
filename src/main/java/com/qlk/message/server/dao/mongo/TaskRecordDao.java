package com.qlk.message.server.dao.mongo;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.qlk.message.server.bean.TaskRecord;

@Repository
public class TaskRecordDao extends MongoBaseDao {
    /**
     * 消息记录的collectionName
     */
    private static final String COLLECTION_NAME = "m_task_record";
    /**
     * 已经完成的push发送记录，保存20天
     */
    private static final Integer COMPLETED_SAVE_DAY = 20;

    public void saveRecord(TaskRecord record) {
        super.save(record, COLLECTION_NAME);
    }

    public void updateRecord(TaskRecord record) {
        super.save(record, COLLECTION_NAME);
    }

    public List<TaskRecord> findSendNotCompleted() {
        Criteria cri = Criteria.where("imStatus").lt(3);
        Query query = Query.query(cri);
        return super.find(query, TaskRecord.class, COLLECTION_NAME);
    }

    public void removeCompleted() {
        long time = COMPLETED_SAVE_DAY * 24 * 60 * 60 * 1000L;
        long now = System.currentTimeMillis();
        Criteria cri = Criteria.where("createdAt").lt(new Date(now - time)).and("imStatus").is(3);
        Query query = Query.query(cri);
        this.mongoTemplate.remove(query, COLLECTION_NAME);
    }
}
