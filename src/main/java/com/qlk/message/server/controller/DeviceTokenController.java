package com.qlk.message.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.baymax.common.exception.PublicService;
import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.baymax.common.utils.biz.IPSeeker;
import com.qlk.baymax.common.utils.net.IPUtil;
import com.qlk.message.server.exception.ExceptionCodes;
import com.qlk.message.server.service.IDeviceTokenService;
import com.qlk.message.server.utils.PropertyValueConstants;

/**
 * 获取客戶端操作的设备信息
 *
 * @author fangguanhong E-mail: xiyangshen@7lk.com
 * @version 2015年7月21日 下午3:03:50 by fangguanhong
 * @Description 概述
 */
@Controller
@RequestMapping("/deviceToken")
public class DeviceTokenController {

    private static Logger logger = CommonLoggerFactory.getLogger(DeviceTokenController.class);

    @Autowired
    private IDeviceTokenService deviceTokenService;

    /**
     * 添加Android token信息
     * DeviceTokenController.add()
     *
     * @param userId      用户id
     * @param deviceToken 设备token
     * @param userType    用户类型(医生d,患者p)
     * @param appPlat     app平台(phone/ipad)
     * @param appVers     app版本(2.0.1)
     * @param model       机型(小米)
     * @param operVers    操作系统版本(4.5)
     * @param status      状态(0:接收发送,1禁止发送)
     *
     * @return
     *
     * @Author chenlin
     * @Date 2016年4月5日
     * @since 1.0.0
     */
    @RequestMapping("/addAndroid")
    public @ResponseBody
    String add(Long userId, @RequestParam(required = false) String deviceToken, String userType,
               @RequestParam(required = false) String appPlat, @RequestParam(required = false) String appVers,
               @RequestParam(required = false) String model, @RequestParam(required = false) String operVers, Integer status, HttpServletRequest req) {

        if (userId == null) {
            return PublicService.returnValue(ExceptionCodes.PARAM_ERROR);
        }
        if (userType == null) {
            userType = PropertyValueConstants.USER_TYPE_DOCTOR;
        }
        if (status == null) {
            status = 0;
        }

        String ip = IPUtil.getIp(req);
        String loginAddr = IPSeeker.getInstance().getCountry(ip);

        logger.info("andorid上报用户信息userId {} deviceToken {} userType {} appPlat {} appVers {} model {} operVers {} status {} ", userId, deviceToken,
                userType, appPlat, appVers, model, operVers, status);
        try {
            this.deviceTokenService.saveAndroidToken(userId, deviceToken, userType, appPlat, appVers, model, operVers, status, ip, loginAddr);
        } catch (BusinessException e) {
            logger.error("保存设备信息业务异常", e);
            return PublicService.returnValue(ExceptionCodes.FAILED);
        } catch (Exception e) {
            logger.error("保存设备信息系统异常", e);
            return PublicService.returnValue(ExceptionCodes.FAILED);
        }
        return PublicService.returnValue(ExceptionCodes.SUCCESS);
    }

    /**
     * 添加IOS token信息
     * DeviceTokenController.addIos()
     *
     * @param userId      用户id
     * @param deviceToken 设备token
     * @param userType    用户类型(医生d,患者p)
     * @param appPlat     app平台(phone/ipad)
     * @param appVers     app版本(2.0.1)
     * @param model       机型(ipone se)
     * @param operVers    操作系统版本(9.3.1)
     * @param status      状态(0:接收发送,1禁止发送)
     * @param noticeType  通知类型(0生产;1环境)
     *
     * @return
     *
     * @Author chenlin
     * @Date 2016年4月5日
     * @praam soundType
     * @since 1.0.0
     */
    @RequestMapping("addIos")
    public @ResponseBody
    String addIos(Long userId, String deviceToken, String userType, String appPlat, String appVers,
                  @RequestParam(required = false) String model, String operVers, Integer status, Integer noticeType, Integer soundType,
                  HttpServletRequest req) {

        if (userId == null) {
            return PublicService.returnValue(ExceptionCodes.PARAM_ERROR);
        }
        if (deviceToken == null) {
            return PublicService.returnValue(ExceptionCodes.PARAM_ERROR);
        }
        if (userType == null) {
            userType = PropertyValueConstants.USER_TYPE_DOCTOR;
        }
        if (status == null) {
            status = 0;
        }
        if (soundType == null) {
            soundType = PropertyValueConstants.DEVICE_SOUND_OPEN;
        }

        String ip = IPUtil.getIp(req);
        String loginAddr = IPSeeker.getInstance().getCountry(ip);
        logger.info("ios上报用户信息userId {} deviceToken {} userType {} appPlat {} appVers {} model {} operVers {} status {} noticeType{}  soundType {}",
                userId, deviceToken, userType, appPlat, appVers, model, operVers, status, noticeType, soundType);
        try {
            this.deviceTokenService.saveIosToken(userId, deviceToken, userType, appPlat, appVers, model, operVers, status, noticeType, ip, loginAddr,
                    soundType);
        } catch (BusinessException e) {
            logger.error("保存设置设备业务异常");
            return PublicService.returnValue(ExceptionCodes.FAILED);
        } catch (Exception e) {
            logger.error("保存设备信息系统异常", e);
            return PublicService.returnValue(ExceptionCodes.FAILED);
        }
        return PublicService.returnValue(ExceptionCodes.SUCCESS);
    }

    /**
     * 删除用户的token
     * DeviceTokenController.delToken()
     *
     * @param userId      用户id
     * @param userType    用户类型(医生d,患者p)
     * @param deviceToken 设备token
     *
     * @return
     *
     * @Author chenlin
     * @Date 2016年4月1日
     * @since 1.0.0
     */
    @RequestMapping("delDevice")
    public @ResponseBody
    String delToken(Long userId, String userType, String deviceToken) {

        logger.info("上报删除用户信息 userId {}  userType {} deviceToken {}  ", userId, userType, deviceToken);
        if (userId == null || deviceToken == null) {
            return PublicService.returnValue(ExceptionCodes.PARAM_ERROR);
        }
        try {
            this.deviceTokenService.deleteToken(userId, deviceToken, userType);
        } catch (BusinessException e) {
            logger.error("删除设备信息业务异常");
            return PublicService.returnValue(ExceptionCodes.FAILED);
        } catch (Exception e) {
            logger.error("删除设备信息系统异常", e);
            return PublicService.returnValue(ExceptionCodes.FAILED);
        }
        return PublicService.returnValue(ExceptionCodes.SUCCESS);
    }
}
