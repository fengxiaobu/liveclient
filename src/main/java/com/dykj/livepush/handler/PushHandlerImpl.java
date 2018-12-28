/**
 * 文件名：PushHandlerImpl.java 描述： 修改人：eguid 修改时间：2016年6月24日 修改内容：
 */
package com.dykj.livepush.handler;

import cn.hutool.core.util.RuntimeUtil;
import com.dykj.live.config.ScheduleConfig;
import com.dykj.util.CommandUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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


    @Resource
    private CommandUtil commandUtil;
    @Resource
    private ThreadPoolTaskScheduler taskScheduler;
    @Override
    public ConcurrentMap<String, Object> push(Map<String, Object> paramMap) throws Exception {
        ConcurrentMap<String, Object> resultMap = null;
        Process proc = null;
        OutHandler errorGobbler = null;
        OutHandler infoGobbler = null;
        try {
            // 从map里面取数据，组装成命令
            String comm = commandUtil.getComm4Map(paramMap);
            if (comm == null) {
                throw new Exception();
            }
            // 执行命令行
            log.info("执行命令：" + comm);
            //proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", comm});
            //proc = Runtime.getRuntime().exec(comm);
            // 返回参数
            resultMap = new ConcurrentHashMap<>(6);
            resultMap.put("map", paramMap);
            Boolean open = Boolean.valueOf(String.valueOf(paramMap.get("open")));
            Boolean online = Boolean.valueOf(String.valueOf(paramMap.get("online")));
            resultMap.put("open", open);
            resultMap.put("online", online);
            log.info("开启状态 {}", open);
            if (online && open) {
                proc = RuntimeUtil.exec(comm);

                errorGobbler = new OutHandler(proc.getErrorStream(), paramMap.get("appName") + "_ERROR");
                infoGobbler = new OutHandler(proc.getInputStream(), paramMap.get("appName") + "_INFO");
                taskScheduler.execute(errorGobbler);
                taskScheduler.execute(infoGobbler);
                //scheduleConfig.getAsyncExecutor().execute(errorGobbler);
                //scheduleConfig.getAsyncExecutor().execute(infoGobbler);
                //errorGobbler.start();
                //infoGobbler.start();

                resultMap.put("info", infoGobbler);
                resultMap.put("error", errorGobbler);
                resultMap.put("process", proc);
            }
        } catch (Exception e) {
            if (proc != null) {
                proc.destroy();
            }
            if (errorGobbler != null) {
                errorGobbler.destroy();
            }
            if (infoGobbler != null) {
                infoGobbler.destroy();
            }
            log.error("发生异常{}", e.getMessage());
            throw e;
        }
        return resultMap;
    }

}
