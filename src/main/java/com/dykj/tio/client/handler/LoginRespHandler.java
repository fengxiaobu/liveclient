package com.dykj.tio.client.handler;

import com.dykj.tio.common.ShowcasePacket;
import com.dykj.tio.common.ShowcaseSessionContext;
import com.dykj.tio.common.intf.AbsShowcaseBsHandler;
import com.dykj.tio.common.packets.LoginRespBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.utils.json.Json;

/**
 * @author tanyaowu
 * 2017年3月27日 下午9:51:28
 */
public class LoginRespHandler extends AbsShowcaseBsHandler<LoginRespBody> {
    private static Logger log = LoggerFactory.getLogger(LoginRespHandler.class);

    /**
     * @author tanyaowu
     */
    public LoginRespHandler() {
    }

    /**
     * @param args
     * @author tanyaowu
     */
    public static void main(String[] args) {

    }

    /**
     * @return
     * @author tanyaowu
     */
    @Override
    public Class<LoginRespBody> bodyClass() {
        return LoginRespBody.class;
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
    public Object handler(ShowcasePacket packet, LoginRespBody bsBody, ChannelContext channelContext) throws Exception {
        log.info("收到登录响应消息:" + Json.toJson(bsBody));
        if (LoginRespBody.Code.SUCCESS.equals(bsBody.getCode())) {
            ShowcaseSessionContext showcaseSessionContext = (ShowcaseSessionContext) channelContext.getAttribute();
            showcaseSessionContext.setToken(bsBody.getToken());
            log.info("登录成功，token是:" + bsBody.getToken());
        }

        return null;
    }
}
