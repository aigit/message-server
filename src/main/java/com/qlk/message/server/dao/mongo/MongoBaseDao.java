package com.qlk.message.server.dao.mongo;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * 基于Spring MongoTemplate的MongoDB操作封装
 * <P>File name : MongoBaseDao.java </P>
 * <P>Author : zhouyanxin </P>
 * <P>Date : 2015年6月28日 </P>
 */
public abstract class MongoBaseDao {

    /**
     * mongodb template
     */
    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * 分页查询
     * MongoBaseDao.find()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2014-10-11 </P>
     * @param query
     * @param entityClass
     * @param collectionName
     * @param page
     * @param num
     * @return
     */
    protected <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
        return mongoTemplate.find(query, entityClass, collectionName);
    }

    /**
     * 查询总数量
     * MongoBaseDao.queryCount()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2014-10-11 </P>
     * @param query
     * @param collectionName
     * @return
     */
    protected long queryCount(Query query, String collectionName) {
        return mongoTemplate.count(query, collectionName);
    }

    /**
     * 保存
     * MongoBaseDao.save()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2014-10-20 </P>
     * @param save
     * @param collectionName
     * @return
     */
    protected <T> void save(T t, String collectionName) {
        mongoTemplate.save(t, collectionName);
    }

    /**
     * 查找全部
     * MongoBaseDao.findAll()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2014-10-20 </P>
     * @param save
     * @param collectionName
     * @return
     */
    protected <T> List<T> findAll(Class<T> entityClass, String collectionName) {
        return mongoTemplate.findAll(entityClass, collectionName);
    }

    /**
     * 保存对象
     * MongoBaseDao.update()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2014-10-20 </P>
     * @param update
     * @param collectionName
     * @return
     */
    protected <T> void update(T o, T n) {
        mongoTemplate.remove(o);
        mongoTemplate.save(n);
    }

    /**
     * MongoBaseDao.update()
     * @Author chenlin
     * @Date 2016年3月30日
     * @since 1.0.0
     * @param o
     */
    protected <T> void update(T o) {
        mongoTemplate.save(o);
    }

    /**
     * 更新对象
     * MongoBaseDao.update()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2014-12-20 </P>
     * @param update
     * @return
     */
    protected <T> void update(Class<T> entityClass, String collectionName, Query query,
            Update update) {
        mongoTemplate.updateFirst(query, update, entityClass, collectionName);
    }

    /**
     * 删除对象
     * MongoBaseDao.remove()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2014-12-01 </P>
     * @param t
     * @param collectionName
     * @return
     */
    protected <T> void remove(T t, String collectionName) {
        this.mongoTemplate.remove(t, collectionName);
    }

    /**
     * 获取并删除满足条件的记录
     * MongoBaseDao.findAndRemove()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2015年7月4日 </P>
     * @param collectionName
     * @param entityClass
     * @param criteria
     * @return
     */
    protected <T> List<T> findAndRemove(Query query, Class<T> entityClass, String collectionName) {
        return mongoTemplate.findAllAndRemove(query, entityClass, collectionName);
    }

    /***
     * 批量插入
     * MongoBaseDao.saveBatch()
     * @Author chenlin
     * @Date 2016年3月30日
     * @since 1.0.0
     * @param batchToSave
     * @param collectionName
     */
    protected <T> void saveBatch(Collection<T> batchToSave, String collectionName) {
        mongoTemplate.insert(batchToSave, collectionName);
    }

    /**
     * 根据查询条件进行删除
     * MongoBaseDao.removeByQuery()
     * @Author chenlin
     * @Date 2016年3月30日
     * @since 1.0.0
     * @param query
     * @param collectionName
     */
    protected void removeByQuery(Query query, String collectionName) {
        this.mongoTemplate.remove(query, collectionName);
    }

}