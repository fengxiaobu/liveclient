/**
 * 文件名：ResultDataUtil.java
 * 描述：提供方便操作的工具方法
 * 修改人：eguid
 * 修改时间：2016年6月30日
 * 修改内容：
 */
package com.dykj.util;


import cn.hutool.core.lang.Validator;
import com.dykj.live.entity.ResultData;

/**
 * 方便操作的工具类
 *
 * @author eguid
 * @version 2016年6月30日
 * @see ResultDataUtil
 * @since jdk1.7
 */

public class ResultDataUtil {
    public static void setData(ResultData result, String status, String msg, Object data) {
        if (status != null)
            result.setStatus(status);
        if (msg != null)
            result.setMsg(msg);
        if (data != null)
            result.setData(data);
    }

    /**
     * 检查字符串不能包含中文字符
     *
     * @return true：是；false：否
     */
    public static boolean checkStringNoCN(String element) {
        String regex = "[a-zA-Z0-9]+";
        return element.matches(regex);
    }

    /**
     * 检查字符串是否是链接地址
     *
     * @param element
     * @return true：是 ；false：否
     */
    public static boolean checkURLNoCN(String element) {
        String regex = "[a-zA-Z0-9\\-_/@:.?%&=]+";
        return element.matches(regex);
        //return true;
    }

    /**
     * 去除字符串两端空格
     *
     * @param str
     */
    public static void trim(String... str) {
        for (String s : str) {
            s = s.trim();
        }

    }

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {

        System.out.println(checkURLNoCN("rtmp://222.212.90.164:10085/hls/p8IktCfmR?sign=t8SzpCfmRz"));
        System.out.println(Validator.isUrl("rtmp://222.212.90.164:10085/hls/p8IktCfmR?sign=t8SzpCfmRz"));
        boolean b = Validator.isUrl("rtsp://192.168.0.64:554/h264/ch1/main/av_stream");
        System.out.println(b);
        /*ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("java", "-version");
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
        String temp;
        while ((temp = bufferedReader.readLine()) != null) {
            System.out.println(new String(temp.getBytes("utf-8"), "utf-8"));
        }*/
    }
}
