/**
 * 文件名：LiveInfoEntity.java 描述：发布视频信息 修改人：eguid 修改时间：2016年6月30日 修改内容：jdk1.7
 */
package com.dykj.live.entity;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author sjy
 * <p>
 * 直播储存对象
 */
@Entity
@Table(name = "LiveInfoEntity")
public class LiveInfoEntity implements Serializable {
    private static final long serialVersionUID = -2657813061488195041L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pushId;
    /**
     * 应用名
     */
    @Column
    private String appName;
    /**
     * 视频源地址，可以是实时流地址也可以是文件路径 Rtsp地址
     */
    @Column
    private String input;

    /**
     * 实时流输出地址，这个默认是固定的rtmp服务器发布地址
     * 推流地址CDN
     */
    @Column
    private String output;

    /**
     * 视频格式，默认flv
     */
    @Column
    private String fmt = "flv";
    /**
     * 帧率，最好是25-60
     */
    @Column
    private String fps = "25";
    /**
     * 分辨率，例如：640x360
     */
    @Column
    private String rs = "1920x10800";

    @Column
    private String twoPart = "0";

    /**
     * CDN直播ID
     */
    @Column
    private String cdnid;

    /**
     * 传输协议 TCP/UDP
     */
    @Column
    private String transport;
    /**
     * 接入协议 ONVIF/RSTP
     */
    @Column
    private String protocol;
    /**
     * 摄像头IP
     */
    @Column
    private String ip;
    /**
     * 摄像头用户名
     */
    @Column
    private String username;
    /**
     * 摄像头密码
     */
    @Column
    private String password;
    /**
     * 是否开启
     */
    @Column
    private Boolean open;
    /**
     * 是否在线
     */
    @Column
    private Boolean online = true;
 /*   *//**
     * 执行命令
     *//*
    @Lob
    @Column(columnDefinition = "mediumtext")
    private String comm;*/
    /**
     * 更新时间
     */
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time = new Date();
    /**
     * 创建时间
     */
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date create_time = new Date();

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Long getPushId() {
        return pushId;
    }

    public void setPushId(Long pushId) {
        this.pushId = pushId;
    }

    public String getCdnid() {
        return cdnid;
    }

    public void setCdnid(String cdnid) {
        this.cdnid = cdnid;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public String getIp() {
        return ip == null ? "" : ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username == null ? "" : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTwoPart() {
        return twoPart;
    }

    public void setTwoPart(String twoPart) {
        this.twoPart = twoPart;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the appName
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName the appName to set
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return the input
     */
    public String getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * @return the output
     */
    public String getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * @return the fmt
     */
    public String getFmt() {
        return fmt;
    }

    /**
     * @param fmt the fmt to set
     */
    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    /**
     * @return the fps
     */
    public String getFps() {
        return fps;
    }

    /**
     * @param fps the fps to set
     */
    public void setFps(String fps) {
        this.fps = fps;
    }

    /**
     * @return the rs
     */
    public String getRs() {
        return rs;
    }

    /**
     * @param rs the rs to set
     */
    public void setRs(String rs) {
        this.rs = rs;
    }


    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

}
