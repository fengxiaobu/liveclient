/**
 * 文件名：PushMangerImpl.java 描述：实现push管理器的接口功能 修改人：eguid 修改时间：2016年6月29日 修改内容：增加管理处理器和应用名关系
 */
package com.dykj.livepush;


import com.dykj.livepush.conf.ConfUtil;
import com.dykj.livepush.dao.HandlerDao;
import com.dykj.livepush.handler.OutHandler;
import com.dykj.livepush.handler.PushHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 实现push管理器的push，delete，view服务
 *
 * @author eguid
 * @version 2016年6月24日
 * @since jdk1.7
 */

@Component
public class PushManagerImpl implements PushManager {
    private static Logger log = LoggerFactory.getLogger(PushManagerImpl.class);
    /**
     * 配置文件
     */
    @Resource
    private ConfUtil confUtil;
    /**
     * 引用push处理器
     */
    @Autowired
    private PushHandler pusher;
    /**
     * 管理处理器的主进程Process及两个输出线程的关系
     */
    @Autowired
    private HandlerDao hd;

    public synchronized void setPusher(PushHandler pusher) {
        this.pusher = pusher;
    }

    public synchronized void setHd(HandlerDao hd) {
        this.hd = hd;
    }

    @Override
    public synchronized String push(Map<String, Object> parammap) {
        String appName = null;
        ConcurrentMap<String, Object> resultMap = null;
        try {
            // ffmpeg环境是否配置正确
            if (!confUtil.isHave()) {
                return null;
            }
            // 参数是否符合要求
            if (parammap == null || parammap.isEmpty() || !parammap.containsKey("appName")) {
                return null;
            }
            appName = (String) parammap.get("appName");
            if (appName != null && "".equals(appName.trim())) {
                return null;
            }
            parammap.put("ffmpegPath", confUtil.getPath());
            resultMap = pusher.push(parammap);
            // 处理器和输出线程对应关系
            hd.set(appName, resultMap);
        } catch (Exception e) {
            // 暂时先写这样，后期加日志
            log.error("重大错误：参数不符合要求或运行失败{}", e.getMessage());
            return null;
        }
        return appName;

    }

    @Override
    public synchronized boolean closePush(String appName) {
        if (hd.isHave(appName)) {
            ConcurrentMap<String, Object> map = hd.get(appName);
            // 关闭两个线程
            ((OutHandler) map.get("error")).destroy();
            ((OutHandler) map.get("info")).destroy();
            // 暂时先这样写，后期加日志
            log.info("停止命令-----end commond");
            // 关闭命令主进程
            ((Process) map.get("process")).destroy();
            // 删除处理器与线程对应关系表
            hd.delete(appName);
            return true;
        }
        return false;
    }

    @Override
    public synchronized Set<String> viewAppName() {
        return hd.getAllAppName();
    }

    @Override
    public synchronized boolean isHave(String appName) {
        return hd.isHave(appName);

    }
}
