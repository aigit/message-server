package com.qlk.message.server.dao.mongo;

import java.util.List;

import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.qlk.message.server.bean.FailPushRecord;

/**
 * push发送异常记录
 * @author Ldl
 * @date 2018/11/15 13:49
 * @since 1.0.0
 */
@Repository
public class FailPushRecordDao extends MongoBaseDao {

    /**
     * push发送异常记录
     * collectionName
     */
    private static final String COLLECTION_NAME_FAIL_PUSH_RECORD = "m_fail_push_record";

    public void saveFailPushRecord(FailPushRecord failPushRecord) {
        super.save(failPushRecord, COLLECTION_NAME_FAIL_PUSH_RECORD);
    }

    /**
     * 查询出某个时间段内的、未补发过的失败push
     * @param beginTime   截止时间点
     * @param reSendTimes 补发次数
     * @return
     */
    public List<FailPushRecord> findFailBatchPushRecordToReSend(Long beginTime, Integer reSendTimes, Integer platform,Integer batchSize) {
        Criteria criteria = Criteria.where("quantityType")
                                    .is(FailPushRecord.QUANTITY_TYPE_SINGLE)
                                    .and("createAt")
                                    .gt(beginTime)
                                    .and("platform")
                                    .is(platform);
        if (reSendTimes == null) {
            criteria.and("reSendTimes").is(null);
        } else {
            criteria.and("reSendTimes").gt(reSendTimes);
        }
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.asc("createAt"))).limit(batchSize);
        return this.find(query, FailPushRecord.class, COLLECTION_NAME_FAIL_PUSH_RECORD);
    }

    public List<FailPushRecord> findFailSinglePushRecordToReSend(Long beginTime, Integer reSendTimes, Integer platform, String[] errorCodes,Integer
                                                                 batchSize) {
        Criteria criteria = Criteria.where("quantityType")
                                    .is(FailPushRecord.QUANTITY_TYPE_BATCH)
                                    .and("createAt")
                                    .gt(beginTime)
                                    .and("platform")
                                    .is(platform).and("resultCode").in(errorCodes);
        if (reSendTimes == null) {
            criteria.and("reSendTimes").is(null);
        } else {
            criteria.and("reSendTimes").gt(reSendTimes);
        }
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.asc("createAt"))).limit(batchSize);
        return this.find(query, FailPushRecord.class, COLLECTION_NAME_FAIL_PUSH_RECORD);
    }
}
