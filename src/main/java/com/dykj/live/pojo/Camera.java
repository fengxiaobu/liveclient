package com.dykj.live.pojo;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "Camera")
public class Camera implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //推流地址CDN
    @Column
    private String cdn;
    //CDN直播ID
    @Column
    private String cdnid;
    //通道名称
    @Column
    private String name;
    //Rtsp地址
    @Column
    private String rtsp;
    //是否开启
    @Column
    private String enable;
    //Onvif地址
    @Column
    private String onvif;
    //是否开启音频
    @Column
    private String audio;
    //传输协议
    @Column
    private String transport;
    //接入协议
    @Column
    private String protocol;
    private String ip;
    private String username;
    private String password;
    //执行命令
    @Lob
    @Column(columnDefinition = "mediumtext")
    private String comm;
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;

    public Camera(String cdn, String cdnid, String name, String rtsp, String enable, String onvif, String audio, String transport, String protocol, String comm, String ip, String username, String password) {
        this.cdn = cdn;
        this.cdnid = cdnid;
        this.name = name;
        this.rtsp = rtsp;
        this.enable = enable;
        this.onvif = onvif;
        this.audio = audio;
        this.transport = transport;
        this.protocol = protocol;
        this.comm = comm;
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.update_time = new Date();
    }

    public Camera() {
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

    public String getCdnid() {
        return cdnid;
    }

    public void setCdnid(String cdnid) {
        this.cdnid = cdnid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCdn() {
        return cdn;
    }

    public void setCdn(String cdn) {
        this.cdn = cdn;
    }


    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRtsp() {
        return rtsp;
    }

    public void setRtsp(String rtsp) {
        this.rtsp = rtsp;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getOnvif() {
        return onvif;
    }

    public void setOnvif(String onvif) {
        this.onvif = onvif;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
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

    public String getComm() {
        return comm;
    }

    public void setComm(String comm) {
        this.comm = comm;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
