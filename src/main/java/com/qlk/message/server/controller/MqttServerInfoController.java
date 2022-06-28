package com.qlk.message.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qlk.baymax.common.exception.PublicService;
import com.qlk.baymax.common.utils.lang.ConfigUtil;
import com.qlk.message.server.exception.ExceptionCodes;
import com.qlk.message.server.vo.MqttRouteVo;

@Controller
@RequestMapping("serverInfo")
public class MqttServerInfoController {

    @RequestMapping("getInfo")
    public @ResponseBody
    String getInfo(Long userId, String userType) {

        MqttRouteVo vo = new MqttRouteVo();
        vo.setHost(ConfigUtil.getString("mqttUrl"));
        vo.setPort(ConfigUtil.getInt("mqttPort"));
        vo.setConnectionTimeout(300);
        vo.setKeepAliveInterval(300);
        vo.setCleanSession(true);
        vo.setWillTopicName("");
        vo.setPublicTopicName("bm/d/a/pub");
        vo.setPrivateTopicName("bm/d/a/" + userId);
        vo.setSslProperties(null);

        return PublicService.returnValue(ExceptionCodes.SUCCESS, vo);
    }

    @RequestMapping("getImInfo")
    public @ResponseBody
    String getInImfo() {

        MqttRouteVo vo = new MqttRouteVo();
        vo.setHost(ConfigUtil.getString("imChatUrl"));
        vo.setPort(ConfigUtil.getInt("imChatPort"));
        vo.setConnectionTimeout(30);
        vo.setKeepAliveInterval(1);
        vo.setCleanSession(true);
        vo.setWillTopicName("clientWillDead");
        vo.setSslProperties(null);

        return PublicService.returnValue(ExceptionCodes.SUCCESS, vo);
    }

}
