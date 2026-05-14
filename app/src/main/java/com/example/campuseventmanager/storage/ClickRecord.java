package com.example.campuseventmanager.storage;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 点击记录实体类
 * <p>
 * 用于记录用户对活动的点击行为，实现点击去重功能（一分钟内同一活动多次点击只记录一次）。
 * 映射到Room数据库的click_records表。
 * </p>
 */
@Entity(tableName = "click_records")
public class ClickRecord {

    /**
     * 主键，自动生成
     */
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**
     * 活动ID（关联events表）
     */
    private String eventId;

    /**
     * 最后一次点击时间（毫秒时间戳）
     */
    private long lastClickTime;

    /**
     * 点击次数
     */
    private int clickCount;

    /**
     * 默认构造函数（Room必需）
     */
    public ClickRecord() {
    }

    /**
     * 构造函数
     *
     * @param eventId       活动ID
     * @param lastClickTime 最后点击时间
     * @param clickCount    点击次数
     */
    @Ignore
    public ClickRecord(String eventId, long lastClickTime, int clickCount) {
        this.eventId = eventId;
        this.lastClickTime = lastClickTime;
        this.clickCount = clickCount;
    }

    /**
     * 获取记录ID
     *
     * @return 记录ID
     */
    public long getId() {
        return id;
    }

    /**
     * 设置记录ID
     *
     * @param id 记录ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 获取活动ID
     *
     * @return 活动ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * 设置活动ID
     *
     * @param eventId 活动ID
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * 获取最后点击时间
     *
     * @return 最后点击时间（毫秒时间戳）
     */
    public long getLastClickTime() {
        return lastClickTime;
    }

    /**
     * 设置最后点击时间
     *
     * @param lastClickTime 最后点击时间（毫秒时间戳）
     */
    public void setLastClickTime(long lastClickTime) {
        this.lastClickTime = lastClickTime;
    }

    /**
     * 获取点击次数
     *
     * @return 点击次数
     */
    public int getClickCount() {
        return clickCount;
    }

    /**
     * 设置点击次数
     *
     * @param clickCount 点击次数
     */
    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }
}
