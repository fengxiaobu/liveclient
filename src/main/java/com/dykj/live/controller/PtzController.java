package com.dykj.live.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dykj.util.DeviceDiscovery;
import com.dykj.util.PtzUtil;
import org.onvif.unofficial.OnvifDevice;
import org.onvif.ver10.schema.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ptz")
public class PtzController {

    @Autowired
    PtzUtil ptzUtil;


    /**
     * 云台
     *
     * @param id
     * @param command stop, up, down, left, right, zoomin, zoomout
     * @param speed
     * @return
     */
    @RequestMapping(value = "/command", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> ptz(String id, String command, String speed) {
        Map<String, String> result = new HashMap<>(6);
        try {
            boolean flag = ptzUtil.ptzMove(id, command, speed);
            if (flag) {
                result.put("code", "200");
                result.put("msg", "成功");
            } else {
                result.put("code", "500");
                result.put("msg", "失败");
            }
        } catch (
                Exception e) {
            result.put("code", "500");
            result.put("msg", "失败");
        }
        return result;
    }

    /**
     * 探询ONVIF设备 默认返回主流地址
     *
     * @param ip
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/probedevice", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> probedevice(String ip, String username, String password) {
        Map<String, Object> result = new HashMap<>(6);
        Map<String, Object> map = new HashMap<>(6);
        if (StrUtil.isBlank(ip)) {
            List<String> ips = new ArrayList<>();
            for (URL url : DeviceDiscovery.discoverWsDevicesAsUrls()) {
                if (url.getPort() == -1 && Validator.isIpv4(url.getHost())) {
                    System.out.println("Device discovered: " + url.toString());
                    ips.add(url.getHost());
                }
            }
            map.put("ips", JSONUtil.toJsonStr(ips));
            result.put("code", "200");
            result.put("msg", "探询成功");
            result.put("result", map);
            return result;
        }

        if (!Validator.isIpv4(ip)) {
            result.put("code", "500");
            result.put("msg", "参数不合法_IP");
            result.put("result", map);
            return result;
        }
        try {
            //OnvifDevice device = new OnvifDevice("192.168.101.202", "admin", "abc123456");
            OnvifDevice device = new OnvifDevice(ip, username, password);
            List<Profile> profiles = device.getMediaService().getProfiles();
            if (profiles.size() != 0) {
                Profile profile = profiles.get(0);
                String rtspStreamUri = device.getMediaService().getRTSPStreamUri(profile.getToken());
                String serviceUrl = device.getDeviceManagementService().getServiceUrl();
                map.put("rtspStreamUri", rtspStreamUri);
                map.put("serviceUrl", serviceUrl);
                map.put("ip", ip);
                map.put("username", username);
                map.put("password", password);
                result.put("code", "200");
                result.put("msg", "成功");
                result.put("result", map);
                return result;
            } else {
                result.put("code", "200");
                result.put("msg", "查询失败");
                result.put("result", map);
                return result;
            }
        } catch (Exception e) {
            result.put("code", "500");
            result.put("msg", "查询失败");
            result.put("result", map);
            return result;
        }

    }

}
