package com.example.campuseventmanager.storage;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 活动数据仓库类
 * <p>
 * 作为数据访问的中间层，负责管理数据库操作和线程调度。
 * 所有数据库操作都在后台线程执行，避免阻塞主线程。
 * </p>
 */
public class EventRepository {

    /**
     * 活动数据访问对象
     */
    private final EventDao eventDao;

    /**
     * 后台执行器服务（单线程）
     */
    private final ExecutorService executorService;

    /**
     * 活动列表的LiveData，用于数据观察
     */
    private LiveData<List<EventEntity>> eventsLiveData;

    /**
     * 构造函数
     * <p>
     * 初始化数据库连接和执行器服务，并加载默认数据（如果数据库为空）。
     * </p>
     *
     * @param context 上下文
     */
    public EventRepository(Context context) {
        EventDatabase db = EventDatabase.getInstance(context);
        eventDao = db.eventDao();
        executorService = Executors.newSingleThreadExecutor();
        eventsLiveData = eventDao.getAllEventsLive();
        initDefaultData();
    }

    /**
     * 初始化默认数据
     * <p>
     * 如果数据库为空，添加示例活动数据。
     * 此操作在后台线程执行。
     * </p>
     */
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

    /**
     * 获取活动列表的LiveData
     * <p>
     * 返回可观察的活动列表，当数据库数据变化时自动通知观察者。
     * </p>
     *
     * @return 活动列表的LiveData
     */
    public LiveData<List<EventEntity>> getEventsLiveData() {
        return eventsLiveData;
    }

    /**
     * 添加新活动
     * <p>
     * 在后台线程执行插入操作。
     * </p>
     *
     * @param event 活动实体
     */
    public void addEvent(EventEntity event) {
        executorService.execute(() -> {
            eventDao.insertEvent(event);
        });
    }

    /**
     * 删除活动
     * <p>
     * 在后台线程执行删除操作。
     * 先根据标题、时间、详情查找对应的活动，然后删除。
     * </p>
     *
     * @param event 活动实体
     */
    public void deleteEvent(EventEntity event) {
        executorService.execute(() -> {
            EventEntity entity = eventDao.findEvent(event.getTitle(), event.getTime(), event.getDetail());
            if (entity != null) {
                eventDao.deleteEvent(entity);
            }
        });
    }

    /**
     * 删除所有活动
     * <p>
     * 在后台线程执行清空操作。
     * </p>
     */
    public void deleteAllEvents() {
        executorService.execute(() -> {
            eventDao.deleteAllEvents();
        });
    }

    /**
     * 更新活动置顶状态
     * <p>
     * 在后台线程执行更新操作。
     * </p>
     *
     * @param event 活动实体
     * @param isTop 是否置顶
     */
    public void updateEventTopStatus(EventEntity event, boolean isTop) {
        executorService.execute(() -> {
            EventEntity entity = eventDao.findEvent(event.getTitle(), event.getTime(), event.getDetail());
            if (entity != null) {
                eventDao.updateEventTopStatus(entity.getId(), isTop);
            }
        });
    }

    /**
     * 更新活动时间
     * <p>
     * 在后台线程执行更新操作。
     * </p>
     *
     * @param event   活动实体
     * @param newTime 新的活动时间
     */
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
