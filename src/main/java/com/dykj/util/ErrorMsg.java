package com.dykj.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 系统错误信息储存
 */
@Component
public class ErrorMsg {
    private static ConcurrentMap<String, String> concurrentMsgMap = new ConcurrentHashMap<String, String>(20);

    /**
     * 新增
     *
     * @param key
     * @param value
     */
    public void putMsg(String key, String value) {
        concurrentMsgMap.put(key, value);
    }

    /**
     * 获取
     *
     * @param key
     */
    public void getMsg(String key) {
        concurrentMsgMap.get(key);
    }

    /**
     * 移除
     *
     * @param key
     */
    public void removeMsg(String key) {
        concurrentMsgMap.remove(key);
    }

    /**
     * 所有
     *
     * @return
     */
    public ConcurrentMap<String, String> getAllMsg() {
        return concurrentMsgMap;
    }

}
