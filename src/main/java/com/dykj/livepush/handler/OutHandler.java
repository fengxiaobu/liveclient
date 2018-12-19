/**
 * 文件名：OutHandler.java 描述：输出命令行主进程消息 修改人：eguid 修改时间：2016年6月27日 修改内容：
 */
package com.dykj.livepush.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 用于输出命令行主进程的消息线程（必须开启，否则命令行主进程无法正常执行） 重要：该类重写了destroy方法，用于安全的关闭该线程
 *
 * @author eguid
 * @version 2016年6月27日
 * @see OutHandler
 * @since jdk1.7
 */

public class OutHandler extends Thread {
    private static Logger log = LoggerFactory.getLogger(OutHandler.class);
    /**
     * 控制状态
     */
    private volatile boolean desstatus = true;

    /**
     * 读取输出流
     */
    private BufferedReader br = null;

    /**
     * 输出类型
     */
    private String type = null;

    public OutHandler(InputStream is, String type) {
        br = new BufferedReader(new InputStreamReader(is));
        this.type = type;
    }

    /**
     * 重写线程销毁方法，安全的关闭线程
     */
    @Override
    public void destroy() {
        setDesStatus(false);
    }

    public void setDesStatus(boolean desStatus) {
        this.desstatus = desStatus;
    }

    /**
     * 执行输出线程
     */
    @Override
    public void run() {
        String msg = null;
        int index = 0;
        int errorIndex = 0;
        int status = 10;

        try {
            log.info(type + "   开始推流！");
            while (desstatus && (msg = br.readLine()) != null) {
                if (msg.indexOf("[rtsp") != -1) {
                    log.info(type + "接收" + status + "个数据包" + msg);
                    log.info(type + "，网络异常丢包，丢包次数:" + errorIndex++ + "，消息体：" + msg);
                    status = 10;
                    index = 0;
                }

                if (index % status == 0) {
                    log.info("接收" + status + "个数据包" + msg);
                    status *= 2;
                }
                index++;
            }
        } catch (IOException e) {
            log.error(type + "发生内部异常错误，自动关闭[" + this.getId() + "]线程");
            destroy();
        } finally {
            if (this.isAlive()) {
                destroy();
            }
        }
    }
}
