package com.example.campuseventmanager.storage;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { EventEntity.class, ClickRecord.class }, version = 3, exportSchema = false)
public abstract class EventDatabase extends RoomDatabase {
    private static volatile EventDatabase instance;

    public abstract EventDao eventDao();

    public abstract ClickRecordDao clickRecordDao();

    public static synchronized EventDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    EventDatabase.class, "campus_events_room.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}