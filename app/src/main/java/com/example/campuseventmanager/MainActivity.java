package com.example.campuseventmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.campuseventmanager.fragment.EventListFragment;
import com.example.campuseventmanager.fragment.AddEventFragment;
import com.example.campuseventmanager.fragment.SettingsFragment;

/**
 * 主活动类
 * <p>
 * 作为应用的主入口，负责管理底部导航栏和Fragment切换。
 * 包含三个主要功能模块：
 * - 活动列表（EventListFragment）
 * - 添加活动（AddEventFragment）
 * - 设置（SettingsFragment）
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 活动创建时的初始化方法
     * <p>
     * 负责：
     * 1. 设置布局文件
     * 2. 初始化底部导航栏
     * 3. 设置导航监听器
     * 4. 默认显示活动列表Fragment
     * </p>
     *
     * @param savedInstanceState 保存的实例状态，用于屏幕旋转等场景恢复状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取底部导航栏控件
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        
        // 设置底部导航栏的选中项变化监听器
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // 检查savedInstanceState是否为null，这表示是首次创建活动
        // 如果不是首次创建（如屏幕旋转），Fragment会自动恢复，不需要重新创建
        if (savedInstanceState == null) {
            // 默认显示活动列表Fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EventListFragment())
                    .commit();
        }
    }

    /**
     * 底部导航栏的监听器
     * <p>
     * 根据用户点击的导航项切换对应的Fragment：
     * - R.id.nav_events: 活动列表
     * - R.id.nav_add: 添加活动
     * - R.id.nav_settings: 设置
     * </p>
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        // 初始化选中的Fragment为null
        Fragment selectedFragment = null;
        
        // 获取被点击菜单项的ID
        int id = item.getItemId();

        // 根据不同的菜单项ID创建对应的Fragment实例
        if (id == R.id.nav_events) {
            // 活动列表Fragment
            selectedFragment = new EventListFragment();
        } else if (id == R.id.nav_add) {
            // 添加活动Fragment
            selectedFragment = new AddEventFragment();
        } else if (id == R.id.nav_settings) {
            // 设置Fragment
            selectedFragment = new SettingsFragment();
        }

        // 如果选中的Fragment不为null，则进行Fragment的切换
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }
        
        // 返回true表示已处理该菜单项的点击事件
        return true;
    };
}
