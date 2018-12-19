package com.dykj.live.controller;

import cn.hutool.core.date.DateUtil;
import com.dykj.util.ErrorMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ConcurrentMap;

@Controller
public class IndexController {

    @Autowired
    ErrorMsg errorMsg;

    @RequestMapping("/index")
    @ResponseBody
    public String index() {
        ConcurrentMap<String, String> allMsg = errorMsg.getAllMsg();
        StringBuffer sb = new StringBuffer();
        allMsg.forEach((k, v) -> sb.append("错误级别").append(v).append("  信息").append(v).append("\n"));
        if (allMsg.size() > 0) {
            return sb.append("客户端启动失败-----------></br>").append(DateUtil.now()).toString();
        } else {
            return sb.append("客户端启动成功-----------></br>").append(DateUtil.now()).toString();
        }
    }

    @RequestMapping("/play")
    public String pay() {
        return "play";
    }

    @RequestMapping("/ckplayer")
    public String ckplayer() {
        return "ckplayer";
    }

    @RequestMapping("/editplayer")
    public String editplayer() {
        return "editplayer";
    }
}
