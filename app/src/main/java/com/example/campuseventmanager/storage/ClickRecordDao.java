package com.example.campuseventmanager.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ClickRecordDao {
    @Query("SELECT * FROM click_records WHERE eventId = :eventId LIMIT 1")
    ClickRecord findByEventId(String eventId);

    @Insert
    long insertClickRecord(ClickRecord record);

    @Update
    void updateClickRecord(ClickRecord record);

    @Query("DELETE FROM click_records")
    void deleteAllClickRecords();
}