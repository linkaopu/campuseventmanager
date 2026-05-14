package com.example.campuseventmanager.storage;

import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用户点击行为统计管理器
 * <p>
 * 负责记录用户对活动的点击行为，实现点击去重功能。
 * 核心逻辑：同一活动在一分钟内多次点击只记录一次，避免重复统计。
 * </p>
 */
public class ClickStatisticsManager {

    /**
     * 单例实例
     */
    private static ClickStatisticsManager instance;

    /**
     * 点击记录数据访问对象
     */
    private final ClickRecordDao clickRecordDao;

    /**
     * 后台执行器服务（单线程）
     */
    private final ExecutorService executorService;

    /**
     * 去重时间间隔：60秒（一分钟内同一活动多次点击只记录一次）
     */
    private static final long DEBOUNCE_INTERVAL = 60 * 1000;

    /**
     * 私有构造函数
     * <p>
     * 初始化数据库连接和执行器服务。
     * </p>
     *
     * @param context 上下文
     */
    private ClickStatisticsManager(Context context) {
        EventDatabase db = EventDatabase.getInstance(context);
        clickRecordDao = db.clickRecordDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 获取单例实例
     * <p>
     * 使用双重检查锁定确保线程安全。
     * </p>
     *
     * @param context 上下文
     * @return ClickStatisticsManager实例
     */
    public static synchronized ClickStatisticsManager getInstance(Context context) {
        if (instance == null) {
            instance = new ClickStatisticsManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 处理点击事件，包含去重逻辑
     * <p>
     * 核心算法：
     * 1. 查询活动对应的点击记录
     * 2. 如果记录不存在，创建新记录
     * 3. 如果记录存在且距离上次点击超过60秒，更新点击次数和时间
     * 4. 如果记录存在但距离上次点击不足60秒，忽略此次点击
     * </p>
     *
     * @param eventId 活动ID，用于标识唯一活动
     */
    public void recordClick(String eventId) {
        executorService.execute(() -> {
            // 查询对应活动的 ClickRecord
            ClickRecord record = clickRecordDao.findByEventId(eventId);
            long currentTime = System.currentTimeMillis();

            if (record == null) {
                // 记录不存在，创建新记录
                ClickRecord newRecord = new ClickRecord();
                newRecord.setEventId(eventId);
                newRecord.setLastClickTime(currentTime);
                newRecord.setClickCount(1);
                clickRecordDao.insertClickRecord(newRecord);
            } else {
                // 记录已存在，比较时间差值
                long timeDiff = currentTime - record.getLastClickTime();

                if (timeDiff >= DEBOUNCE_INTERVAL) {
                    // 时间差值大于等于60秒，点击次数加1并更新时间
                    record.setClickCount(record.getClickCount() + 1);
                    record.setLastClickTime(currentTime);
                    clickRecordDao.updateClickRecord(record);
                }
                // 时间差值小于60秒，忽略此次点击
            }
        });
    }

    /**
     * 获取指定活动的点击次数
     * <p>
     * 注意：此方法在调用线程执行，可能阻塞UI线程，建议在后台线程调用。
     * </p>
     *
     * @param eventId 活动ID
     * @return 点击次数（如果记录不存在返回0）
     */
    public int getClickCount(String eventId) {
        ClickRecord record = clickRecordDao.findByEventId(eventId);
        return record != null ? record.getClickCount() : 0;
    }

    /**
     * 获取指定活动的上次点击时间
     * <p>
     * 注意：此方法在调用线程执行，可能阻塞UI线程，建议在后台线程调用。
     * </p>
     *
     * @param eventId 活动ID
     * @return 上次点击时间戳（毫秒），如果记录不存在返回0
     */
    public long getLastClickTime(String eventId) {
        ClickRecord record = clickRecordDao.findByEventId(eventId);
        return record != null ? record.getLastClickTime() : 0;
    }

    /**
     * 清除所有点击记录
     * <p>
     * 在后台线程执行清空操作。
     * </p>
     */
    public void clearAllRecords() {
        executorService.execute(() -> {
            clickRecordDao.deleteAllClickRecords();
        });
    }
}
