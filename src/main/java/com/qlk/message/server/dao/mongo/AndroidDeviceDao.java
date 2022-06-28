package com.qlk.message.server.dao.mongo;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.qlk.message.server.bean.AndroidDevice;
import com.qlk.message.server.utils.PropertyValueConstants;

/**
 * 用户处理用户设备持久化的接口，包含android设备信息的获取，用户声音的控制
 * @Description
 * @author fangguanhong E-mail:E-mail地址
 * @version 2015-7-21 下午7:54:37 by fangguanhong
 */
@Repository
public class AndroidDeviceDao extends MongoBaseDao {

    /**
     * 消息记录的collectionName--医生
     */
    private static final String COLLECTION_NAME = "m_androidevice";
    /**
     * 消息记录的collectionName--患者
     */
    private static final String COLLECTION_PT_NAME = "m_pt_androidevice";

    public static final String getColletionName(AndroidDevice androidDevice) {
        if (PropertyValueConstants.USER_TYPE_DOCTOR.equals(androidDevice.getUserType())) {
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
     * 新增
     * @param androidDevice void <返回值描述>
     * @Throws 异常信息
     * @History 2015-7-21 下午8:04:32 by fangguanhong
     */
    public void saveOrUpdate(AndroidDevice androidDevice) {
        super.save(androidDevice, getColletionName(androidDevice));
    }

    /**
     * 更新
     * @param androidDeviceO
     * @param androidDeviceN void <返回值描述>
     * @Throws 异常信息
     * @History 2015-7-22 上午10:55:27 by fangguanhong
     */
    public void update(AndroidDevice androidDevice) {
        Criteria cri = Criteria.where("userId").is(androidDevice.getUserId()).and("userType").is(androidDevice.getUserType());
        Query query = Query.query(cri);
        Update update = new Update().set("soundType", androidDevice.getSoundType()).set("changedAt", androidDevice.getChangedAt());
        this.mongoTemplate.updateFirst(query, update, getColletionName(androidDevice));
    }

    /**
     * 根据用户类型查询数据
     * @param userId
     * @return AndroidDevice <返回值描述>
     * @Throws 异常信息
     * @History 2015-7-21 下午9:17:17 by fangguanhong
     */
    public AndroidDevice findDeviceByUserType(Long userId, String userType) {
        Criteria cri = Criteria.where("userId").is(userId).and("userType").is(userType);
        Query query = Query.query(cri);
        List<AndroidDevice> deviceList = this.mongoTemplate.find(query, AndroidDevice.class, getColletionName(userType));
        if (null != deviceList && deviceList.size() > 0) {
            return deviceList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据用户ids及用户类型查询Android用户列表
     * AndroidDeviceDao.findDeviceByUsersType()
     * @Author Ldl
     * @Date 2017年11月30日
     * @since 1.0.0
     * @param userIdList
     * @param userType
     * @return
     */
    public List<AndroidDevice> findDeviceByUsersType(Set<Long> userIdSet, Integer soundType, String userType) {
        Criteria cri = Criteria.where("userId").in(userIdSet).and("userType").is(userType);
        if (soundType != null) {
            cri.and("soundType").is(soundType);
        }
        Query query = Query.query(cri);
        List<AndroidDevice> deviceList = this.mongoTemplate.find(query, AndroidDevice.class, getColletionName(userType));
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
     * AndroidDeviceDao.delDeviceByUserId()
     * @Author chenlin
     * @Date 2016年5月16日
     * @since 1.0.0
     * @param userId
     * @param userType
     * @param deviceToken
     */
    public void delDeviceByUserId(Long userId, String userType) {
        Criteria cri = Criteria.where("userId").is(userId).and("userType").is(userType);
        Query query = Query.query(cri);
        this.mongoTemplate.remove(query, getColletionName(userType));
    }

}
