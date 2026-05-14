package com.example.campuseventmanager.storage;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * 活动数据库类
 * <p>
 * Room数据库的主类，管理数据库的创建和访问。
 * 包含两个表：
 * - events: 存储活动信息
 * - click_records: 存储用户点击行为记录
 * </p>
 */
@Database(entities = {EventEntity.class, ClickRecord.class}, version = 3, exportSchema = false)
public abstract class EventDatabase extends RoomDatabase {

    /**
     * 数据库实例（单例模式）
     */
    private static volatile EventDatabase instance;

    /**
     * 获取活动数据访问对象
     *
     * @return EventDao实例
     */
    public abstract EventDao eventDao();

    /**
     * 获取点击记录数据访问对象
     *
     * @return ClickRecordDao实例
     */
    public abstract ClickRecordDao clickRecordDao();

    /**
     * 获取数据库单例实例
     * <p>
     * 使用双重检查锁定（DCL）确保线程安全的单例模式。
     * 数据库名称为 "campus_events_room.db"。
     * </p>
     *
     * @param context 上下文
     * @return EventDatabase实例
     */
    public static synchronized EventDatabase getInstance(Context context) {
        // 双重检查锁定
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    EventDatabase.class, "campus_events_room.db")
                    // 版本升级时如果没有迁移方案，直接销毁重建数据库
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
