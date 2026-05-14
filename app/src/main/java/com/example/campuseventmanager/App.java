package com.example.campuseventmanager;

import android.app.Application;
import com.amap.api.location.AMapLocationClient;
import com.example.campuseventmanager.storage.MMKVManager;

/**
 * 应用程序入口类
 * <p>
 * 负责在应用启动时初始化必要的组件和服务，包括：
 * - MMKV 高性能键值存储初始化
 * - 高德地图 SDK 隐私政策设置
 * </p>
 */
public class App extends Application {

    /**
     * 应用启动时调用的初始化方法
     * <p>
     * 在应用进程创建时执行，完成全局初始化工作：
     * 1. 初始化 MMKV，替代 SharedPreferences 作为轻量级存储
     * 2. 设置高德地图隐私政策，这是新版高德 SDK 的必需步骤
     * </p>
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 MMKV 高性能键值存储
        MMKVManager.initialize(this);
        
        // 设置高德地图隐私政策（新版SDK必需）
        // updatePrivacyShow: 设置是否显示隐私政策弹窗
        // updatePrivacyAgree: 设置用户是否同意隐私政策
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
    }
}
