package com.dykj.tio.client.handler;

import cn.hutool.json.JSONUtil;
import com.dykj.live.dao.CameraRepository;
import com.dykj.live.pojo.Camera;
import com.dykj.tio.common.ShowcasePacket;
import com.dykj.tio.common.intf.AbsShowcaseBsHandler;
import com.dykj.tio.common.packets.P2PRespBody;
import com.dykj.util.PtzUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author tanyaowu
 * 2017年3月27日 下午9:51:28
 */
@Component
public class P2PRespHandler extends AbsShowcaseBsHandler<P2PRespBody> {

    public static final String uri = "";
    public static P2PRespHandler p2PRespHandler;
    private static Logger log = LoggerFactory.getLogger(P2PRespHandler.class);
    @Autowired
    private PtzUtil ptzUtil;
    @Autowired
    private CameraRepository cameraRepository;

    /**
     * @author tanyaowu
     */
    public P2PRespHandler() {
    }

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        p2PRespHandler = this;
        p2PRespHandler.cameraRepository = this.cameraRepository;
        p2PRespHandler.ptzUtil = this.ptzUtil;
        //初使化时将已静态化的accessTokenOcrRepository实例化
    }

    /**
     * @return
     * @author tanyaowu
     */
    @Override
    public Class<P2PRespBody> bodyClass() {
        return P2PRespBody.class;
    }

    /**
     * @param packet
     * @param bsBody
     * @param channelContext
     * @return
     * @throws Exception
     * @author tanyaowu
     */
    @Override
    public Object handler(ShowcasePacket packet, P2PRespBody bsBody, ChannelContext channelContext) {
        log.info("收到P2P响应消息:{}", JSONUtil.toJsonStr(bsBody));
        String text = bsBody.getText();
        if (JSONUtil.isJson(text)) {
            Map map = JSONUtil.toBean(text, Map.class);
            String cdn = String.valueOf(map.get("cdn"));
            String command = String.valueOf(map.get("command"));
            String speed = String.valueOf(map.get("speed"));
            List<Camera> cameras = p2PRespHandler.cameraRepository.findByCdnidContaining(cdn);
            //TODO 远程云台控制
            if (cameras.size() == 1) {
                try {
                    p2PRespHandler.ptzUtil.ptzMove(cdn, command, speed);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("云台网络故障");
                }
            }
        }
        return null;
    }
}
