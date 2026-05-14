package com.example.campuseventmanager.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.campuseventmanager.R;
import static android.content.Context.MODE_PRIVATE;

/**
 * 设置Fragment
 * <p>
 * 负责应用设置界面，当前支持：
 * - 日间/夜间模式切换
 * </p>
 */
public class SettingsFragment extends Fragment {

    /**
     * SharedPreferences文件名
     */
    private static final String PREF_NAME = "ThemePref";

    /**
     * 夜间模式设置的键名
     */
    private static final String PREF_KEY = "NightMode";

    /**
     * 创建视图时调用
     * <p>
     * 负责：
     * 1. 加载布局
     * 2. 获取夜间模式开关控件
     * 3. 从SharedPreferences读取当前设置
     * 4. 设置开关状态改变监听器
     * </p>
     *
     * @param inflater           布局加载器
     * @param container          父容器
     * @param savedInstanceState 保存的实例状态
     * @return Fragment视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        // 获取夜间模式开关控件
        Switch switchNight = view.findViewById(R.id.switch_night);

        // 获取SharedPreferences对象，用于存储夜间模式设置
        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        
        // 从SharedPreferences中读取夜间模式设置状态
        boolean isNight = prefs.getBoolean(PREF_KEY, false);
        
        // 设置开关的初始状态
        switchNight.setChecked(isNight);

        // 设置开关状态改变监听器
        switchNight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 获取SharedPreferences编辑器
            SharedPreferences.Editor editor = prefs.edit();
            
            // 保存新的夜间模式设置状态
            editor.putBoolean(PREF_KEY, isChecked);
            
            // 应用保存的更改
            editor.apply();

            // 根据开关状态设置夜间模式
            if (isChecked) {
                // 启用夜间模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // 关闭夜间模式（日间模式）
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            
            // 重建活动以应用主题更改
            if (getActivity() != null) {
                getActivity().recreate();
            }
        });

        return view;
    }
}
