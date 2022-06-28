package com.qlk.message.server.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qlk.baymax.common.exception.PublicService;
import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.message.server.exception.ExceptionCodes;
import com.qlk.message.server.service.IDeviceTokenService;
import com.qlk.message.server.utils.PropertyValueConstants;

/**
 * 客户端相关设备设置
 * @Description
 * @author fangguanhong E-mail:E-mail地址
 * @version 2015-7-21 下午9:06:19 by fangguanhong
 */
@Controller
@RequestMapping("deviceSet")
public class DeviceSetController {

    private static Logger logger = CommonLoggerFactory.getLogger(DeviceSetController.class);

    @Autowired
    private IDeviceTokenService deviceTokenService;

    @RequestMapping("soundSwitch")
    public @ResponseBody String soundSwitch(Long userId, String userType, Integer soundType) {

        if (userId == null) {
            return PublicService.returnValue(ExceptionCodes.PARAM_ERROR);
        }

        if (userType == null) {
            userType = PropertyValueConstants.USER_TYPE_DOCTOR;
        }

        try {
            logger.info("用户信息userId {} userType {} 上传接收push声音 soundType {} ", userId, userType, soundType);
            this.deviceTokenService.soundSwitch(userId, userType, soundType);
        } catch (Exception e) {
            logger.error("设置声音异常", e);
            return PublicService.returnValue(ExceptionCodes.FAILED);
        }
        return PublicService.returnValue(ExceptionCodes.SUCCESS);
    }
}
