package com.qlk.message.server.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.baymax.common.exception.PublicService;
import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.message.server.bean.PushParams;
import com.qlk.message.server.exception.ExceptionCodes;
import com.qlk.message.server.service.handler.UserPushHandler;
import com.qlk.message.server.vo.GroupMessageVO;

/**
 * push发送控制类
 *
 * @author chenlin
 * @since 1.0.0
 */
@Controller
@RequestMapping("push")
public class PushController {

    private static Logger logger = CommonLoggerFactory.getLogger(PushController.class);

    @Autowired
    private UserPushHandler userPushHandler;


    /**
     * 实时发送用户请求的push
     * PushController.pushMessage()
     *
     * @param userId      用户id
     * @param userType    用户类型
     * @param pushMessage 消息内容
     * @param linkUrl     跳转链接
     *
     * @return
     *
     * @Author chenlin
     * @Date 2015年7月22日
     * @since 1.0.0
     */
    @RequestMapping("pushMessage")
    public @ResponseBody
    String pushMessage(Long userId, String userType, String pushMessage, String linkUrl) {

        if (userId == null || userType == null || pushMessage == null) {
            logger.info("调用实时发送push参数有空值,userId=" + userId + ",userType=" + userType + ",pushMessage=" + pushMessage);
            return PublicService.returnValue(ExceptionCodes.PARAM_ERROR);
        }
        logger.info("excute send pushMessage,where userId {} userType  {} pushMessage {}  linkUrl {}", userId, userType, pushMessage, linkUrl);
        // 请求服务端发送push消息ni
        try {
            pushMessage = pushMessage.replaceAll("<a href[^>]*>", "");
            pushMessage = pushMessage.replaceAll("</a>", "");
            PushParams pushParams = new PushParams(userType, null, pushMessage, linkUrl, userId, null);
            userPushHandler.sendSinglePushMessage(userType, pushParams);
        } catch (BusinessException e) {
            logger.info("调用实时发送push业务异常，参数值为userId=" + userId + ",userType=" + userType + ",pushMessage=" + pushMessage + ",异常为:" + e.getMessage());
            return PublicService.returnValue(e.getCode());
        } catch (Exception e) {
            logger.error("调用实时发送push出现系统异常，参数值为userId=" + userId + ",userType=" + userType + ",pushMessage=" + pushMessage + ",异常为:"
                    + e.getMessage(), e);
            return PublicService.returnValue(ExceptionCodes.FAILED);
        }
        return PublicService.returnValue(ExceptionCodes.SUCCESS);
    }

    /**
     * 所有用户发送相同内容push
     * PushController.multiPush()
     * @param userType    用户类型
     * @param pushMessage 推送内容
     * @param linkUrl     push跳转地址
     * @return
     * @Author chenlin
     * @Date 2016年3月29日
     * @since 1.0.0
     */
    @ResponseBody
    @RequestMapping("multiPush")
    public String pushAll(String userType, String pushMessage, String linkUrl) {
        if (userType == null || pushMessage == null) {
            logger.info("调用实时所有用户发送push参数有空值,userType=" + userType + ",pushMessage=" + pushMessage);
            return PublicService.returnValue(ExceptionCodes.PARAM_ERROR);
        }
        logger.info("excute send multPush,where userType  {} pushMessage {}  linkUrl {} ", userType, pushMessage, linkUrl);
        // 请求服务端发送push消息
        try {
            pushMessage = pushMessage.replaceAll("<a href[^>]*>", "");
            pushMessage = pushMessage.replaceAll("</a>", "");
            userPushHandler.pushToAllUser(userType, pushMessage, linkUrl);
        } catch (BusinessException e) {
            logger.info("调用实时所有用户发送push业务异常，参数值为userType=" + userType + ",pushMessage=" + pushMessage + ",异常为:" + e.getMessage());
            return PublicService.returnValue(e.getCode());
        } catch (Exception e) {
            logger.info("调用实时所有用户发送push出现系统异常，参数值为userType=" + userType + ",pushMessage=" + pushMessage + ",异常为:" + e.getMessage());
            return PublicService.returnValue(ExceptionCodes.FAILED);
        }
        return PublicService.returnValue(ExceptionCodes.SUCCESS);
    }

    @RequestMapping("ingroup")
    @ResponseBody
    public String pushByGroup(String userType, String pushMessage, String linkUrl, String recipientCacheTopushKey) {
        logger.info("按标签分组向用户发送push,userType=" + userType + ",pushMessage=" + pushMessage + ",recipientCacheTopushKey=" + recipientCacheTopushKey);
        if (userType == null || StringUtils.isEmpty(pushMessage) || StringUtils.isEmpty(recipientCacheTopushKey)) {
            return PublicService.returnValue(ExceptionCodes.PARAM_ERROR);
        }
        // 请求服务端发送push消息
        try {
            pushMessage = pushMessage.replaceAll("<a href[^>]*>", "");
            pushMessage = pushMessage.replaceAll("</a>", "");
            userPushHandler.batchPushByGroup(userType, pushMessage, linkUrl, recipientCacheTopushKey);
        } catch (BusinessException e) {
            logger.error("按组向用户发送push业务异常，参数值,userType:{},pushMessage:{},linkUrl:{},recipientCacheTopushKey:{}", userType, pushMessage, linkUrl,
                    recipientCacheTopushKey);
            return PublicService.returnValue(e.getCode());
        } catch (Exception e) {
            logger.error("按组向用户发送push业务异常，参数值,userType:{},pushMessage:{},linkUrl:{},recipientCacheTopushKey:{},e:{}", userType, pushMessage, linkUrl,
                    recipientCacheTopushKey, e);
            return PublicService.returnValue(ExceptionCodes.FAILED);
        }
        return PublicService.returnValue(ExceptionCodes.SUCCESS);

    }

    @RequestMapping("reSendGroupPush")
    @ResponseBody
    public String reSendGroupPush() {
        logger.info("检查、补发待发送的push(标签医生)start");
        userPushHandler.reSendPushByGroup();
        logger.info("检查、补发待发送的push(标签医生)end");
        return PublicService.returnValue(ExceptionCodes.SUCCESS);
    }

    @RequestMapping("groupmessage/get")
    @ResponseBody
    public String getGroupMessageInCache() {
        logger.info("getGroupMessageInCachestart");
        GroupMessageVO groupMessage = userPushHandler.getGroupMessageInCache();
        return PublicService.returnValue(ExceptionCodes.SUCCESS, groupMessage);
    }

}