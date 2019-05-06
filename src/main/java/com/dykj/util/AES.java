package com.dykj.util;

import cn.hutool.core.codec.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Administrator
 */
public class AES {

    // 加密
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

        return Base64.encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        /*
         * 此处使用AES-128-ECB加密模式，key需要为16位。
         */
        String cKey = "be2a4614bca9dfd8eff3383c264a81ff";
        // 需要加密的字串
        String cSrc = "eh80sZpUwn+lmLISUjlcppR3QBtHXU4se9JK7/qX6+f0qkm2P7qxPYWQ++gHSDnLw7eknkEFX8QrLixYqdmxUmRsQq8xU2subDiWDzVND4m1lf2WqmSTzQbnbz45Tp3H1Ev0QEbgF6bTlMnSOQhvpWqDaEbqt6bQVaeckmF4kEp50iOamimLulXMW7bnp1JckAQBH/jfWvCpGgP2tKMfW5F8AWZyuukTbZumLQFoZvCHPHDz8a8t6rmmHzogw93vii8xRSOA393xwl0IW5NWf1OQx9B/FZxIwnr9pa+w3lEpu19wOlB+KfkAczU9CEOTJF8Mk/R5MQcHfvKjXByVCqX/wpGYpyH9PqYpR0ECmc5vD5flsoqpJlsH8fk/ItF9UaoymaGcGV/SsegDyc2KJoYrvG8VEZZTcKygesTr/USAhkN9e173vjB0BhRlfotmeYtxK7xKGs0J060SQ7/bRZtQ9ZK+twY+3Iv0eU7MXwJ65qc+ec7+S7aiQ5V+O8FVPQvywxrNGwHOuZjDidr99ifHhdOM3p/foY1lc9bThRnJz11SVKeuzraehNrt5LpeJRlnjjwuJ+9m9qR28moGHOOweBlLvoAU0Wnhf+NQYLTqdOd2DMxNt4YpNgncJIb2Uu7ro4jacrLGTZZVEdgiqgq4EPVsyZ4s1VWkoj8TdHSceLKB6E2eYvnyUE0GxKi7t7QrZdcSrp6HLyuMK+jD97mJe2v8QpWPPEY/WZB9hzesa1vpG0ie9v86YhqQSM9A7JRlTSNEGXm1PtNaYihTlDMznH4e63f6vr/yMQMP9lJO7qfWk5PiYh/bWPes+NyEmtqi2iFjy/1wg/VKn2Raz4DjiQ0S+JYgSglBLu5rZus58OeH6AYvwPHuYlwVXAwb";
        System.out.println(cSrc);
        // 加密
        String enString = AES.Encrypt(cSrc, cKey);
        System.out.println("加密后的字串是：" + enString);

        // 解密
        String DeString = AES.Decrypt(cSrc, cKey);
        System.out.println("解密后的字串是：" + DeString);
    }
}

//源代码片段来自云代码http://yuncode.net