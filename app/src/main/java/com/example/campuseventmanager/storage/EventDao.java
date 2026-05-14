package com.example.campuseventmanager.storage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * 活动数据访问接口（DAO）
 * <p>
 * 定义了对events表的所有数据库操作方法，使用Room的注解来生成SQL语句。
 * 支持活动的增删改查操作，包括置顶状态的更新。
 * </p>
 */
@Dao
public interface EventDao {

    /**
     * 查询所有活动
     * <p>
     * 按置顶状态降序（置顶在前）、时间升序排序
     * </p>
     *
     * @return 活动列表
     */
    @Query("SELECT * FROM events ORDER BY isTop DESC, time ASC")
    List<EventEntity> getAllEvents();

    /**
     * 查询所有活动（LiveData版本）
     * <p>
     * 返回LiveData，当数据库数据变化时自动通知观察者
     * 按置顶状态降序（置顶在前）、时间升序排序
     * </p>
     *
     * @return 活动列表的LiveData
     */
    @Query("SELECT * FROM events ORDER BY isTop DESC, time ASC")
    LiveData<List<EventEntity>> getAllEventsLive();

    /**
     * 插入新活动
     *
     * @param event 活动实体
     * @return 插入记录的ID
     */
    @Insert
    long insertEvent(EventEntity event);

    /**
     * 更新活动信息
     *
     * @param event 活动实体
     */
    @Update
    void updateEvent(EventEntity event);

    /**
     * 删除指定活动
     *
     * @param event 活动实体
     */
    @Delete
    void deleteEvent(EventEntity event);

    /**
     * 删除所有活动
     */
    @Query("DELETE FROM events")
    void deleteAllEvents();

    /**
     * 更新活动置顶状态
     *
     * @param id    活动ID
     * @param isTop 是否置顶
     */
    @Query("UPDATE events SET isTop = :isTop WHERE id = :id")
    void updateEventTopStatus(long id, boolean isTop);

    /**
     * 根据标题、时间、详情查找活动
     * <p>
     * 用于判断是否存在重复活动
     * </p>
     *
     * @param title  活动标题
     * @param time   活动时间
     * @param detail 活动详情
     * @return 匹配的活动（最多返回一个）
     */
    @Query("SELECT * FROM events WHERE title = :title AND time = :time AND detail = :detail LIMIT 1")
    EventEntity findEvent(String title, String time, String detail);
}
