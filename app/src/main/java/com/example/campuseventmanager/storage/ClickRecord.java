package com.example.campuseventmanager.storage;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "click_records")
public class ClickRecord {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String eventId;
    private long lastClickTime;
    private int clickCount;

    public ClickRecord() {
    }

    @Ignore
    public ClickRecord(String eventId, long lastClickTime, int clickCount) {
        this.eventId = eventId;
        this.lastClickTime = lastClickTime;
        this.clickCount = clickCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public long getLastClickTime() {
        return lastClickTime;
    }

    public void setLastClickTime(long lastClickTime) {
        this.lastClickTime = lastClickTime;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }
}