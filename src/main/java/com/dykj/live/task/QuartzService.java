package com.dykj.live.task;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.dykj.live.config.ClientStarterConfig;
import com.dykj.live.dao.CameraRepository;
import com.dykj.live.pojo.Camera;
import com.dykj.livepush.PushManager;
import com.dykj.livepush.dao.HandlerDao;
import com.dykj.util.PtzUtil;
import com.dykj.util.UidManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 重启停止的推流线程
 * 同步数据到数据库
 * @author sjy
 */
@Component
public class QuartzService {
    private final static String UPDATE_CAMERA = "http://222.212.90.164:8088/camera/v2/update";
    //private final static String UPDATE_CAMERA = "http://127.0.0.1:8080/camera/v2/update";

    private static Logger log = LoggerFactory.getLogger(QuartzService.class);
    @Resource
    private CameraRepository cameraRepository;
    @Resource
    private HandlerDao hd;
    @Resource
    private PushManager pusher;
    @Resource
    private PtzUtil ptzUtil;

    /**
     * 每分钟启动
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sysLive() {
        try {
            cameraRepository.deleteAll();
            ConcurrentMap<String, ConcurrentMap<String, Object>> hdAll = hd.getAll();
            hdAll.forEach((k, v) -> {
                ConcurrentMap<String, Object> map = hd.get(k);
                Map<String, Object> paramMap = (Map<String, Object>) map.get("map");
                String appName = (String) paramMap.get("appName");
                boolean isAlive = ((Process) map.get("process")).isAlive();
                log.info("{} 线程状态: {}", appName, isAlive);
                if (!isAlive) {
                    pusher.push(paramMap);
                }
                String input = (String) paramMap.get("input");
                String output = (String) paramMap.get("output");
                String twoPart = (String) paramMap.get("twoPart");
                String codec = (String) paramMap.get("codec");
                String ip = (String) paramMap.get("ip");
                String username = (String) paramMap.get("username");
                String password = (String) paramMap.get("password");
                String protocol = (String) paramMap.get("protocol");
                String transport = (String) paramMap.get("transport");
                int index = output.indexOf("?");
                String cdnid;
                if (index == -1) {
                    cdnid = output.substring(output.lastIndexOf("/") + 1);
                } else {
                    cdnid = output.substring(output.lastIndexOf("/") + 1, output.indexOf("?"));
                }
                Camera camera = new Camera(output, cdnid, appName, input, "1", "", "0", transport, protocol, JSONUtil.toJsonStr(paramMap), ip, username, password);
                cameraRepository.saveAndFlush(camera);
            });
            /**
             * 同步数据库
             */
            try {
                List<Camera> cameras = cameraRepository.findAll();
                cameras.forEach(v -> log.info("ID:{}  数据:{}", v.getCdnid(), v.toString()));

                HashMap<String, String> hashMap = MapUtil.newHashMap();
                hashMap.put("key", UidManage.getInstance().gettUid());
                hashMap.put("list", JSONUtil.toJsonStr(cameras));
                HttpResponse response = HttpRequest.post(UPDATE_CAMERA)
                        .form("data", JSONUtil.toJsonStr(hashMap))
                        //超时，毫秒
                        .timeout(20000)
                        .execute();
                log.info("上传数据结果------->:{}", response.body().trim());
                log.info("上传数量为------->:{}", cameras.size());
            } catch (HttpException e) {
                log.error("发生异常----->上传通道数据网络错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发生异常{}", e.getLocalizedMessage());
            log.error("发生异常" + e.getMessage());
        }
    }


    /**
     * 同步登陆t-io服务器
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sysRegister() {
        try {
            String uid = UidManage.getInstance().gettUid();
            ClientStarterConfig.login(uid, uid);
            //初始化新增云台对象
            ptzUtil.initCameraPtzServer();
        } catch (Exception e) {
            log.error("发生异常" + e.getMessage());
        }
    }

}

