package com.dykj.live;

import cn.hutool.core.thread.ThreadUtil;
import org.onvif.unofficial.OnvifDevice;
import org.onvif.unofficial.services.PtzService;
import org.onvif.ver10.schema.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class Test {
    public static void main(String[] args) throws Exception {
        String address = "192.168.101.224";// props.getProperty("address");
        String login = "admin";// props.getProperty("login");
        String password = "admin123";//props.getProperty("password");
        System.out.println(address + "/" + login + "/" + password);
        OnvifDevice device = new OnvifDevice(address, login, password);
        List<Profile> profs = device.getMediaService().getProfiles();
        if (profs.size() > 0) {
            Profile profile = profs.get(0);
            String token = profile.getToken();
            PtzService ptzService = device.getPtzService();

            //绝对移动
            //ptzService.absoluteMove(token,1.0f,1.0f,0.0f);
            //连续移动
            ptzService.continuousMove(token, 0.3f, -0.9f, 0.0f);
            //相对移动
            //ptzService.relativeMove(token, 1.0f, 1.0f, 0.0f);
            Thread.sleep(2000);
            //停止移动00
            ptzService.stopMove(token);
        }
    }

    @org.junit.Test
    public void test1() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-rtsp_transport tcp  -i rtsp://admin:admin123@192.168.101.224:554/cam/realmonitor?channel=1\\&subtype=0\\&unicast=true\\&proto=Onvif -c copy -f flv rtmp://video-console.benefitech.cn:10085/hls/RRodPIbmR?sign=RgodESbmgz");
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
        String temp;
        while ((temp = bufferedReader.readLine()) != null) {
            System.out.println(new String(temp.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        }
    }

    @org.junit.Test
    public void test2() {
        String input1 = "rtsp://192.168.101.224:554/cam/realmonitor?channel=1&subtype=0&unicast=true&proto=Onvif";
        String input2 = "rtsp://admin:admin123@192.168.101.224:554/cam/realmonitor?channel=1&subtype=0&unicast=true&proto=Onvif";
        System.out.println(input1.indexOf("@"));
        System.out.println(input1.replace("rtsp://", "rtsp://" + "admin" + ":" + "admin123"));

    }

    @org.junit.Test
    public void test3() throws IOException {
        ThreadFactory threadFactory = ThreadUtil.newNamedThreadFactory("test", true);
        ThreadLocal<Object> threadLocal = ThreadUtil.createThreadLocal(true);

/*
        Runnable runnable = ThreadUtil.newThread(() -> {
            try {
                ConvertVideoPakcet go = new ConvertVideoPakcet().from("rtmp://media3.sinovision.net:1935/live/livestream")
                        .to("rtmp://video-console.benefitech.cn:10085/hls/test123")
                        .go();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, "test");

        threadFactory.newThread(runnable).start();
        System.out.println(threadFactory.newThread(runnable).getState().name());

        //System.out.println(state.compareTo());
        System.out.println(threadFactory.newThread(runnable).isAlive());
        System.out.println(threadFactory.newThread(runnable).isInterrupted());
        System.out.println(threadFactory.newThread(runnable).isDaemon());
        ThreadUtil.sleep(100 * 100);
        threadFactory.newThread(runnable).stop();
        System.out.println(threadFactory.newThread(runnable).getState().name());
        ThreadUtil.sleep(100 * 100);
        System.out.println(threadFactory.newThread(runnable).isAlive());
        System.out.println(threadFactory.newThread(runnable).isInterrupted());
        System.out.println(threadFactory.newThread(runnable).isDaemon());
        System.out.println(threadFactory.newThread(runnable).getState().name());*/
    }
}
