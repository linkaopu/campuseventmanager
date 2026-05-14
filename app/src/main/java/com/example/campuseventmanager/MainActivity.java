package com.example.campuseventmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.campuseventmanager.fragment.EventListFragment;
import com.example.campuseventmanager.fragment.AddEventFragment;
import com.example.campuseventmanager.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
// 设置底部导航栏的选中项变化监听器
// 当用户点击不同的导航项时，会触发监听器中的回调方法
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // 检查savedInstanceState是否为null，这表示是首次创建活动
        if (savedInstanceState == null) {
            // 获取FragmentManager并开始一个事务
            getSupportFragmentManager().beginTransaction()
                    // 用EventListFragment替换fragment_container中的现有片段
                    .replace(R.id.fragment_container, new EventListFragment())
                    // 提交事务，完成片段的替换
                    .commit();
        }
    }

    // 底部导航栏的监听器，用于处理导航项的选择事件
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                // 初始化选中的Fragment为null
                Fragment selectedFragment = null;
                // 获取被点击菜单项的ID
                int id = item.getItemId();

                // 根据不同的菜单项ID创建对应的Fragment实例
                if (id == R.id.nav_events) {
                    // 如果点击的是"事件"菜单项，创建事件列表Fragment
                    selectedFragment = new EventListFragment();
                } else if (id == R.id.nav_add) {
                    // 如果点击的是"添加"菜单项，创建添加事件Fragment
                    selectedFragment = new AddEventFragment();
                } else if (id == R.id.nav_settings) {
                    // 如果点击的是"设置"菜单项，创建设置Fragment
                    selectedFragment = new SettingsFragment();
                }

                // 如果选中的Fragment不为null，则进行Fragment的切换
                if (selectedFragment != null) {
                    // 获取FragmentManager，开始事务，替换当前容器中的Fragment，并提交事务
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                // 返回true表示已处理该菜单项的点击事件
                return true;
            };
}