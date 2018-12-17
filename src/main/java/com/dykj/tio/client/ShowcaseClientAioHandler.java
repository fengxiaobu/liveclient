package com.dykj.tio.client;


import com.dykj.tio.client.handler.LoginRespHandler;
import com.dykj.tio.client.handler.P2PRespHandler;
import com.dykj.tio.common.ShowcaseAbsAioHandler;
import com.dykj.tio.common.ShowcasePacket;
import com.dykj.tio.common.Type;
import com.dykj.tio.common.intf.AbsShowcaseBsHandler;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanyaowu
 * 2017年3月27日 上午12:18:11
 */
public class ShowcaseClientAioHandler extends ShowcaseAbsAioHandler implements ClientAioHandler {

    private static Map<Byte, AbsShowcaseBsHandler<?>> handlerMap = new HashMap<>();
    private static ShowcasePacket heartbeatPacket = new ShowcasePacket(Type.HEART_BEAT_REQ, null);

    static {
        handlerMap.put(Type.LOGIN_RESP, new LoginRespHandler());
        handlerMap.put(Type.P2P_RESP, new P2PRespHandler());
    }

    /**
     * 处理消息
     */
    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        ShowcasePacket showcasePacket = (ShowcasePacket) packet;
        Byte type = showcasePacket.getType();
        AbsShowcaseBsHandler<?> showcaseBsHandler = handlerMap.get(type);
        showcaseBsHandler.handler(showcasePacket, channelContext);
        return;
    }

    /**
     * 此方法如果返回null，框架层面则不会发心跳；如果返回非null，框架层面会定时发本方法返回的消息包
     */
    @Override
    public ShowcasePacket heartbeatPacket() {
        return heartbeatPacket;
    }
}
