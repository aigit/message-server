package com.qlk.message.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.message.server.bean.AndroidDevice;
import com.qlk.message.server.bean.IosDevice;
import com.qlk.message.server.dao.mongo.AndroidDeviceDao;
import com.qlk.message.server.dao.mongo.IosDeviceDao;
import com.qlk.message.server.service.IDeviceTokenService;
import com.qlk.message.server.utils.PropertyValueConstants;
import com.qlk.message.server.vo.UserDeviceVo;

/**
 * 用户设备信息
 * @author Ldl
 */
@Service
public class DeviceTokenServiceImpl implements IDeviceTokenService {

    @Autowired
    private IosDeviceDao iosDeviceDao;
    @Autowired
    private AndroidDeviceDao androidDeviceDao;

    /**
     * 保存IOS客戶端token信息
     */
    @Override
    public void saveIosToken(Long userId, String token, String userType, String appPlatform, String appVersion, String model, String operateVersion,
                             Integer status, Integer noticeType, String ip, String loginAddr, Integer soundType) throws BusinessException {
        // 刪除对立客户端该用户数据
        AndroidDevice androidDevic = androidDeviceDao.findDeviceByUserType(userId, userType);
        if (null != androidDevic) {
            androidDeviceDao.delDeviceByUserId(userId, userType);
        }

        // 通过token查看是否已经存在，如果存在则修改
        IosDevice iosDevice = iosDeviceDao.findDeviceByUserType(userId, userType);
        if (null == iosDevice) {
            iosDevice = iosDeviceDao.findDeviceByToken(token, userType);
            if (iosDevice == null) {
                iosDevice = new IosDevice();
                iosDevice.setCreatedAt(new Date());
                // 用户类型
                iosDevice.setUserType(userType);
            }
        } else {
            iosDevice.setChangedAt(new Date());
        }
        // 状态
        iosDevice.setStatus(status);
        // 设置用户
        iosDevice.setUserId(userId);
        // 设置号
        iosDevice.setDeviceToken(token);
        // 应用版本
        iosDevice.setAppVersion(appVersion);
        // 应用平台
        iosDevice.setAppPlatform(appPlatform);
        // 操作系统版本
        iosDevice.setOperateVersion(operateVersion);
        // 通知类型：0生产，1环境
        iosDevice.setNoticeType(noticeType);
        // 设置机型
        iosDevice.setModel(model);
        // IP
        iosDevice.setIp(ip);
        // 登录地址
        iosDevice.setLoginAddr(loginAddr);
        // 声音开关类型(0,开；1,关)
        iosDevice.setSoundType(soundType);
        iosDeviceDao.saveOrUpdate(iosDevice);
    }

    /**
     * 保存android客户端token信息
     */
    @Override
    public void saveAndroidToken(Long userId, String deviceToken, String userType, String appPlatform, String appVersion, String model,
                                 String operateVersion, Integer status, String ip, String loginAddr) throws BusinessException {
        // 删除对立用户客户端该用户数据
        IosDevice iosDevic = iosDeviceDao.findDeviceByUserType(userId, userType);
        if (null != iosDevic) {
            iosDeviceDao.delDeviceByUserId(userId, userType);
        }

        AndroidDevice androidDevice = androidDeviceDao.findDeviceByUserType(userId, userType);
        if (null == androidDevice) {
            androidDevice = new AndroidDevice();
            androidDevice.setCreatedAt(new Date());
            // 用户类型
            androidDevice.setUserType(userType);
            // 声音开关类型(0,开；1,关)
            androidDevice.setSoundType(PropertyValueConstants.DEVICE_SOUND_OPEN);
        } else {
            androidDevice.setChangedAt(new Date());
        }
        // 设置状态
        androidDevice.setStatus(status);
        // 设置用户
        androidDevice.setUserId(userId);
        // 设置号
        androidDevice.setDeviceToken(deviceToken);
        // 应用版本
        androidDevice.setAppVersion(appVersion);
        // 应用平台
        androidDevice.setAppPlatform(appPlatform);
        // 操作系统版本
        androidDevice.setOperateVersion(operateVersion);
        // 设置机型
        androidDevice.setModel(model);
        // IP
        androidDevice.setIp(ip);
        // 登录地址
        androidDevice.setLoginAddr(loginAddr);
        androidDeviceDao.saveOrUpdate(androidDevice);
    }

    /**
     * 客户端声音开关
     * @param userId
     * @param userType
     * @param soundType void <返回值描述>
     * @Throws 异常信息
     * @History 2015-7-21 下午9:24:07 by fangguanhong
     */
    @Override
    public void soundSwitch(Long userId, String userType, Integer soundType) throws BusinessException {
        IosDevice iosDevice = iosDeviceDao.findDeviceByUserType(userId, userType);
        if (null != iosDevice) {
            iosDevice.setSoundType(soundType);
            // 更新声音开关
            iosDevice.setChangedAt(new Date());
            iosDeviceDao.update(iosDevice);
        } else {
            AndroidDevice androidDevice = androidDeviceDao.findDeviceByUserType(userId, userType);
            if (null != androidDevice) {
                androidDevice.setSoundType(soundType);
                androidDevice.setChangedAt(new Date());
                // 更新声音开关
                androidDeviceDao.update(androidDevice);
            }
        }
    }

    @Override
    public void deleteToken(Long userId, String deviceToken, String userType) throws BusinessException {
        iosDeviceDao.delDeviceByUserIdAndDevice(userId, userType, deviceToken);
        androidDeviceDao.delDeviceByUserIdAndDevice(userId, userType, deviceToken);
    }

    @Override
    public UserDeviceVo findUserDevice(Long userId, String userType) {
        UserDeviceVo userDeviceVo = new UserDeviceVo();
        /*
         根据用户id和用户类型,查询android用户数据
          */
        AndroidDevice device = this.androidDeviceDao.findDeviceByUserType(userId, userType);
        if (device != null) {
            userDeviceVo.setUserId(userId);
            userDeviceVo.setPlatType(PropertyValueConstants.PLATFORM_ANDRIOD);
            userDeviceVo.setTopic("1");
            userDeviceVo.setStatus(device.getStatus());
            Integer sound = device.getSoundType();
            userDeviceVo.setSound(sound == null ? null : sound.toString());
            userDeviceVo.setToken(device.getDeviceToken());
            return userDeviceVo;
        } else {
            /*
            查询IOS用户设备信息
             */
            IosDevice iosDevice = this.iosDeviceDao.findDeviceByUserType(userId, userType);
            if (iosDevice != null) {
                userDeviceVo.setUserId(userId);
                userDeviceVo.setPlatType(PropertyValueConstants.PLATFORM_IOS);
                userDeviceVo.setToken(iosDevice.getDeviceToken());
                userDeviceVo.setStatus(iosDevice.getStatus());
                Integer sound = iosDevice.getSoundType();
                userDeviceVo.setSound(sound == PropertyValueConstants.DEVICE_SOUND_OPEN ? "default" : "");
                return userDeviceVo;
            }
        }
        return null;
    }

    /**
     * 从设备信息中按照参数条件查询出待push的用户设备列表
     * @param set       用户Id集合
     * @param soundType 提示音类型
     * @param platType  所在设备平台类型
     * @param userType  用户类型 d-医生,p-患者
     * @return List<UserDeviceVo>
     */
    @Override
    public List<UserDeviceVo> findToPushUsers(Set<String> set, Integer soundType, Integer platType, String userType) {
        List<UserDeviceVo> userDeviceList = new ArrayList<>();
        if (PropertyValueConstants.PLATFORM_ANDRIOD.equals(platType)) {
            Set<Long> ids = new HashSet<>();
            for (String userId : set) {
                ids.add(Long.parseLong(userId));
            }
            List<AndroidDevice> androidUserList = this.androidDeviceDao.findDeviceByUsersType(ids, soundType, userType);
            if (androidUserList != null && androidUserList.size() > 0) {
                for (AndroidDevice androidDevice : androidUserList) {
                    UserDeviceVo userDeviceVo = new UserDeviceVo();
                    userDeviceVo.setUserId(androidDevice.getUserId());
                    userDeviceVo.setStatus(androidDevice.getStatus());
                    userDeviceVo.setToken(androidDevice.getDeviceToken());
                    userDeviceList.add(userDeviceVo);
                }
            }
        }
        if (PropertyValueConstants.PLATFORM_IOS.equals(platType)) {
            Set<Long> ids = new HashSet<>();
            for (String userId : set) {
                ids.add(Long.parseLong(userId));
            }
            List<IosDevice> iosUserList = this.iosDeviceDao.findDeviceByUserIds(ids, soundType, userType);
            if (iosUserList != null && iosUserList.size() > 0) {
                for (IosDevice iosDevice : iosUserList) {
                    UserDeviceVo userDeviceVo = new UserDeviceVo();
                    userDeviceVo.setUserId(iosDevice.getUserId());
                    userDeviceVo.setToken(iosDevice.getDeviceToken());
                    userDeviceVo.setStatus(iosDevice.getStatus());
                    userDeviceList.add(userDeviceVo);
                }
            }
        }
        return userDeviceList;
    }
}
