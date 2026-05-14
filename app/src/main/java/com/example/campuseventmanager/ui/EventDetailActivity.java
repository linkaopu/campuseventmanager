package com.example.campuseventmanager.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.campuseventmanager.MainActivity;
import com.example.campuseventmanager.R;
import com.example.campuseventmanager.storage.EventEntity;
import com.example.campuseventmanager.storage.EventRepository;
import com.example.campuseventmanager.workmanager.ReminderWorker;

/**
 * 活动详情Activity
 * <p>
 * 负责展示活动的详细信息，支持以下功能：
 * - 显示活动标题、时间、详情、地点
 * - 设置活动提醒（使用WorkManager调度）
 * - 步行导航（跳转到RouteNavigationActivity）
 * - 删除活动
 * </p>
 */
public class EventDetailActivity extends AppCompatActivity {

    /**
     * 通知权限请求码
     */
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    /**
     * 创建活动时的初始化方法
     * <p>
     * 负责：
     * 1. 设置布局
     * 2. 获取活动数据并显示
     * 3. 设置按钮点击事件监听
     * </p>
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // 获取界面控件引用
        TextView tvTitle = findViewById(R.id.detail_title); // 活动标题
        TextView tvTime = findViewById(R.id.detail_time); // 活动时间
        TextView tvDetail = findViewById(R.id.detail_content); // 活动详情
        TextView tvAddress = findViewById(R.id.detail_address); // 活动地点
        Button btnRemind = findViewById(R.id.btn_remind); // 提醒按钮
        Button btnNavigation = findViewById(R.id.btn_navigation); // 导航按钮
        Button btnDelete = findViewById(R.id.btn_delete); // 删除按钮

        // 获取从列表传递过来的活动数据
        EventEntity event = (EventEntity) getIntent().getSerializableExtra("EVENT_DATA");

        if (event != null) {
            // 显示活动信息
            tvTitle.setText(event.getTitle());
            tvTime.setText(event.getTime());
            tvDetail.setText(event.getDetail());

            // 如果有地址信息则显示，否则隐藏
            if (event.getAddress() != null && !event.getAddress().isEmpty()) {
                tvAddress.setText("活动地点：" + event.getAddress());
            } else {
                tvAddress.setVisibility(View.GONE);
            }
        }

        // 提醒按钮点击事件
        btnRemind.setOnClickListener(v -> {
            // Android 13及以上版本需要请求通知权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(
                        android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[] { android.Manifest.permission.POST_NOTIFICATIONS },
                            REQUEST_NOTIFICATION_PERMISSION);
                    return;
                }
            }

            // 权限通过后，使用WorkManager调度通知任务
            Data inputData = new Data.Builder()
                    .putString("TITLE", event.getTitle())
                    .build();

            OneTimeWorkRequest reminderRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(this).enqueue(reminderRequest);
        });

        // 导航按钮点击事件：跳转到步行导航页面
        btnNavigation.setOnClickListener(v -> {
            Intent intent = new Intent(this, RouteNavigationActivity.class);
            intent.putExtra("EVENT_DATA", event);
            startActivity(intent);
        });

        // 删除按钮点击事件：显示确认对话框并删除活动
        btnDelete.setOnClickListener(v -> {
            if (event != null) {
                new AlertDialog.Builder(this)
                        .setTitle("确认删除")
                        .setMessage("确定要删除这个活动吗？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 删除活动
                            EventRepository eventRepository = new EventRepository(this);
                            eventRepository.deleteEvent(event);

                            // 返回主页面
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }
}
