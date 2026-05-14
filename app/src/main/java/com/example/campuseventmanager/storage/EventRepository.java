package com.example.campuseventmanager.storage;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {
    private final EventDao eventDao;
    private final ExecutorService executorService;
    private LiveData<List<EventEntity>> eventsLiveData;

    public EventRepository(Context context) {
        EventDatabase db = EventDatabase.getInstance(context);
        eventDao = db.eventDao();
        executorService = Executors.newSingleThreadExecutor();
        eventsLiveData = eventDao.getAllEventsLive();
        initDefaultData();
    }

    private void initDefaultData() {
        executorService.execute(() -> {
            List<EventEntity> entities = eventDao.getAllEvents();
            // 如果数据库为空，添加默认数据
            if (entities.isEmpty()) {
                EventEntity defaultEvent1 = new EventEntity();
                defaultEvent1.setTitle("校园歌手大赛");
                defaultEvent1.setTime("2026-04-20 19:00");
                defaultEvent1.setDetail("决赛之夜，巅峰对决");
                defaultEvent1.setTop(false);
                eventDao.insertEvent(defaultEvent1);

                EventEntity defaultEvent2 = new EventEntity();
                defaultEvent2.setTitle("学术讲座");
                defaultEvent2.setTime("2026-04-25 14:00");
                defaultEvent2.setDetail("人工智能前沿技术");
                defaultEvent2.setTop(false);
                eventDao.insertEvent(defaultEvent2);
            }
        });
    }

    public LiveData<List<EventEntity>> getEventsLiveData() {
        return eventsLiveData;
    }

    public void addEvent(EventEntity event) {
        executorService.execute(() -> {
            eventDao.insertEvent(event);
        });
    }

    public void deleteEvent(EventEntity event) {
        executorService.execute(() -> {
            EventEntity entity = eventDao.findEvent(event.getTitle(), event.getTime(), event.getDetail());
            if (entity != null) {
                eventDao.deleteEvent(entity);
            }
        });
    }

    public void deleteAllEvents() {
        executorService.execute(() -> {
            eventDao.deleteAllEvents();
        });
    }

    public void updateEventTopStatus(EventEntity event, boolean isTop) {
        executorService.execute(() -> {
            EventEntity entity = eventDao.findEvent(event.getTitle(), event.getTime(), event.getDetail());
            if (entity != null) {
                eventDao.updateEventTopStatus(entity.getId(), isTop);
            }
        });
    }

    public void updateEventTime(EventEntity event, String newTime) {
        executorService.execute(() -> {
            EventEntity entity = eventDao.findEvent(event.getTitle(), event.getTime(), event.getDetail());
            if (entity != null) {
                entity.setTime(newTime);
                eventDao.updateEvent(entity);
            }
        });
    }
}