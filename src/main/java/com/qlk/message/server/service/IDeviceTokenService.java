package com.qlk.message.server.service;

import java.util.List;
import java.util.Set;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.message.server.vo.UserDeviceVo;

/**
 * 移动设备token接口
 * @author modify by dongqing
 */
public interface IDeviceTokenService {

    /**
     * 保存ios客戶端token信息
     * IDeviceTokenService.saveIosToken()
     * @Author chenlin
     * @Date 2016年7月22日
     * @since 1.0.0
     * @param userId
     * @param deviceToken
     * @param userType
     * @param appPlat
     * @param appVers
     * @param model
     * @param operVers
     * @param status
     * @param noticeType
     * @param ip
     * @param loginAddr
     * @param soundType
     * @throws BusinessException
     */
    public void saveIosToken(Long userId, String deviceToken, String userType, String appPlat, String appVers, String model, String operVers,
                             Integer status, Integer noticeType, String ip, String loginAddr, Integer soundType) throws BusinessException;

    /**
     * 保存android客戶端token信息
     * IDeviceTokenService.saveAndroidToken()
     * @Author chenlin
     * @Date 2016年7月22日
     * @since 1.0.0
     * @param userId
     * @param deviceToken
     * @param userType
     * @param appPlat
     * @param appVers
     * @param model
     * @param operVers
     * @param status
     * @param ip
     * @param loginAddr
     * @throws BusinessException
     */
    public void saveAndroidToken(Long userId, String deviceToken, String userType, String appPlat, String appVers, String model, String operVers,
                                 Integer status, String ip, String loginAddr) throws BusinessException;

    /**
     * 客户端声音开关控制
     * @param userId
     * @param userType
     * @param soundType void <返回值描述>
     * @Throws 异常信息
     * @History 2015-7-22 上午11:06:21 by fangguanhong
     */
    public void soundSwitch(Long userId, String userType, Integer soundType) throws BusinessException;

    /**
     * 根据用户id,用户类型,用户设备号删除用户
     * IDeviceTokenService.deleteToken()
     * @Author chenlin
     * @Date 2016年3月29日
     * @since 1.0.0
     * @param userId
     * @param deviceToken
     * @param userType
     */
    public void deleteToken(Long userId, String deviceToken, String userType) throws BusinessException;


    /**
     * 根据用户ID查询用户设备信息
     * @param userId
     * @param userType
     * @return
     */
    UserDeviceVo findUserDevice(Long userId, String userType);

    /**
     * 根据用户集合查询用户的push信息列表
     * @param set
     * @param soundType
     * @param platType
     * @param userType
     * @return
     */
    List<UserDeviceVo> findToPushUsers(Set<String> set, Integer soundType, Integer platType, String userType);

}
