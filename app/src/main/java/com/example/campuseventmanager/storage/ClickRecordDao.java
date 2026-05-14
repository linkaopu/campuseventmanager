package com.example.campuseventmanager.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * 点击记录数据访问接口（DAO）
 * <p>
 * 定义了对click_records表的所有数据库操作方法，用于用户行为点击事件的统计与去重。
 * </p>
 */
@Dao
public interface ClickRecordDao {

    /**
     * 根据活动ID查找点击记录
     * <p>
     * 用于判断用户在一分钟内是否重复点击同一活动。
     * </p>
     *
     * @param eventId 活动ID
     * @return 点击记录（最多返回一个）
     */
    @Query("SELECT * FROM click_records WHERE eventId = :eventId LIMIT 1")
    ClickRecord findByEventId(String eventId);

    /**
     * 插入新的点击记录
     *
     * @param record 点击记录实体
     * @return 插入记录的ID
     */
    @Insert
    long insertClickRecord(ClickRecord record);

    /**
     * 更新点击记录
     * <p>
     * 用于更新最后点击时间和点击次数。
     * </p>
     *
     * @param record 点击记录实体
     */
    @Update
    void updateClickRecord(ClickRecord record);

    /**
     * 删除所有点击记录
     * <p>
     * 用于数据清理或重置统计。
     * </p>
     */
    @Query("DELETE FROM click_records")
    void deleteAllClickRecords();
}
