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

/**
 * WorkManager提醒任务类
 * <p>
 * 负责在后台执行活动提醒通知的发送任务。
 * 使用WorkManager调度，可以保证任务在合适的时机执行，
 * 即使应用处于后台或设备重启后也能正常运行。
 * </p>
 */
public class ReminderWorker extends Worker {

    /**
     * 通知渠道ID（Android 8.0+必需）
     */
    private static final String CHANNEL_ID = "EVENT_REMINDER_CHANNEL";

    /**
     * 通知渠道名称
     */
    private static final String CHANNEL_NAME = "校园活动提醒";

    /**
     * 通知ID
     */
    private static final int NOTIFICATION_ID = 100;

    /**
     * 构造函数
     *
     * @param context      上下文
     * @param workerParams 工作参数
     */
    public ReminderWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    /**
     * 执行工作任务的方法
     * <p>
     * 从输入数据中获取活动标题，创建并发送通知。
     * </p>
     *
     * @return 返回执行结果
     */
    @Override
    public Result doWork() {
        // 从输入数据中获取活动标题
        String eventTitle = getInputData().getString("TITLE");

        // 创建通知渠道（Android 8.0+必需）
        createNotificationChannel();

        // 构建通知对象
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)          // 设置通知小图标
                .setContentTitle("活动提醒")                   // 设置通知标题
                .setContentText(eventTitle + " 即将开始！")    // 设置通知内容
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 设置高优先级
                .setAutoCancel(true)                          // 点击后自动取消
                .build();

        // 获取通知管理器并发送通知
        NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, notification);

        // 返回成功结果
        return Result.success();
    }

    /**
     * 创建通知渠道
     * <p>
     * 仅在Android 8.0 (API 26)及以上版本中需要创建通知渠道。
     * </p>
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,                    // 渠道ID
                    CHANNEL_NAME,                  // 渠道名称
                    NotificationManager.IMPORTANCE_HIGH // 重要性级别
            );
            channel.setDescription("用于显示校园活动提醒");
            
            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
