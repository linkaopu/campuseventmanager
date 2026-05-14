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

/**
 * 活动列表Fragment
 * <p>
 * 负责展示校园活动列表，支持以下功能：
 * - 使用RecyclerView展示活动列表（替代传统ListView，提升性能）
 * - 支持侧滑菜单操作（置顶/取消置顶、删除）
 * - 点击活动项跳转到详情页面
 * - 数据变化自动更新UI（通过LiveData观察）
 * </p>
 */
public class EventListFragment extends Fragment {

    /**
     * 活动列表数据
     */
    private ArrayList<EventEntity> eventList = new ArrayList<>();

    /**
     * RecyclerView适配器
     */
    private EventRecyclerViewAdapter adapter;

    /**
     * 数据仓库，负责数据访问
     */
    private EventRepository eventRepository;

    /**
     * 创建视图时调用
     * <p>
     * 负责：
     * 1. 加载布局
     * 2. 初始化数据仓库
     * 3. 配置RecyclerView（布局管理器、适配器）
     * 4. 设置数据观察者，响应数据变化
     * </p>
     *
     * @param inflater           布局加载器
     * @param container          父容器
     * @param savedInstanceState 保存的实例状态
     * @return Fragment视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        // 初始化数据仓库
        eventRepository = new EventRepository(getContext());

        // 获取RecyclerView控件
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        // 设置布局管理器为线性布局（垂直列表）
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 创建适配器，设置点击事件回调
        adapter = new EventRecyclerViewAdapter(eventList, eventRepository,
                new EventRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(EventEntity event) {
                        // 点击活动项，跳转到详情页面
                        Intent intent = new Intent(requireContext(), EventDetailActivity.class);
                        intent.putExtra("EVENT_DATA", event);
                        startActivity(intent);
                    }

                    @Override
                    public void onDataChanged() {
                        // 数据变化通过LiveData自动观察，此处留空
                    }
                });
        // 将适配器设置给RecyclerView
        recyclerView.setAdapter(adapter);

        // 观察数据变化，自动更新UI
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

        return view;
    }
}
