package com.example.campuseventmanager.storage;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "events")
public class EventEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String time;
    private String detail;
    private boolean isTop;
    private String address;
    private double latitude;
    private double longitude;

    public EventEntity() {
    }

    @Ignore
    public EventEntity(String title, String time, String detail, boolean isTop) {
        this.title = title;
        this.time = time;
        this.detail = detail;
        this.isTop = isTop;
    }

    @Ignore
    public EventEntity(String title, String time, String detail, boolean isTop, String address) {
        this.title = title;
        this.time = time;
        this.detail = detail;
        this.isTop = isTop;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}