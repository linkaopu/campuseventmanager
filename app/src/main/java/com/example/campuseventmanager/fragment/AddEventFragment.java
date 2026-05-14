package com.example.campuseventmanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.campuseventmanager.R;
import com.example.campuseventmanager.storage.EventEntity;
import com.example.campuseventmanager.storage.EventRepository;
import com.example.campuseventmanager.ui.LocationPickerActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.regex.Pattern;

public class AddEventFragment extends Fragment {
    private static final String TAG = "AddEventFragment";
    private static final int REQUEST_LOCATION_PICKER = 100;

    // 时间格式正则表达式: YYYY-MM-DD HH:MM:SS
    private static final String TIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";

    private TextView tvAddress;
    private double selectedLatitude = 0;
    private double selectedLongitude = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 使用布局填充器加载fragment_add_event布局文件，并将其视图添加到容器中
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        // 初始化 EventRepository
        EventRepository eventRepository = new EventRepository(getContext());

        // 通过ID查找并获取布局中的标题输入框
        EditText etTitle = view.findViewById(R.id.et_title);
        // 通过ID查找并获取布局中的时间输入框
        EditText etTime = view.findViewById(R.id.et_time);
        // 通过ID查找并获取布局中的活动类型下拉选择框
        Spinner spinnerType = view.findViewById(R.id.spinner_type);
        // 通过ID查找并获取布局中的地址显示文本
        this.tvAddress = view.findViewById(R.id.tv_address);
        // 通过ID查找并获取布局中的定位图标
        ImageView ivLocation = view.findViewById(R.id.iv_location);
        // 通过ID查找并获取布局中的提交按钮
        Button btnSubmit = view.findViewById(R.id.btn_submit);

        // 为定位图标设置点击监听器
        ivLocation.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LocationPickerActivity.class);
            startActivityForResult(intent, REQUEST_LOCATION_PICKER);
        });

        // 为提交按钮设置点击监听器
        btnSubmit.setOnClickListener(v -> {
            // 获取输入内容
            String title = etTitle.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String address = tvAddress.getText().toString().trim();

            // 表单验证
            String validationError = validateForm(title, time, address);
            if (validationError != null) {
                Toast.makeText(requireContext(), validationError, Toast.LENGTH_LONG).show();
                return;
            }

            // 使用Log标签打印提交的活动信息
            Log.d(TAG, "提交：" + title + " | " + type + " | " + time + " | " + address);

            // 把新活动加入数据库
            EventEntity event = new EventEntity(title, time, "活动类型：" + type, false);
            event.setAddress(address);
            event.setLatitude(selectedLatitude);
            event.setLongitude(selectedLongitude);
            eventRepository.addEvent(event);

            // 跳回活动列表
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EventListFragment())
                    .commit();

            // 更新底部导航栏选中状态
            if (isAdded()) {
                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_events);
                }
            }
        });

        return view;
    }

    /**
     * 表单验证方法
     * 
     * @param title   活动标题
     * @param time    活动时间
     * @param address 活动地点
     * @return 验证错误信息，如果验证通过返回 null
     */
    private String validateForm(String title, String time, String address) {
        StringBuilder errorMessage = new StringBuilder();

        // 验证活动标题
        if (title.isEmpty()) {
            errorMessage.append("活动标题不能为空\n");
        }

        // 验证活动时间格式
        if (time.isEmpty()) {
            errorMessage.append("活动时间不能为空\n");
        } else if (!Pattern.matches(TIME_PATTERN, time)) {
            errorMessage.append("活动时间格式不正确，请输入：YYYY-MM-DD HH:MM:SS\n");
        }

        // 验证活动位置
        if (address.isEmpty() || address.equals("我的当前位置")) {
            errorMessage.append("请选择活动位置\n");
        }

        if (errorMessage.length() > 0) {
            return errorMessage.toString().trim();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_PICKER && resultCode == getActivity().RESULT_OK && data != null) {
            String address = data.getStringExtra("address");
            selectedLatitude = data.getDoubleExtra("latitude", 0);
            selectedLongitude = data.getDoubleExtra("longitude", 0);
            if (address != null) {
                tvAddress.setText(address);
            }
        }
    }
}