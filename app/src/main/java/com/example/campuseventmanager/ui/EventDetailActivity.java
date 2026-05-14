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

public class EventDetailActivity extends AppCompatActivity {
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        TextView tvTitle = findViewById(R.id.detail_title);
        TextView tvTime = findViewById(R.id.detail_time);
        TextView tvDetail = findViewById(R.id.detail_content);
        TextView tvAddress = findViewById(R.id.detail_address);
        Button btnRemind = findViewById(R.id.btn_remind);
        Button btnNavigation = findViewById(R.id.btn_navigation);
        Button btnDelete = findViewById(R.id.btn_delete);

        EventEntity event = (EventEntity) getIntent().getSerializableExtra("EVENT_DATA");
        if (event != null) {
            tvTitle.setText(event.getTitle());
            tvTime.setText(event.getTime());
            tvDetail.setText(event.getDetail());
            if (event.getAddress() != null && !event.getAddress().isEmpty()) {
                tvAddress.setText("活动地点：" + event.getAddress());
            } else {
                tvAddress.setVisibility(View.GONE);
            }
        }

        // 点击提醒按钮
        btnRemind.setOnClickListener(v -> {
            // 先申请权限
            // 检查Android系统版本是否大于等于Android 13 (API级别33)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // 检查是否已经授予通知权限
                if (checkSelfPermission(
                        android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予通知权限，则请求该权限
                    ActivityCompat.requestPermissions(this,
                            new String[] { android.Manifest.permission.POST_NOTIFICATIONS },
                            REQUEST_NOTIFICATION_PERMISSION);
                    return; // 请求权限后直接返回，不执行后续代码
                }
            }

            // 权限通过 → 使用 WorkManager 调度通知任务
            Data inputData = new Data.Builder()
                    .putString("TITLE", event.getTitle())
                    .build();

            OneTimeWorkRequest reminderRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(this).enqueue(reminderRequest);
        });

        // 点击导航按钮
        btnNavigation.setOnClickListener(v -> {
            Intent intent = new Intent(this, RouteNavigationActivity.class);
            intent.putExtra("EVENT_DATA", event);
            startActivity(intent);
        });

        // 点击删除按钮
        btnDelete.setOnClickListener(v -> {
            if (event != null) {
                // 显示确认对话框
                new AlertDialog.Builder(this)
                        .setTitle("确认删除")
                        .setMessage("确定要删除这个活动吗？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 使用 EventRepository 删除事件
                            EventRepository eventRepository = new EventRepository(this);
                            eventRepository.deleteEvent(event);
                            // 删除成功后跳转到主活动，并重新加载事件列表
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