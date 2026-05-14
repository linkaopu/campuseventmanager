package com.example.campuseventmanager.storage;

import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClickStatisticsManager {
    private static ClickStatisticsManager instance;
    private final ClickRecordDao clickRecordDao;
    private final ExecutorService executorService;
    
    // 去重时间间隔：60秒
    private static final long DEBOUNCE_INTERVAL = 60 * 1000;

    private ClickStatisticsManager(Context context) {
        EventDatabase db = EventDatabase.getInstance(context);
        clickRecordDao = db.clickRecordDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized ClickStatisticsManager getInstance(Context context) {
        if (instance == null) {
            instance = new ClickStatisticsManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 处理点击事件，包含去重逻辑
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
     * 
     * @param eventId 活动ID
     * @return 点击次数
     */
    public int getClickCount(String eventId) {
        ClickRecord record = clickRecordDao.findByEventId(eventId);
        return record != null ? record.getClickCount() : 0;
    }

    /**
     * 获取指定活动的上次点击时间
     * 
     * @param eventId 活动ID
     * @return 上次点击时间戳
     */
    public long getLastClickTime(String eventId) {
        ClickRecord record = clickRecordDao.findByEventId(eventId);
        return record != null ? record.getLastClickTime() : 0;
    }

    /**
     * 清除所有点击记录
     */
    public void clearAllRecords() {
        executorService.execute(() -> {
            clickRecordDao.deleteAllClickRecords();
        });
    }
}