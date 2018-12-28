package com.dykj.live.config;

import com.dykj.tio.client.ShowcaseClientAioHandler;
import com.dykj.tio.client.ShowcaseClientAioListener;
import com.dykj.tio.common.Const;
import com.dykj.tio.common.ShowcasePacket;
import com.dykj.tio.common.Type;
import com.dykj.tio.common.packets.LoginReqBody;
import com.dykj.tio.common.packets.P2PReqBody;
import com.dykj.util.UidManage;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Node;
import org.tio.core.Tio;
import org.tio.utils.json.Json;

/**
 * 启动t-io客户端 接受远程云台消息
 */
@Component
@Order(value = 0)
public class ClientStarterConfig implements ApplicationRunner {

    static String serverIp = "222.212.90.164";
    static int serverPort = Const.PORT;
    static ClientChannelContext clientChannelContext;
    private static Node serverNode = new Node(serverIp, serverPort);
    //用来自动连接的，不想自动连接请设为null
    private static ReconnConf reconnConf = new ReconnConf(5000L);
    private static ClientAioHandler tioClientHandler = new ShowcaseClientAioHandler();
    private static ClientAioListener aioListener = new ShowcaseClientAioListener();
    private static ClientGroupContext clientGroupContext = new ClientGroupContext(tioClientHandler, aioListener, reconnConf);
    private static TioClient tioClient = null;

    /**
     * 向服务器登录(仅仅作为标识)
     *
     * @param id       自定义ID
     * @param password 自定义密码
     * @throws Exception
     */
    public static void login(String id, String password) throws Exception {
        LoginReqBody loginReqBody = new LoginReqBody();
        loginReqBody.setLoginname(id);
        loginReqBody.setPassword(password);

        ShowcasePacket reqPacket = new ShowcasePacket();
        reqPacket.setType(Type.LOGIN_REQ);
        reqPacket.setBody(Json.toJson(loginReqBody).getBytes(ShowcasePacket.CHARSET));
        Tio.send(clientChannelContext, reqPacket);
    }

    /**
     * 发送P2P消息
     *
     * @param id   发给谁
     * @param text 内容
     * @throws Exception
     */
    public static void p2pMsg(String id, String text) throws Exception {
        P2PReqBody p2pReqBody = new P2PReqBody();
        p2pReqBody.setToUserid(id);
        p2pReqBody.setText(text);
        ShowcasePacket reqPacket = new ShowcasePacket();
        reqPacket.setType(Type.P2P_REQ);
        reqPacket.setBody(Json.toJson(p2pReqBody).getBytes(ShowcasePacket.CHARSET));
        Tio.send(clientChannelContext, reqPacket);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        tioClient = new TioClient(clientGroupContext);
        clientChannelContext = tioClient.connect(serverNode);
        String uuid = UidManage.getInstance().gettUid();
        login(uuid, uuid);
        p2pMsg("admin", "客户端启动----> " + uuid);
    }
}
