package com.example.campuseventmanager.storage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM events ORDER BY isTop DESC, time ASC")
    List<EventEntity> getAllEvents();

    @Query("SELECT * FROM events ORDER BY isTop DESC, time ASC")
    LiveData<List<EventEntity>> getAllEventsLive();

    @Insert
    long insertEvent(EventEntity event);

    @Update
    void updateEvent(EventEntity event);

    @Delete
    void deleteEvent(EventEntity event);

    @Query("DELETE FROM events")
    void deleteAllEvents();

    @Query("UPDATE events SET isTop = :isTop WHERE id = :id")
    void updateEventTopStatus(long id, boolean isTop);

    @Query("SELECT * FROM events WHERE title = :title AND time = :time AND detail = :detail LIMIT 1")
    EventEntity findEvent(String title, String time, String detail);
}