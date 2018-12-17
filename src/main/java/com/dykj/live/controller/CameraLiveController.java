/**
 * 文件名：CameraLiveController.java 描述：提供实时直播推送服务 修改人：eguid 修改时间：2016年6月30日 修改内容：
 */
package com.dykj.live.controller;


import com.dykj.live.entity.LiveInfoEntity;
import com.dykj.live.entity.ResultData;
import com.dykj.live.service.CameraLiveService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


/**
 * 提供REST形式的实时直播推送，查看和关闭服务
 *
 * @author eguid
 * @version 2016年6月30日
 * @see CameraLiveController
 * @since jdk1.7
 */
@Controller
@RequestMapping("/live")
public class CameraLiveController {
    @Resource(name = "cameraLiveService")
    private CameraLiveService cls;

    public void setCls(CameraLiveService cls) {
        this.cls = cls;
    }

    @ResponseBody
    @RequestMapping(value = "/push/{appName}", method = RequestMethod.POST)
    public ResultData open(@PathVariable("appName") String appName, LiveInfoEntity liveInfo) {
        return cls.add(liveInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/close/{appName}", method = RequestMethod.DELETE)
    public ResultData close(@PathVariable("appName") String appName) {
        return cls.remove(appName);
    }

    @ResponseBody
    @RequestMapping(value = "/view/{appName}", method = RequestMethod.GET)
    public ResultData view(@PathVariable("appName") String appName) {
        return cls.view(appName);
    }

    @ResponseBody
    @RequestMapping(value = "/viewAll", method = RequestMethod.GET)
    public ResultData view() {
        return cls.viewAll();
    }
}
