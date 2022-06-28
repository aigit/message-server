/**
 *
 */
package com.qlk.message.server.listence;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.qlk.baymax.common.log.CommonLoggerFactory;
import com.qlk.baymax.common.utils.thread.ThreadPoolUtil;
import com.qlk.message.server.service.handler.UserPushHandler;

/**
 * 服务启动后需要执行的服务
 *
 * @author Ldl
 * @since 1.0.0
 */
@Component
public class StartupListener implements InitializingBean {

    private static Logger LOGGER = CommonLoggerFactory.getLogger(StartupListener.class);

    @Autowired
    private UserPushHandler userPushHandler;

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
