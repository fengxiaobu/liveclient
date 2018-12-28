package com.dykj.util;

import cn.hutool.core.lang.Validator;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 判断网络连接状况.
 */
@Component
public class NetStateUtil {

    public static void main(String[] args) {
        NetStateUtil netStateUtil = new NetStateUtil();
        System.out.println(netStateUtil.isConnect("192.168.101.224"));
    }

    /**
     * 判断服务是否可用
     *
     * @param ip
     * @return
     */
    public boolean isConnect(String ip) {
        boolean connect = false;
        Runtime runtime = Runtime.getRuntime();
        Process process;
        try {
            if (!Validator.isIpv4(ip)) {
                return false;
            }
            process = runtime.exec("ping " + ip);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, Charset.forName("utf-8"));
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            isr.close();
            br.close();
            if (null != sb && !"".equals(sb.toString())) {
                // 网络畅通
                // 网络不畅通
                connect = sb.toString().indexOf("TTL") > 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            connect = false;
        }
        return connect;
    }
}