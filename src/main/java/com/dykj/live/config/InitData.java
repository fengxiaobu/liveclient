package com.dykj.live.config;

import com.dykj.live.dao.LiveInfoEntityRepository;
import com.dykj.live.entity.LiveInfoEntity;
import com.dykj.livepush.PushManager;
import com.dykj.util.CommandUtil;
import com.dykj.util.PtzUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 初始化相机对象
 */
@Component
@Order(2) // 执行顺序
public class InitData implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger(InitData.class);
    @Resource
    PtzUtil ptzUtil;

    @Resource
    private CommandUtil commandUtil;
    @Resource
    private LiveInfoEntityRepository liveInfoEntityRepository;
    @Resource
    private PushManager pushHandler;

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<LiveInfoEntity> cameras = liveInfoEntityRepository.findAll();
            cameras.forEach(liveInfoEntity -> {
                System.out.println(liveInfoEntity);
                try {
                    pushHandler.push(commandUtil.getMap4LiveInfo(liveInfoEntity));
                } catch (Exception e) {
                    log.error("初始化推流信息发生异常: {}", e.getMessage());
                }
            });
            log.info("初始化推流信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化推流信息发生异常: {}", e.getMessage());
        }
        ptzUtil.initCameraPtzServer();
    }
}
