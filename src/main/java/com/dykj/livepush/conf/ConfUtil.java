/**
 * 文件名：ConfUtil.java 描述：读取配置文件属性 修改人：eguid 修改时间：2016年7月8日 修改内容：
 */
package com.dykj.livepush.conf;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import com.dykj.util.ErrorMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 读取配置文件
 *
 * @author eguid
 * @version 2016年7月8日
 * @see ConfUtil
 * @since jdk1.7
 */
@Component
public class ConfUtil {
    private volatile static boolean isHave = false;
    private volatile static String ffmpegPath = "ffmpeg";
    private static Logger log = LoggerFactory.getLogger(ConfUtil.class);

    @Autowired
    ErrorMsg errorMsg;
    public ConfUtil() {
        initConfInfo();
    }

    /**
     * 判断是否支持ffmpeg环境
     */
    private void initConfInfo() {
        try {
            String exec = RuntimeUtil.execForStr("ffmpeg  -version");
            if (StrUtil.containsAny(exec, "version")) {
                isHave = true;
            }
            log.info("预加载FFMPEG配置:{}", isHave() ? "加载ffmpeg成功！" : "加载ffmpeg失败！");
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg.putMsg("msg", "加载ffmpeg失败！");
            log.error("执行<ffmpeg  -version>的时候发生错误,错误原因:{}", e.getMessage());
        }
    }

    /**
     * 判断ffmpeg环境配置
     *
     * @return true：配置成功；false：配置失败
     */
    public boolean isHave() {
        return isHave;
    }

    /**
     * 获取ffmpeg环境调用地址
     * 添加方法功能描述
     *
     * @return
     */
    public String getPath() {
        return ffmpegPath;
    }
}
