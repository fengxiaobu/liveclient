package com.dykj.util;

import cn.hutool.core.util.StrUtil;
import com.dykj.live.entity.LiveInfoEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommandUtil {


    /**
     * 解析参数
     *
     * @param liveInfo
     * @return
     */
    public Map<String, Object> getMap4LiveInfo(LiveInfoEntity liveInfo) {
        String appName = liveInfo.getAppName();
        String input = liveInfo.getInput();
        String output = liveInfo.getOutput();
        String fmt = liveInfo.getFmt();
        String fps = liveInfo.getFps();
        String rs = liveInfo.getRs();
        String twoPart = liveInfo.getTwoPart();
        String username = liveInfo.getUsername();
        String password = liveInfo.getPassword();
        String ip = liveInfo.getIp();
        String protocol = liveInfo.getProtocol();
        String transport = liveInfo.getTransport();
        Boolean open = liveInfo.getOpen();
        Long pushId = liveInfo.getPushId();
        Boolean online = liveInfo.getOnline();

        Map<String, Object> map = new HashMap<String, Object>(20);
        map.put("appName", appName);
        // 输入输出暂时固定
        map.put("input", input);
        map.put("output", output);
        map.put("twoPart", twoPart);
        /*ONVIF设备必填*/
        map.put("ip", ip);

        map.put("pushId", pushId);
        map.put("username", username);
        map.put("password", password);
        map.put("protocol", protocol);
        map.put("transport", transport);
        map.put("open", open);
        map.put("online", online);

        if (fmt != null) {
            map.put("fmt", fmt);
        }
        if (fps != null) {
            map.put("fps", fps);
        }
        if (rs != null) {
            map.put("rs", rs);
        }

        return map;
    }


    /*
     * "ffmpeg -i "+
     * "rtsp://admin:admin@192.168.2.236:37779/cam/realmonitor?channel=1&subtype=0 "
     * + " -f flv -r 25 -s 640x360 -an" + " rtmp://192.168.30.21/live/test"
     * 推送流格式：
     * name:应用名；input:接收地址；output:推送地址；fmt:视频格式；fps:视频帧率；rs:视频分辨率
     * 是否开启音频
     */

    /**
     * 通过解析参数生成可执行的命令行字符串；
     * name:应用名；input:接收地址；output:推送地址；fmt:视频格式；fps:视频帧率；rs:视频分辨率
     * "ffmpeg -i rtsp://admin:admin@192.168.2.236:37779/cam/realmonitor?channel=1&subtype=0 -f flv -r 25 -s 640x360 -an rtmp://192.168.30.21/live/test"
     *
     * @param paramMap
     * @return 命令行字符串
     * @throws Exception
     */
    public String getComm4Map(Map<String, Object> paramMap) {
        try {
            if (paramMap.containsKey("ffmpegPath")) {
                String ffmpegPath = (String) paramMap.get("ffmpegPath");
                // -i：输入流地址或者文件绝对地址
                StringBuilder comm = new StringBuilder(ffmpegPath);
                // 是否有必输项：输入地址，输出地址，应用名，twoPart：0-推一个元码流；1-推一个自定义推流；2-推两个流（一个是自定义，一个是元码）
                if (paramMap.containsKey("input") && paramMap.containsKey("output") && paramMap.containsKey("appName") && paramMap.containsKey("twoPart")) {
                    String input = (String) paramMap.get("input");
                    String output = (String) paramMap.get("output");
                    String appName = (String) paramMap.get("appName");
                    String twoPart = (String) paramMap.get("twoPart");
                    //String codec = (String) paramMap.get("codec");

                    //默认h264解码
                    //codec = (codec == null ? "h264" : (String) paramMap.get("codec"));
                    if (StrUtil.containsAny(input, "rtsp://")) {
                        // comm.append(" -loglevel quiet -rtsp_transport tcp  -i ");
                        comm.append(" -rtsp_transport tcp  -i ");
                    } else {
                        //comm.append("  -loglevel quiet -i ");
                        comm.append(" -i ");
                    }
                    // 输入地址
                    comm.append("\"").append(input).append("\"");
                    // 当twoPart为0时，只推一个元码流
                    if ("0".equals(twoPart)) {
                        //转码-耗费CPU
                        //comm.append(" -vcodec " + codec + " -f flv -an " + output  );
                        //不转码直接推流-省资源_推荐
                        comm.append(" -c copy -f flv \"" + output + "\"");
                    } else {
                        // -f ：转换格式，默认flv
                        if (paramMap.containsKey("fmt")) {
                            String fmt = (String) paramMap.get("fmt");
                            comm.append(" -f " + fmt);
                        }
                        // -r :帧率，默认25；-g :帧间隔
                        if (paramMap.containsKey("fps")) {
                            String fps = (String) paramMap.get("fps");
                            comm.append(" -r " + fps);
                            comm.append(" -g " + fps);
                        }
                        // -s 分辨率 默认是原分辨率
                        if (paramMap.containsKey("rs")) {
                            String rs = (String) paramMap.get("rs");
                            comm.append(" -s " + rs);
                        }
                        // 输出地址+发布的应用名
                        comm.append(" -an " + output);
                        // 当twoPart为2时推两个流，一个自定义流，一个元码流
                        if ("2".equals(twoPart)) {
                            // 一个视频源，可以有多个输出，第二个输出为拷贝源视频输出，不改变视频的各项参数并且命名为应用名+HD
                            comm.append(" -vcodec copy  -f flv -an ").append(output + appName + "HD");
                        }
                    }
                    return comm.toString();
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

}
