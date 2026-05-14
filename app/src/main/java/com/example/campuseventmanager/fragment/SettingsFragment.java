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

public class SettingsFragment extends Fragment {
    private static final String PREF_NAME = "ThemePref";
    private static final String PREF_KEY = "NightMode";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        // 通过ID查找夜间模式开关视图
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
                // 关闭夜间模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            // 重建活动以应用主题更改
            if (getActivity() != null)
                getActivity().recreate();
        });

        return view;
    }
}