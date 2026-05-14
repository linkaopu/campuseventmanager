package com.example.campuseventmanager.storage;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * 活动实体类
 * <p>
 * 表示校园活动的数据模型，映射到Room数据库的events表。
 * 实现Serializable接口以便在Activity间传递对象。
 * </p>
 */
@Entity(tableName = "events")
public class EventEntity implements Serializable {

    /**
     * 主键，自动生成
     */
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动时间，格式：YYYY-MM-DD HH:MM:SS
     */
    private String time;

    /**
     * 活动详情描述
     */
    private String detail;

    /**
     * 是否置顶标志
     * true: 置顶，显示在列表顶部
     * false: 不置顶，按普通顺序显示
     */
    private boolean isTop;

    /**
     * 活动地址
     */
    private String address;

    /**
     * 活动地点纬度
     */
    private double latitude;

    /**
     * 活动地点经度
     */
    private double longitude;

    /**
     * 默认构造函数（Room必需）
     */
    public EventEntity() {
    }

    /**
     * 构造函数（不含地址信息）
     *
     * @param title  活动标题
     * @param time   活动时间
     * @param detail 活动详情
     * @param isTop  是否置顶
     */
    @Ignore
    public EventEntity(String title, String time, String detail, boolean isTop) {
        this.title = title;
        this.time = time;
        this.detail = detail;
        this.isTop = isTop;
    }

    /**
     * 构造函数（包含地址信息）
     *
     * @param title   活动标题
     * @param time    活动时间
     * @param detail  活动详情
     * @param isTop   是否置顶
     * @param address 活动地址
     */
    @Ignore
    public EventEntity(String title, String time, String detail, boolean isTop, String address) {
        this.title = title;
        this.time = time;
        this.detail = detail;
        this.isTop = isTop;
        this.address = address;
    }

    /**
     * 获取活动ID
     *
     * @return 活动ID
     */
    public long getId() {
        return id;
    }

    /**
     * 设置活动ID
     *
     * @param id 活动ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 获取活动标题
     *
     * @return 活动标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置活动标题
     *
     * @param title 活动标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取活动时间
     *
     * @return 活动时间（格式：YYYY-MM-DD HH:MM:SS）
     */
    public String getTime() {
        return time;
    }

    /**
     * 设置活动时间
     *
     * @param time 活动时间（格式：YYYY-MM-DD HH:MM:SS）
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 获取活动详情
     *
     * @return 活动详情描述
     */
    public String getDetail() {
        return detail;
    }

    /**
     * 设置活动详情
     *
     * @param detail 活动详情描述
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * 判断是否置顶
     *
     * @return true: 已置顶，false: 未置顶
     */
    public boolean isTop() {
        return isTop;
    }

    /**
     * 设置置顶状态
     *
     * @param top true: 置顶，false: 取消置顶
     */
    public void setTop(boolean top) {
        isTop = top;
    }

    /**
     * 获取活动地址
     *
     * @return 活动地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置活动地址
     *
     * @param address 活动地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取活动地点纬度
     *
     * @return 纬度值
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * 设置活动地点纬度
     *
     * @param latitude 纬度值
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * 获取活动地点经度
     *
     * @return 经度值
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * 设置活动地点经度
     *
     * @param longitude 经度值
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
