package com.example.campuseventmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.campuseventmanager.storage.ClickStatisticsManager;
import com.example.campuseventmanager.storage.EventEntity;
import com.example.campuseventmanager.storage.EventRepository;
import android.content.Context;
import java.util.List;

/**
 * RecyclerView适配器
 * <p>
 * 负责将活动数据绑定到RecyclerView列表项，支持以下功能：
 * - 展示活动标题和时间
 * - 置顶活动显示特殊背景色
 * - 侧滑菜单操作（置顶/取消置顶、删除）
 * - 点击事件统计（包含去重逻辑）
 * </p>
 */
public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.EventViewHolder> {

    /**
     * 活动列表数据
     */
    private List<EventEntity> eventList;

    /**
     * 点击事件监听器
     */
    private final OnItemClickListener listener;

    /**
     * 数据仓库，用于数据操作
     */
    private EventRepository eventRepository;

    /**
     * 点击事件监听器接口
     */
    public interface OnItemClickListener {
        /**
         * 点击活动项时调用
         *
         * @param event 被点击的活动
         */
        void onItemClick(EventEntity event);

        /**
         * 数据发生变化时调用
         */
        void onDataChanged();
    }

    /**
     * 构造函数
     *
     * @param eventList       活动列表数据
     * @param eventRepository 数据仓库
     * @param listener        点击事件监听器
     */
    public EventRecyclerViewAdapter(List<EventEntity> eventList, EventRepository eventRepository,
            OnItemClickListener listener) {
        this.eventList = eventList;
        this.eventRepository = eventRepository;
        this.listener = listener;
    }

    /**
     * 创建ViewHolder
     * <p>
     * 负责加载列表项布局并创建ViewHolder实例。
     * </p>
     *
     * @param parent   父容器
     * @param viewType 视图类型
     * @return ViewHolder实例
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_event_swipe, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * 绑定数据到ViewHolder
     * <p>
     * 负责将活动数据绑定到列表项视图，并设置各种事件监听。
     * </p>
     *
     * @param holder   ViewHolder对象
     * @param position 当前项在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // 获取当前位置的活动
        EventEntity event = eventList.get(position);

        // 设置活动标题和时间
        holder.tvTitle.setText(event.getTitle());
        holder.tvTime.setText(event.getTime());

        // 设置背景颜色：置顶活动使用深色背景
        if (event.isTop()) {
            holder.contentView.setBackgroundColor(
                    holder.contentView.getResources().getColor(R.color.top_event_background));
        } else {
            holder.contentView.setBackgroundColor(
                    holder.contentView.getResources().getColor(R.color.list_item_background));
        }

        // 根据置顶状态设置按钮文本（"置顶"或"取消置顶"）
        holder.btnTop.setText(event.isTop() ? "取消置顶" : "置顶");

        // 列表项点击事件（只在侧滑菜单关闭时响应）
        holder.swipeLayout.setOnClickListener(v -> {
            if (!holder.swipeLayout.isMenuOpen()) {
                // 记录点击事件（包含去重逻辑，一分钟内同一活动多次点击只记录一次）
                String eventId = String.valueOf(event.getId());
                if (eventId != null && !eventId.isEmpty()) {
                    ClickStatisticsManager.getInstance(holder.itemView.getContext()).recordClick(eventId);
                }
                // 调用监听器
                listener.onItemClick(event);
            }
        });

        // 置顶按钮点击事件（支持切换）
        holder.btnTop.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                EventEntity currentEvent = eventList.get(pos);
                boolean isCurrentlyTop = currentEvent.isTop();

                // 切换置顶状态
                currentEvent.setTop(!isCurrentlyTop);
                eventRepository.updateEventTopStatus(currentEvent, !isCurrentlyTop);

                // 根据新状态调整列表位置
                if (isCurrentlyTop) {
                    // 取消置顶：移动到非置顶区域
                    eventList.remove(pos);
                    int insertPos = eventList.size();
                    for (int i = 0; i < eventList.size(); i++) {
                        if (!eventList.get(i).isTop()) {
                            insertPos = i;
                            break;
                        }
                    }
                    eventList.add(insertPos, currentEvent);
                } else {
                    // 置顶：移动到列表开头
                    eventList.remove(pos);
                    eventList.add(0, currentEvent);
                }

                // 刷新列表
                notifyDataSetChanged();
                listener.onDataChanged();
                holder.swipeLayout.closeMenu();
            }
        });

        // 删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                EventEntity deleteEvent = eventList.get(pos);
                eventRepository.deleteEvent(deleteEvent);
                eventList.remove(pos);
                notifyItemRemoved(pos);
                listener.onDataChanged();
            }
        });
    }

    /**
     * 获取列表项数量
     *
     * @return 活动列表大小
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder类
     * <p>
     * 持有列表项视图的引用，用于数据绑定和事件处理。
     * </p>
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle; // 活动标题
        TextView tvTime; // 活动时间
        Button btnTop; // 置顶按钮
        Button btnDelete; // 删除按钮
        SwipeMenuItemLayout swipeLayout; // 侧滑菜单布局
        View contentView; // 内容区域视图

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeLayout = (SwipeMenuItemLayout) itemView;
            contentView = itemView.findViewById(R.id.content_view);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            btnTop = itemView.findViewById(R.id.btn_top);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
