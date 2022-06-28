package com.qlk.message.server.service.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.baymax.common.redis.RedisUtil;
import com.qlk.message.server.bean.AndroidDevice;
import com.qlk.message.server.bean.IosDevice;
import com.qlk.message.server.bean.PushParams;
import com.qlk.message.server.dao.mongo.AndroidDeviceDao;
import com.qlk.message.server.dao.mongo.IosDeviceDao;
import com.qlk.message.server.service.IUserPushService;
import com.qlk.message.server.utils.PropertyValueConstants;
import com.qlk.message.server.vo.GroupMessageVO;
import com.qlk.message.server.vo.UserDeviceVo;

/**
 * 推送策略入口
 * @author Ldl
 * @date 2018/11/13 17:06
 * @since 1.0.0
 */
@Component
public class UserPushHandler {

    private static final String USER_SERVICE_NAME_PATIENT = "patientService";
    private static final String USER_SERVICE_NAME_DOCTOR = "doctorService";

    @Autowired
    private IosDeviceDao iosDeviceDao;
    @Autowired
    private AndroidDeviceDao androidDeviceDao;

    private final Map<String, IUserPushService> pushServiceMap = new ConcurrentHashMap<>(5);

    @Autowired
    public UserPushHandler(Map<String, IUserPushService> pushServiceMap) {
        this.pushServiceMap.clear();
        pushServiceMap.forEach((k, v) -> {
            switch (k) {
                case USER_SERVICE_NAME_PATIENT:
                    this.pushServiceMap.put(PropertyValueConstants.USER_TYPE_PATIENT, v);
                    break;
                case USER_SERVICE_NAME_DOCTOR:
                    this.pushServiceMap.put(PropertyValueConstants.USER_TYPE_DOCTOR, v);
                    break;
                default:
                    break;
            }

        });
    }

    /**
     * 发送单个设备
     * @param userType
     * @param pushParams
     * @throws BusinessException
     */
    public void sendSinglePushMessage(String userType, PushParams pushParams) throws BusinessException {
        pushServiceMap.get(userType).sendSinglePushMessage(pushParams);
    }


    /**
     * 向指定类型的所用用户发送push
     * @param userType
     * @param message
     * @param linkUrl
     * @throws BusinessException
     * @Author chenlin
     * @Date 2016年4月5日
     * @see IUserPushService#sendMultiPushMessage(java.lang.String,
     * java.lang.String, java.lang.String)
     * @since 1.0.0
     */
    public void pushToAllUser(String userType, String message, String linkUrl) throws BusinessException {
        pushServiceMap.get(userType).pushToAllUser(message, linkUrl);
    }

    /**
     * 向批量用户发送Push
     * IUserPushService.batchPushOnPrivate()
     * @param userType
     * @param message
     * @param linkUrl
     * @param recipientCacheTopushKey 标签分组下用户的缓存ids
     * @Author Ldl
     * @Date 2017年11月29日
     * @since 1.0.0
     */
    public void batchPushByGroup(String userType, String message, String linkUrl, String recipientCacheTopushKey) throws BusinessException {
        pushServiceMap.get(userType).batchPushByGroup(message, linkUrl, recipientCacheTopushKey);
    }

    /**
     * 补发标签push
     */
    public void reSendPushByGroup() {
        pushServiceMap.get(PropertyValueConstants.USER_TYPE_DOCTOR).reSendPushByGroup();
    }

    /**
     * 从缓存中查询出待发送的push
     * @return
     */
    public GroupMessageVO getGroupMessageInCache() {
        return null;
    }

}
