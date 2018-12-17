package com.dykj.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.setting.Setting;

/**
 * 程序内使用同一个句柄,已连接的句柄
 * @author sjy
 */
public class UidManage {

    private final static String uid;
    private static volatile UidManage instance;

    static {
        String config = FileUtil.getAbsolutePath("config");
        String path = config + "/cfg.setting";
        boolean absolutePath = FileUtil.exist(path);
        if (!absolutePath) {
            uid = IdUtil.simpleUUID();
            FileUtil.touch(path);
            Setting setting = new Setting(path);
            setting.set("name", uid);
            setting.store(path);
            System.out.println(setting.get("name"));
        } else {
            Setting setting = new Setting(path);
            uid = setting.get("name");
        }
    }

    private UidManage() {
    }

    /**
     * 单例模式
     */
    public static UidManage getInstance() {
        if (instance == null) {
            synchronized (UidManage.class) {
                if (instance == null) {
                    instance = new UidManage();
                }
            }
        }
        return instance;
    }

    /**
     * 获取句柄
     */
    public String gettUid() {
        return uid;
    }

}
