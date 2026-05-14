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

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.EventViewHolder> {
    private List<EventEntity> eventList;
    private final OnItemClickListener listener;
    private EventRepository eventRepository;

    public interface OnItemClickListener {
        void onItemClick(EventEntity event);

        void onDataChanged();
    }

    public EventRecyclerViewAdapter(List<EventEntity> eventList, EventRepository eventRepository,
            OnItemClickListener listener) {
        this.eventList = eventList;
        this.eventRepository = eventRepository;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_event_swipe, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * 绑定数据到ViewHolder，用于在RecyclerView中显示事件项
     * 
     * @param holder   ViewHolder对象，用于引用视图组件
     * @param position 当前项在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // 从事件列表中获取指定位置的事件对象
        EventEntity event = eventList.get(position);
        // 设置事件标题
        holder.tvTitle.setText(event.getTitle());
        // 设置事件时间
        holder.tvTime.setText(event.getTime());

        // 设置背景颜色：置顶事件使用深色背景
        if (event.isTop()) {
            holder.contentView
                    .setBackgroundColor(holder.contentView.getResources().getColor(R.color.top_event_background));
        } else {
            holder.contentView
                    .setBackgroundColor(holder.contentView.getResources().getColor(R.color.list_item_background));
        }

        // 根据置顶状态设置按钮文本
        holder.btnTop.setText(event.isTop() ? "取消置顶" : "置顶");

        // 只在菜单关闭时才响应点击
        holder.swipeLayout.setOnClickListener(v -> {
            // 检查滑动菜单是否关闭
            if (!holder.swipeLayout.isMenuOpen()) {
                // 记录点击事件（包含去重逻辑）
                String eventId = String.valueOf(event.getId());
                // 确保事件ID有效
                if (eventId != null && !eventId.isEmpty()) {
                    // 使用点击统计管理器记录点击事件
                    ClickStatisticsManager.getInstance(holder.itemView.getContext()).recordClick(eventId);
                }
                // 调用点击监听器，传递被点击的事件
                listener.onItemClick(event);
            }
        });

        // 设置置顶按钮点击事件（支持切换）
        holder.btnTop.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                EventEntity currentEvent = eventList.get(pos);
                boolean isCurrentlyTop = currentEvent.isTop();
                
                // 切换置顶状态
                currentEvent.setTop(!isCurrentlyTop);
                eventRepository.updateEventTopStatus(currentEvent, !isCurrentlyTop);
                
                if (isCurrentlyTop) {
                    // 取消置顶：从列表中移除并添加到非置顶区域
                    eventList.remove(pos);
                    // 找到第一个非置顶项的位置，插入到那里
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
                
                notifyDataSetChanged();
                listener.onDataChanged();
                holder.swipeLayout.closeMenu();
            }
        });

        // 设置删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            // 获取当前项的位置
            int pos = holder.getAdapterPosition();
            // 检查位置是否有效
            if (pos != RecyclerView.NO_POSITION) {
                // 获取要删除的事件
                EventEntity deleteEvent = eventList.get(pos);
                // 通过事件仓库删除事件
                eventRepository.deleteEvent(deleteEvent);
                // 从列表中移除事件
                eventList.remove(pos);
                // 通知指定位置的项目已被移除
                notifyItemRemoved(pos);
                // 通知监听器数据已更改
                listener.onDataChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvTime;
        Button btnTop;
        Button btnDelete;
        SwipeMenuItemLayout swipeLayout;
        View contentView;

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