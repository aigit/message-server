package com.qlk.message.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.qlk.baymax.common.config.EnableHessianAutoConfiguration;
import com.qlk.baymax.common.redis.config.EnableRedisAutoConfiguration;
import com.qlk.baymax.common.repository.config.EnableMultiMongoTemplateConfiguration;

/**
 * 项目启动入口
 *
 * @author 王泽浩 shuisu@7lk.com
 * @date 2017年09月09日
 * @since 1.0.0
 */
@ComponentScan("com.qlk")
@EnableRedisAutoConfiguration
@EnableHessianAutoConfiguration
@EnableMultiMongoTemplateConfiguration
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class Application {

    /**
     * 项目启动main方法
     *
     * @param args the input arguments
     *
     * @author 王泽浩 shuisu@7lk.com
     * @date 2017年09月09日
     * @since 1.0.0
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
