package com.qlk.message.server.dao.mongo;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.qlk.message.server.bean.IosDevice;
import com.qlk.message.server.utils.PropertyValueConstants;

/**
 * 用户处理用户设备持久化的接口，包含ios设备信息的获取，用户声音的控制
 * @Description 概述
 * @author chenlin E-mail: jingmo@7lk.com
 * @version 2015年7月21日 上午9:49:06 by chenlin
 */
@Repository
public class IosDeviceDao extends MongoBaseDao {

    /**
     * 消息记录的collectionName--医生
     */
    private static final String COLLECTION_NAME = "m_iosdevice";
    /**
     * 消息记录的collectionName--患者
     */
    private static final String COLLECTION_PT_NAME = "m_pt_iosdevice";

    public static final String getColletionName(IosDevice iosDevice) {
        if (PropertyValueConstants.USER_TYPE_DOCTOR.equals(iosDevice.getUserType())) {
            return COLLECTION_NAME;
        } else {
            return COLLECTION_PT_NAME;
        }
    }

    public static final String getColletionName(String userType) {
        if (PropertyValueConstants.USER_TYPE_DOCTOR.equals(userType)) {
            return COLLECTION_NAME;
        } else {
            return COLLECTION_PT_NAME;
        }
    }

    /**
     * 保存数据
     * @param iosDevice void <返回值描述>
     * @Throws 异常信息
     * @History 2015-7-21 下午8:05:41 by fangguanhong
     */
    public void saveOrUpdate(IosDevice iosDevice) {
        super.save(iosDevice, getColletionName(iosDevice));
    }

    /**
     * 更新数据
     * @param iosDevice void <返回值描述>
     * @Throws 异常信息
     * @History 2015-7-21 下午10:08:32 by fangguanhong
     */
    public void update(IosDevice iosDevice) {
        Criteria cri = Criteria.where("userId").is(iosDevice.getUserId()).and("userType").is(iosDevice.getUserType());
        Query query = Query.query(cri);
        Update update = new Update().set("soundType", iosDevice.getSoundType()).set("changedAt", iosDevice.getChangedAt());
        this.mongoTemplate.updateFirst(query, update, getColletionName(iosDevice));
    }

    public IosDevice findDeviceByToken(String deviceToken, String userType) {
        Criteria cri = Criteria.where("deviceToken").is(deviceToken);
        Query query = Query.query(cri);
        List<IosDevice> deviceList = this.mongoTemplate.find(query, IosDevice.class, getColletionName(userType));
        if (null != deviceList && deviceList.size() > 0) {
            return deviceList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据用户ID以及用户类型查找数据列表
     * @param userId
     * @return IosDevice <返回值描述>
     * @Throws 异常信息
     * @History 2015-7-21 下午8:06:02 by fangguanhong
     */
    public IosDevice findDeviceByUserType(Long userId, String userType) {
        Criteria cri = Criteria.where("userId").is(userId).and("userType").is(userType);
        Query query = Query.query(cri);
        List<IosDevice> deviceList = this.mongoTemplate.find(query, IosDevice.class, getColletionName(userType));
        if (null != deviceList && deviceList.size() > 0) {
            return deviceList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据用户 id列表及用户类型查询ios用户设备列表
     * IosDeviceDao.findDeviceByUserIds()
     * @Author Ldl
     * @Date 2017年11月30日
     * @since 1.0.0
     * @param userIdList
     * @param userType
     * @return
     */
    public List<IosDevice> findDeviceByUserIds(Set<Long> userIdList, Integer soundType, String userType) {
        Criteria cri = Criteria.where("userId").in(userIdList).and("userType").is(userType).and("soundType").is(soundType);
        Query query = Query.query(cri);
        List<IosDevice> deviceList = this.mongoTemplate.find(query, IosDevice.class, getColletionName(userType));
        return deviceList;
    }

    /**
     * 根据用户ID和设备信息删除记录
     * @param userId void <返回值描述>
     * @Throws 异常信息
     * @History 2015-7-21 下午8:22:57 by fangguanhong
     */
    public void delDeviceByUserIdAndDevice(Long userId, String userType, String deviceToken) {
        Criteria cri = Criteria.where("userId").is(userId).and("userType").is(userType).and("deviceToken").is(deviceToken);
        Query query = Query.query(cri);
        this.mongoTemplate.remove(query, getColletionName(userType));
    }

    /**
     * 根据用户id删除记录
     * IosDeviceDao.delDeviceByUserId()
     * @Author chenlin
     * @Date 2016年5月16日
     * @since 1.0.0
     * @param userId
     * @param userType
     */
    public void delDeviceByUserId(Long userId, String userType) {
        Criteria cri = Criteria.where("userId").is(userId).and("userType").is(userType);
        Query query = Query.query(cri);
        this.mongoTemplate.remove(query, getColletionName(userType));
    }
}
