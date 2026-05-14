package com.example.campuseventmanager.storage;

import android.app.Application;
import com.tencent.mmkv.MMKV;

public class MMKVManager {
    private static MMKV mmkv;

    public static void initialize(Application application) {
        MMKV.initialize(application);
        mmkv = MMKV.defaultMMKV();
    }

    public static MMKV getInstance() {
        return mmkv;
    }

    // 存储字符串
    public static void putString(String key, String value) {
        mmkv.encode(key, value);
    }

    // 获取字符串
    public static String getString(String key, String defaultValue) {
        return mmkv.decodeString(key, defaultValue);
    }

    // 存储整数
    public static void putInt(String key, int value) {
        mmkv.encode(key, value);
    }

    // 获取整数
    public static int getInt(String key, int defaultValue) {
        return mmkv.decodeInt(key, defaultValue);
    }

    // 存储布尔值
    public static void putBoolean(String key, boolean value) {
        mmkv.encode(key, value);
    }

    // 获取布尔值
    public static boolean getBoolean(String key, boolean defaultValue) {
        return mmkv.decodeBool(key, defaultValue);
    }

    // 存储长整数
    public static void putLong(String key, long value) {
        mmkv.encode(key, value);
    }

    // 获取长整数
    public static long getLong(String key, long defaultValue) {
        return mmkv.decodeLong(key, defaultValue);
    }

    // 删除指定键
    public static void remove(String key) {
        mmkv.removeValueForKey(key);
    }

    // 清空所有数据
    public static void clear() {
        mmkv.clearAll();
    }

    // 判断键是否存在
    public static boolean contains(String key) {
        return mmkv.contains(key);
    }
}