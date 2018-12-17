package com.dykj.live.config;

import cn.hutool.json.JSONUtil;
import com.dykj.live.dao.CameraRepository;
import com.dykj.live.entity.LiveInfoEntity;
import com.dykj.live.pojo.Camera;
import com.dykj.live.service.CameraLiveService;
import com.dykj.livepush.PushManager;
import com.dykj.util.PtzUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 初始化相机对象
 */
@Component
@Order(2) // 执行顺序
public class InitData implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger(InitData.class);
    @Resource
    CameraRepository cameraRepository;
    @Resource
    PtzUtil ptzUtil;
    @Resource
    private PushManager pusher;
    @Resource
    private CameraLiveService cameraLiveService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<Camera> cameras = cameraRepository.findAll();
            cameras.forEach(e -> {
                System.out.println(e);
                pusher.push(JSONUtil.toBean(e.getComm(), Map.class));
                cameraLiveService.add(JSONUtil.toBean(e.getComm(), LiveInfoEntity.class));
            });
            log.info("初始化 {} 条推流信息成功:", cameras.size());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化推流信息发生异常: {}", e.getMessage());
        }
        ptzUtil.initCameraPtzServer();
    }
}
