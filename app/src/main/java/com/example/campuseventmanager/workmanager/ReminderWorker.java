package com.example.campuseventmanager.workmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.campuseventmanager.R;

public class ReminderWorker extends Worker {
    private static final String CHANNEL_ID = "EVENT_REMINDER_CHANNEL";
    private static final String CHANNEL_NAME = "校园活动提醒";
    private static final int NOTIFICATION_ID = 100;

    public ReminderWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    /**
     * 执行工作任务的方法，用于显示活动提醒通知
     * 
     * @return 返回执行结果，成功时返回Result.success()
     */
    @Override
    public Result doWork() {
        // 从输入数据中获取活动标题
        String eventTitle = getInputData().getString("TITLE");

        // 创建通知渠道，确保通知可以正常显示
        createNotificationChannel();

        // 构建通知对象
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                // 设置通知的小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                // 设置通知的标题
                .setContentTitle("活动提醒")
                // 设置通知的内容文本
                .setContentText(eventTitle + " 即将开始！")
                // 设置通知的优先级为高
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // 设置点击通知后自动取消
                .setAutoCancel(true)
                .build();

        // 获取通知管理器
        NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
        // 发送通知
        manager.notify(NOTIFICATION_ID, notification);

        // 返回成功结果
        return Result.success();
    }

    /**
     * 创建通知渠道的方法
     * 仅在Android 8.0 (API 26及以上版本)系统中需要创建通知渠道
     */
    private void createNotificationChannel() {
        // 检查系统版本是否为Android 8.0或更高
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建通知渠道对象
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, // 渠道ID
                    CHANNEL_NAME, // 渠道名称
                    NotificationManager.IMPORTANCE_HIGH // 重要性级别
            );
            // 设置通知渠道的描述信息
            channel.setDescription("用于显示校园活动提醒");
            // 获取通知管理器
            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            // 创建通知渠道
            manager.createNotificationChannel(channel);
        }
    }
}