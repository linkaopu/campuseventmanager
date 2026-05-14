package com.example.campuseventmanager.storage;

import android.app.Application;
import com.tencent.mmkv.MMKV;

/**
 * MMKV管理器类
 * <p>
 * 封装MMKV（腾讯高性能键值存储库）的操作，提供统一的存储接口。
 * MMKV是基于mmap内存映射的key-value组件，性能远高于SharedPreferences。
 * </p>
 */
public class MMKVManager {

    /**
     * MMKV实例（单例）
     */
    private static MMKV mmkv;

    /**
     * 初始化MMKV
     * <p>
     * 必须在Application的onCreate中调用，初始化全局MMKV实例。
     * </p>
     *
     * @param application 应用程序上下文
     */
    public static void initialize(Application application) {
        MMKV.initialize(application);
        mmkv = MMKV.defaultMMKV();
    }

    /**
     * 获取MMKV实例
     *
     * @return MMKV实例
     */
    public static MMKV getInstance() {
        return mmkv;
    }

    /**
     * 存储字符串
     *
     * @param key   键
     * @param value 值
     */
    public static void putString(String key, String value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取字符串
     *
     * @param key          键
     * @param defaultValue 默认值（当键不存在时返回）
     * @return 存储的值或默认值
     */
    public static String getString(String key, String defaultValue) {
        return mmkv.decodeString(key, defaultValue);
    }

    /**
     * 存储整数
     *
     * @param key   键
     * @param value 值
     */
    public static void putInt(String key, int value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取整数
     *
     * @param key          键
     * @param defaultValue 默认值（当键不存在时返回）
     * @return 存储的值或默认值
     */
    public static int getInt(String key, int defaultValue) {
        return mmkv.decodeInt(key, defaultValue);
    }

    /**
     * 存储布尔值
     *
     * @param key   键
     * @param value 值
     */
    public static void putBoolean(String key, boolean value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取布尔值
     *
     * @param key          键
     * @param defaultValue 默认值（当键不存在时返回）
     * @return 存储的值或默认值
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return mmkv.decodeBool(key, defaultValue);
    }

    /**
     * 存储长整数
     *
     * @param key   键
     * @param value 值
     */
    public static void putLong(String key, long value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取长整数
     *
     * @param key          键
     * @param defaultValue 默认值（当键不存在时返回）
     * @return 存储的值或默认值
     */
    public static long getLong(String key, long defaultValue) {
        return mmkv.decodeLong(key, defaultValue);
    }

    /**
     * 删除指定键
     *
     * @param key 要删除的键
     */
    public static void remove(String key) {
        mmkv.removeValueForKey(key);
    }

    /**
     * 清空所有数据
     */
    public static void clear() {
        mmkv.clearAll();
    }

    /**
     * 判断键是否存在
     *
     * @param key 要检查的键
     * @return true: 存在，false: 不存在
     */
    public static boolean contains(String key) {
        return mmkv.contains(key);
    }
}
