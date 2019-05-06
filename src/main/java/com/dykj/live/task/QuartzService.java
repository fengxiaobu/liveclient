package com.dykj.live.task;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.dykj.live.config.ClientStarterConfig;
import com.dykj.live.config.Easy;
import com.dykj.live.dao.LiveInfoEntityRepository;
import com.dykj.live.entity.LiveInfoEntity;
import com.dykj.livepush.PushManager;
import com.dykj.util.CommandUtil;
import com.dykj.util.NetStateUtil;
import com.dykj.util.PtzUtil;
import com.dykj.util.UidManage;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 同步数据  检查摄像头 同步信息 初始化云台
 *
 * @author sjy
 */
@Component
public class QuartzService {
    @Resource
    private Easy easy;

    private static Logger log = LoggerFactory.getLogger(QuartzService.class);
    @Resource
    private LiveInfoEntityRepository liveInfoEntityRepository;
    @Resource
    private PtzUtil ptzUtil;
    @Resource
    private PushManager pushManager;

    @Resource
    private NetStateUtil netStateUtil;
    @Resource
    private CommandUtil commandUtil;

    /**
     * 每1分钟检查开启一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sysOpenLive() {
        try {
            List<LiveInfoEntity> liveInfoEntities = liveInfoEntityRepository.findAll();
            log.info("定时开启推流信息开始");
            liveInfoEntities.forEach(liveInfoEntity -> {
                try {
                    HttpResponse response = HttpRequest.post(easy.getGetSession())
                            .form("id", liveInfoEntity.getCdnid())
                            //超时，毫秒
                            .timeout(20000)
                            .execute();
                    String body = response.body();
                    log.info("DSS服务器返回信息: {}", body);
                    if (JSONUtil.isJson(body)) {
                        Map<String, Object> map = JSONUtil.toBean(body, Map.class);
                        Object session = map.get("session");
                        if (ObjectUtil.isNull(session)) {
                            log.info("直播{} _ {}_停止", liveInfoEntity.getPushId(), liveInfoEntity.getAppName());
                            if (liveInfoEntity.getOnline() && liveInfoEntity.getOpen()) {
                                pushManager.closePush(String.valueOf(liveInfoEntity.getPushId()));
                                pushManager.push(commandUtil.getMap4LiveInfo(liveInfoEntity));
                                log.info("直播 {} _ {}处于开启/在线成功{}_{}", liveInfoEntity.getPushId(), liveInfoEntity.getAppName(), liveInfoEntity.getOnline(), liveInfoEntity.getOpen());
                            } else {
                                log.info("直播 {} _ {}处于离线/关闭状态{}_{}", liveInfoEntity.getPushId(), liveInfoEntity.getAppName(), liveInfoEntity.getOnline(), liveInfoEntity.getOpen());
                            }
                        } else {
                            log.info("直播{} _ {}_推流中", liveInfoEntity.getPushId(), liveInfoEntity.getAppName());
                        }
                    } else {
                        log.error("DSS服务器返回的数据不是JSON");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("DSS服务器发生错误{}", e.getMessage());
                }
            });
            log.info("定时开启推流信息结束");
        } catch (Exception e) {
            log.error("定时开启推流信息发生错误 {}", e.getMessage());
        }
    }

    /**
     * 每分钟启动同步数据
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sysLiveInfo() {
        try {
            try {
                List<LiveInfoEntity> liveInfoEntities = liveInfoEntityRepository.findAll();
                List<Map> list = new ArrayList<>();
                liveInfoEntities.forEach(v -> {
                            //推流地址CDN
                            String cdn = v.getOutput();
                            String name = v.getAppName();
                            //Rtsp地址
                            String rtsp = v.getInput();
                            //是否开启
                            Boolean enable = v.getOpen();
                            Boolean online = v.getOnline();
                            //传输协议
                            String transport = v.getTransport();
                            //接入协议
                            String protocol = v.getProtocol();
                            //获取直播CDN ID
                            String cdnid = v.getCdnid();
                            String cameraIP = v.getIp();
                            String cameraUserName = v.getUsername();
                            String cameraPassword = v.getPassword();
                            Map<String, Object> map = new HashMap<>(6);
                            map.put("cdn", cdn);
                            map.put("name", name);
                            map.put("rtsp", rtsp);
                            map.put("enable", enable);
                            map.put("online", online);
                            map.put("cdnid", cdnid);
                            map.put("transport", transport);
                            map.put("protocol", protocol);
                            map.put("cameraIP", cameraIP);
                            map.put("cameraUserName", cameraUserName);
                            map.put("cameraPassword", cameraPassword);
                            list.add(map);
                            log.info("ID:{}  数据:{}", v.getCdnid(), v.toString());
                        }
                );

                HashMap<String, String> hashMap = MapUtil.newHashMap();
                hashMap.put("key", UidManage.getInstance().gettUid());
                hashMap.put("list", JSONUtil.toJsonStr(list));
                String jsonStr = JSONUtil.toJsonStr(hashMap);
                log.info("上传数据 ------->:{}", jsonStr);
                String post = post(easy.getUpdateCamera(), jsonStr);
                HttpResponse response = HttpRequest.post(easy.getUpdateCamera())
                        .form("data", jsonStr)
                        //超时，毫秒
                        .timeout(20000)
                        .disableCache()
                        .execute();
                /*response.body().trim()*/
                log.info("上传数据结果------->:{}", post);
                log.info("上传数量为------->:{}", list.size());
            } catch (IOException e) {
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
            log.error("同步登陆t-io服务器时发生异常" + e.getMessage());
        }
    }

    /**
     * 初始化新增云台对象
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sysInitCameraPtzServer() {
        try {
            ptzUtil.initCameraPtzServer();
        } catch (Exception e) {
            log.error("初始化新增云台对象时发生异常" + e.getMessage());
        }
    }

    /**
     * 定时摄像头服务可用状态
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sysCheckContent() {
        try {
            List<LiveInfoEntity> liveInfoEntities = liveInfoEntityRepository.findAll();
            liveInfoEntities.forEach(liveInfoEntity -> {
                boolean connect = netStateUtil.isConnect(liveInfoEntity.getIp());
                String pushId = String.valueOf(liveInfoEntity.getPushId());
                liveInfoEntity.setOnline(connect);
                liveInfoEntity.setUpdate_time(new Date());
                liveInfoEntityRepository.saveAndFlush(liveInfoEntity);
                if (connect) {
                    log.info("摄像头{}_服务可用", liveInfoEntity.getAppName());
                } else {
                    pushManager.closePush(pushId);
                    log.info("摄像头{}_服务不可用", liveInfoEntity.getAppName());
                }
            });
        } catch (Exception e) {
            log.error("检测摄像头服务可用状态时发生异常" + e.getMessage());
        }
    }

    public static final MediaType JSON = MediaType.get("application/form-data; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}

