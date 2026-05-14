package com.example.campuseventmanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.Observer;
import com.example.campuseventmanager.EventRecyclerViewAdapter;
import com.example.campuseventmanager.R;
import com.example.campuseventmanager.storage.EventEntity;
import com.example.campuseventmanager.storage.EventRepository;
import com.example.campuseventmanager.ui.EventDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {
    private ArrayList<EventEntity> eventList = new ArrayList<>();
    private EventRecyclerViewAdapter adapter;
    private EventRepository eventRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        // 初始化 EventRepository
        eventRepository = new EventRepository(getContext());

        // 通过ID查找并获取RecyclerView控件
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        // 设置布局管理器为线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 创建EventRecyclerViewAdapter适配器
        adapter = new EventRecyclerViewAdapter(eventList, eventRepository,
                new EventRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(EventEntity event) {
                        // 创建一个Intent，用于启动EventDetailActivity
                        Intent intent = new Intent(requireContext(), EventDetailActivity.class);
                        // 将事件数据作为额外信息添加到Intent中
                        intent.putExtra("EVENT_DATA", event);
                        // 启动EventDetailActivity，并传递事件数据
                        startActivity(intent);
                    }

                    @Override
                    public void onDataChanged() {
                        // 数据变化通过LiveData自动观察
                    }
                });
        // 将适配器设置给RecyclerView，以便显示数据
        recyclerView.setAdapter(adapter);

        // 观察数据变化
        eventRepository.getEventsLiveData().observe(getViewLifecycleOwner(), new Observer<List<EventEntity>>() {
            @Override
            public void onChanged(List<EventEntity> events) {
                eventList.clear();
                if (events != null) {
                    eventList.addAll(events);
                }
                adapter.notifyDataSetChanged();
            }
        });

        // 返回当前视图
        return view;
    }
}