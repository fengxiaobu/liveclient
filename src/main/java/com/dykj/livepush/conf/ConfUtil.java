/**
 * 文件名：ConfUtil.java 描述：读取配置文件属性 修改人：eguid 修改时间：2016年7月8日 修改内容：
 */
package com.dykj.livepush.conf;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.dykj.util.ErrorMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
    //private volatile static String ffmpegPath = "ffmpeg";
    private volatile static String ffmpegPath = "";
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
       /* try {
            String exec = RuntimeUtil.execForStr("ffmpeg  -version");
            if (StrUtil.containsAny(exec, "version")) {
                isHave = true;
            }
            log.info("预加载FFMPEG配置:{}", isHave() ? "加载ffmpeg成功！" : "加载ffmpeg失败！");
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg.putMsg("msg", "加载ffmpeg失败！");
            log.error("执行<ffmpeg  -version>的时候发生错误,错误原因:{}", e.getMessage());
        }*/

        try {
            int count;
            byte[] buf = new byte[1024];
            //获取临时目录
            String tmpDir = FileUtil.getTmpDirPath() + "/ffmpeg";
            String[] dlls = new String[]{"avcodec-57.dll", "avdevice-57.dll", "avfilter-6.dll", "avformat-57.dll", "avutil-55.dll", "ffmpeg.exe", "ffplay.exe", "ffprobe.exe", "postproc-54.dll", "swresample-2.dll", "swscale-4.dll"};
            String platformPrefix = "ffmpeg";
            for (int i = 0; i < dlls.length; ++i) {
                String dllName = dlls[i];
                File fdll = new File(tmpDir, dllName);
                if (!FileUtil.exist(fdll)) {
                    try (InputStream in = ConfUtil.class.getClassLoader()
                            .getResourceAsStream(platformPrefix + "/" + dllName)) {
                        try (BufferedOutputStream out = FileUtil.getOutputStream(fdll)) {
                            long copySize = IoUtil.copy(in, out, IoUtil.DEFAULT_MIDDLE_BUFFER_SIZE);
                        }
                    }
                    // fdll.deleteOnExit();
                }
            }
            ffmpegPath = tmpDir + "/ffmpeg";
        } catch (IOException e) {
            e.printStackTrace();
        }

        //String path = getClass().getResource("/").getPath() + "ffmpeg/ffmpeg.exe";
        System.out.print("预加载FFMPEG配置:" + ffmpegPath);
        //File ffmpeg = new File(ffmpegPath);
        //ffmpegPath = ffmpeg.getPath();
        if (isHave = true) {
            System.out.println("加载ffmpeg成功！");
        } else {
            System.out.println("加载ffmpeg失败！");
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
