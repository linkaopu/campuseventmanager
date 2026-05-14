package com.example.campuseventmanager;

import android.app.Application;
import com.amap.api.location.AMapLocationClient;
import com.example.campuseventmanager.storage.MMKVManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 MMKV
        MMKVManager.initialize(this);
        // 设置高德地图隐私政策（新版SDK必需）
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
    }
}