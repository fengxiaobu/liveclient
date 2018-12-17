/**
 * 文件名：LiveInfoEntity.java 描述：发布视频信息 修改人：eguid 修改时间：2016年6月30日 修改内容：jdk1.7
 */
package com.dykj.live.entity;

import java.io.Serializable;

/**
 * 发布视频信息
 *
 * @author eguid
 * @version 2016年6月30日
 * @see LiveInfoEntity
 * @since jdk1.7
 */
public class LiveInfoEntity implements Serializable {
    private static final long serialVersionUID = -2657813061488195041L;
    /**
     * 应用名
     */
    private String appName;
    /**
     * 视频源地址，可以是实时流地址也可以是文件路径
     */
    private String input;
    /**
     * 实时流输出地址，这个默认是固定的rtmp服务器发布地址
     */
    private String output;
    /**
     * 视频格式，默认flv
     */
    private String fmt;
    /**
     * 帧率，最好是25-60
     */
    private String fps;
    /**
     * 分辨率，例如：640x360
     */
    private String rs;
    /**
     * 是否关闭音频
     */
    private String disableAudio;
    private String twoPart = "0";
    //传输协议   TCP/UDP
    private String transport = "TCP";
    //接入协议 ONVIF/RSTP
    private String protocol;
    private String ip;
    private String username;
    private String password;

    public LiveInfoEntity() {
        super();
    }

    public LiveInfoEntity(String appName, String twoPart, String input, String output, String fmt, String fps, String rs,
                          String disableAudio) {
        super();
        this.appName = appName;
        this.twoPart = twoPart;
        this.input = input;
        this.output = output;
        this.fmt = fmt;
        this.fps = fps;
        this.rs = rs;
        this.disableAudio = disableAudio;
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

    /**
     * @return the disableAudio
     */
    public String getDisableAudio() {
        return disableAudio;
    }

    /**
     * @param disableAudio the disableAudio to set
     */
    public void setDisableAudio(String disableAudio) {
        this.disableAudio = disableAudio;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LiveInfoEntity [appName=" + appName + ", input=" + input + ", output=" + output + ", fmt=" + fmt
                + ", fps=" + fps + ", rs=" + rs + ", disableAudio=" + disableAudio + "]";
    }

}
