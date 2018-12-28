package com.dykj.live.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 读取配置文件
 *
 * @author sjy
 */
@Component
@EnableConfigurationProperties(Easy.class)//防止报红
@ConfigurationProperties(prefix = "easy") //与配置文件中开头相同
public class Easy {

    private String easyDssUrl;
    private String pushServer;
    private String ip;
    private String username;
    private String password;
    private String updateCamera;
    private String getSession;
    private String secretKey;
    private String logLevel;

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getGetSession() {
        return getSession;
    }

    public void setGetSession(String getSession) {
        this.getSession = getSession;
    }

    public String getEasyDssUrl() {
        return easyDssUrl;
    }

    public void setEasyDssUrl(String easyDssUrl) {
        this.easyDssUrl = easyDssUrl;
    }

    public String getPushServer() {
        return pushServer;
    }

    public void setPushServer(String pushServer) {
        this.pushServer = pushServer;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUpdateCamera() {
        return updateCamera;
    }

    public void setUpdateCamera(String updateCamera) {
        this.updateCamera = updateCamera;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
