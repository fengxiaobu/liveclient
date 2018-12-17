/**
 * 文件名：PushHandlerImpl.java 描述： 修改人：eguid 修改时间：2016年6月24日 修改内容：
 */
package com.dykj.livepush.handler;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 提供解析参数生成ffmpeg命令并处理push操作
 *
 * @author eguid
 * @version 2016年6月24日
 * @see PushHandlerImpl
 * @since jdk1.7
 */
@Component
public class PushHandlerImpl implements PushHandler {
    private static Logger log = LoggerFactory.getLogger(PushHandlerImpl.class);

    @Override
    public ConcurrentMap<String, Object> push(Map<String, Object> paramMap) throws Exception {
        ConcurrentMap<String, Object> resultMap = null;
        Process proc = null;
        OutHandler errorGobbler = null;
        OutHandler infoGobbler = null;

        try {
            // 从map里面取数据，组装成命令
            String comm = getComm4Map(paramMap);
            if (comm == null) {
                throw new Exception();
            }
            // 执行命令行
            log.info("执行命令：" + comm);
            //proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", comm});
            //proc = Runtime.getRuntime().exec(comm);
            proc = RuntimeUtil.exec(comm);

            errorGobbler = new OutHandler(proc.getErrorStream(), (String) paramMap.get("appName") + "_ERROR");
            infoGobbler = new OutHandler(proc.getInputStream(), (String) paramMap.get("appName") + "_INFO");
            errorGobbler.start();
            infoGobbler.start();
            // 返回参数
            resultMap = new ConcurrentHashMap<>();
            resultMap.put("map", paramMap);
            resultMap.put("info", infoGobbler);
            resultMap.put("error", errorGobbler);
            resultMap.put("process", proc);
        } catch (Exception e) {
            if (proc != null) {
                proc.destroy();
            }
            if (errorGobbler != null) {
                errorGobbler.destroy();
            }
            log.error("发生异常{}", e.getMessage());
            throw e;
        }
        return resultMap;
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
    protected String getComm4Map(Map<String, Object> paramMap) throws Exception {
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
                    String codec = (String) paramMap.get("codec");
                    String transport = (String) paramMap.get("transport");
                    //默认h264解码
                    codec = (codec == null ? "h264" : (String) paramMap.get("codec"));
                    if (StrUtil.containsAny(input, "rtsp://")) {
                       // comm.append(" -loglevel quiet -rtsp_transport tcp  -i ");
                        comm.append(" -rtsp_transport tcp  -i ");
                    } else {
                        //comm.append("  -loglevel quiet -i ");
                        comm.append(" -i ");
                    }
                    // 输入地址
                    comm.append(input);
                    // 当twoPart为0时，只推一个元码流
                    if ("0".equals(twoPart)) {
                        //转码-耗费CPU
                        //comm.append(" -vcodec " + codec + " -f flv -an " + output  );
                        //不转码直接推流-省资源_推荐
                        comm.append(" -c copy -f flv " + output);
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
