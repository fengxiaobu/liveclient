package com.dykj.live.task;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.dykj.live.config.ClientStarterConfig;
import com.dykj.live.dao.LiveInfoEntityRepository;
import com.dykj.live.entity.LiveInfoEntity;
import com.dykj.livepush.PushManager;
import com.dykj.livepush.dao.HandlerDao;
import com.dykj.util.CommandUtil;
import com.dykj.util.NetStateUtil;
import com.dykj.util.PtzUtil;
import com.dykj.util.UidManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 重启停止的推流线程
 * 同步数据到数据库
 *
 * @author sjy
 */
@Component
public class QuartzService {
    private final static String UPDATE_CAMERA = "http://222.212.90.164:8088/camera/v3/update";
    //private final static String UPDATE_CAMERA = "http://127.0.0.1:8080/camera/v3/update";

    private static Logger log = LoggerFactory.getLogger(QuartzService.class);
    @Resource
    private LiveInfoEntityRepository liveInfoEntityRepository;
    @Resource
    private HandlerDao hd;
    @Resource
    private PushManager pusher;
    @Resource
    private PtzUtil ptzUtil;

    @Resource
    private NetStateUtil netStateUtil;
    @Resource
    private CommandUtil commandUtil;

    /**
     * 每10分钟检查开启一次
     */
    @Scheduled(cron = "0 0/3 * * * ?")
    public void sysOpen() {
        try {
            List<LiveInfoEntity> liveInfoEntities = liveInfoEntityRepository.findAll();
            liveInfoEntities.forEach(liveInfoEntity -> {
                if (liveInfoEntity.getOnline() && liveInfoEntity.getOpen()) {
                    pusher.closePush(String.valueOf(liveInfoEntity.getPushId()));
                    pusher.push(commandUtil.getMap4LiveInfo(liveInfoEntity));
                } else {
                    log.info("{} _ {}处于离线状态", liveInfoEntity.getPushId(), liveInfoEntity.getAppName());
                }
            });
            log.info("定时开启推流信息成功");
        } catch (Exception e) {
            log.error("定时开启推流信息发生错误 {}", e.getMessage());
        }
    }

    /**
     * 每分钟启动同步数据
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sysLive() {
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
                            //传输协议
                            String transport = v.getTransport();
                            //接入协议
                            String protocol = v.getProtocol();
                            //获取直播CDN ID
                            String cdnid = v.getCdnid();
                            Map<String, Object> map = new HashMap<>(6);
                            map.put("cdn", cdn);
                            map.put("name", name);
                            map.put("rtsp", rtsp);
                            map.put("enable", enable);
                            map.put("cdnid", cdnid);
                            map.put("transport", transport);
                            map.put("protocol", protocol);
                            list.add(map);
                            log.info("ID:{}  数据:{}", v.getCdnid(), v.toString());
                        }
                );

                HashMap<String, String> hashMap = MapUtil.newHashMap();
                hashMap.put("key", UidManage.getInstance().gettUid());
                hashMap.put("list", JSONUtil.toJsonStr(list));
                HttpResponse response = HttpRequest.post(UPDATE_CAMERA)
                        .form("data", JSONUtil.toJsonStr(hashMap))
                        //超时，毫秒
                        .timeout(20000)
                        .execute();
                log.info("上传数据结果------->:{}", response.body().trim());
                log.info("上传数量为------->:{}", list.size());
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

    /**
     * 定时摄像头服务可用状态
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sysContent() {
        try {
            List<LiveInfoEntity> liveInfoEntities = liveInfoEntityRepository.findAll();
            liveInfoEntities.forEach(liveInfoEntity -> {
                boolean connect = netStateUtil.isConnect(liveInfoEntity.getIp());
                String pushId = String.valueOf(liveInfoEntity.getPushId());
                liveInfoEntity.setOnline(connect);
                liveInfoEntityRepository.saveAndFlush(liveInfoEntity);
                if (!connect) {
                    pusher.closePush(pushId);
                }
            });
            log.info("检测摄像头服务可用状态成功");
        } catch (Exception e) {
            log.error("检测摄像头服务可用状态时发生异常" + e.getMessage());
        }
    }

}

