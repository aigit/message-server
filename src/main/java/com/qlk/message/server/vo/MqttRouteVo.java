package com.qlk.message.server.vo;

import java.util.Map;

public class MqttRouteVo {
    
    /**
     * mqtt host
     */
    private String host;
    /**
     * mqtt post
     */
    private int port;
    /**
     * mqtt 连接超时时间
     */
    private int connectionTimeout;
    /**
     * mqtt 心跳时间
     */
    private int keepAliveInterval;
    /**
     * 是否清除客户端连接session信息
     */
    private boolean cleanSession;
    /**
     * mqtt 遗嘱消息主题名称
     */
    private String willTopicName;
    /**
     * mqtt 公共主题名称
     */
    private String publicTopicName;
    /**
     * mqtt 私人主题名称
     */
    private String privateTopicName;
    /**
     * mqtt ssl认证信息
     */
    private Map<String,String> sslProperties;
    
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }
    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }
    public boolean isCleanSession() {
        return cleanSession;
    }
    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }
    public String getWillTopicName() {
        return willTopicName;
    }
    public void setWillTopicName(String willTopicName) {
        this.willTopicName = willTopicName;
    }
    public String getPublicTopicName() {
        return publicTopicName;
    }
    public void setPublicTopicName(String publicTopicName) {
        this.publicTopicName = publicTopicName;
    }
    public String getPrivateTopicName() {
        return privateTopicName;
    }
    public void setPrivateTopicName(String privateTopicName) {
        this.privateTopicName = privateTopicName;
    }
    public Map<String, String> getSslProperties() {
        return sslProperties;
    }
    public void setSslProperties(Map<String, String> sslProperties) {
        this.sslProperties = sslProperties;
    }
    
}
