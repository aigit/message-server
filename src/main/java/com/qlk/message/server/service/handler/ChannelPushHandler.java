package com.qlk.message.server.service.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.message.server.bean.PushParams;
import com.qlk.message.server.service.IChannelPushService;
import com.qlk.message.server.utils.PropertyValueConstants;

/**
 * 具体发送渠道Handler
 * @author Ldl
 * @date 2018/11/14 14:57
 * @since 1.0.0
 */
@Component
public class ChannelPushHandler {

    public static final String CHANNEL_SERVICE_NAME_ANDROID = "androidPushService";
    public static final String CHANNEL_SERVICE_NAME_IOS = "iosPushService";

    private final Map<Integer, IChannelPushService> channelPushServiceMap = new ConcurrentHashMap<>(5);

    @Autowired
    public ChannelPushHandler(Map<String, IChannelPushService> channelPushServiceMap) {
        this.channelPushServiceMap.clear();
        channelPushServiceMap.forEach((k, v) -> {
                    switch (k) {
                        case CHANNEL_SERVICE_NAME_IOS:
                            this.channelPushServiceMap.put(PropertyValueConstants.PLATFORM_IOS, v);
                            break;
                        case CHANNEL_SERVICE_NAME_ANDROID:
                            this.channelPushServiceMap.put(PropertyValueConstants.PLATFORM_ANDRIOD, v);
                            break;
                        default:
                            break;
                    }
                }
        );
    }

    public IChannelPushService getChannelPushService(Integer channelType) {
        return channelPushServiceMap.get(channelType);
    }

    /**
     * 通过向不同设备发送单个Push
     * @param pushParams
     * @throws BusinessException
     */
    public void sendSingleDevice(PushParams pushParams) throws BusinessException {
        channelPushServiceMap.get(pushParams.getPlatType()).sendSingleDevice(pushParams);
    }


}
