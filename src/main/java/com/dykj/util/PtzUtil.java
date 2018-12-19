package com.dykj.util;

import cn.hutool.core.util.StrUtil;
import com.dykj.live.dao.LiveInfoEntityRepository;
import com.dykj.live.entity.LiveInfoEntity;
import org.onvif.unofficial.OnvifDevice;
import org.onvif.unofficial.services.PtzService;
import org.onvif.ver10.schema.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.xml.soap.SOAPException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author sjy
 */
@Component
public class PtzUtil {

    private static Logger log = LoggerFactory.getLogger(PtzUtil.class);
    /**
     * 存放云台对象
     */
    private static ConcurrentMap<String, OnvifDevice> onvifDeviceConcurrentHashMap = new ConcurrentHashMap<String, OnvifDevice>(20);
    @Resource
    LiveInfoEntityRepository liveInfoEntityRepository;

    public static void main(String[] args) {
        try {
            OnvifDevice device = new OnvifDevice("192.168.101.224", "admin", "admin123");
            //1OnvifDevice device = new OnvifDevice("192.168.101.202", "admin", "abc123456");
            List<Profile> profiles = device.getMediaService().getProfiles();
            for (Profile p : profiles) {
                try {
                    PtzService ptzService = device.getPtzService();
                    ptzService.continuousMove(p.getToken(), 0.0f, 0.5f, 0.0f);
                    System.out.println(device.getDeviceManagementService().getHostname());
                    String serviceUrl = device.getDeviceManagementService().getServiceUrl();
                    System.out.println(serviceUrl);
                    p.setName("大益办公室云台");
                    System.out.println("URL from Profile \'" + p.getName() + "\': " + device.getMediaService().getSnapshotUri(p.getToken()));
                    System.out.println("URL from Profile \'" + p.getName() + "\': " + device.getMediaService().getHTTPStreamUri(p.getToken()));
                    System.out.println("URL from Profile \'" + p.getName() + "\': " + device.getMediaService().getRTSPStreamUri(p.getToken()));
                    System.out.println("URL from Profile \'" + p.getName() + "\': " + device.getMediaService().getTCPStreamUri(p.getToken()));
                    System.out.println("URL from Profile \'" + p.getName() + "\': " + device.getMediaService().getUDPStreamUri(p.getToken()));
                    System.out.println("URL from Profile \'" + p.getName() + "\': " + device.getModel());
                    System.out.println();
                } catch (SOAPException e) {
                    System.err.println("Cannot grap snapshot URL, got Exception " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据ID获取云台对象
     *
     * @param id
     * @return
     */
    public OnvifDevice getDevice(String id) {
        if (onvifDeviceConcurrentHashMap.containsKey(id)) {
            return onvifDeviceConcurrentHashMap.get(id);
        }
        return null;
    }

    /**
     * 初始化云台对象
     */
    public void initCameraPtzServer() {
        try {
            List<LiveInfoEntity> cameras = liveInfoEntityRepository.findAll();
            cameras.stream().filter(camera -> StrUtil.equals("ONVIF", camera.getProtocol()) && !onvifDeviceConcurrentHashMap.containsKey(camera.getCdnid())).forEach(camera -> {
                String cdnid = camera.getCdnid();
                String ip = camera.getIp();
                String username = camera.getUsername();
                String password = camera.getPassword();
                try {
                    OnvifDevice device = new OnvifDevice(ip, username, password);
                    onvifDeviceConcurrentHashMap.put(cdnid, device);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("注册云台对象发生错 误设备IP:{}  错误原因:{}", ip, e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("注册云台对象发生错   错误原因:{}", e.getMessage());
        }
    }

    /**
     * 云台工具
     *
     * @param id
     * @param command
     * @param speed
     * @return
     */
    public boolean ptzMove(String id, String command, String speed) {
        OnvifDevice device = getDevice(id);
        try {
            if (device == null) {
                return false;
            }
            List<Profile> profs = device.getMediaService().getProfiles();
            if (profs.size() > 0) {
                Profile profile = profs.get(0);
                String token = profile.getToken();
                PtzService ptzService = device.getPtzService();
                //绝对移动
                //ptzService.absoluteMove(token,1.0f,1.0f,0.0f);
                if (StrUtil.equals("stop", command)) {
                    //停止移动
                    ptzService.stopMove(token);
                } else if (StrUtil.equals("up", command)) {
                    //连续移动
                    ptzService.continuousMove(token, 0.0f, 0.5f, 0.0f);
                } else if (StrUtil.equals("down", command)) {
                    //连续移动
                    ptzService.continuousMove(token, 0.0f, -0.5f, 0.0f);
                } else if (StrUtil.equals("left", command)) {
                    //连续移动
                    ptzService.continuousMove(token, -0.5f, 0.0f, 0.0f);
                } else if (StrUtil.equals("right", command)) {
                    //连续移动
                    ptzService.continuousMove(token, 0.5f, 0.0f, 0.0f);
                } else if (StrUtil.equals("zoomin", command)) {
                    //连续移动
                    ptzService.continuousMove(token, 0.0f, 0.0f, 0.5f);
                } else if (StrUtil.equals("zoomout", command)) {
                    //连续移动
                    ptzService.continuousMove(token, 0.0f, 0.0f, -0.5f);
                }
                log.info("云台功能正常");
                return true;
            } else {
                log.error("不支持云台功能 设备IP:{}", getHostName(device));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("云台功能发生异常,设备信息:{},异常信息:{}", getHostName(device), e.getMessage());
            return false;
        }
    }

    public void getInfo(String id) {
        OnvifDevice device = getDevice(id);
        try {
            List<Profile> profiles = device.getMediaService().getProfiles();
            for (Profile p : profiles) {
                try {

                    System.out.println("URL from Profile \'" + p.getName() + "\': " + device.getMediaService().getSnapshotUri(p.getToken()));
                } catch (SOAPException e) {
                    System.err.println("Cannot grap snapshot URL, got Exception " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备名称
     *
     * @param device
     * @return
     */
    public String getHostName(OnvifDevice device) {
        try {
            String hostname = device.getDeviceManagementService().getHostname();
            return hostname;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未获取到";
    }

}