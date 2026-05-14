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

/**
 * 添加活动Fragment
 * <p>
 * 负责提供活动添加界面，支持以下功能：
 * - 输入活动标题、时间、类型
 * - 选择活动位置（通过地图选择）
 * - 表单验证（标题非空、时间格式正确、位置已选择）
 * - 提交活动到数据库
 * </p>
 */
public class AddEventFragment extends Fragment {

    /**
     * 日志标签
     */
    private static final String TAG = "AddEventFragment";

    /**
     * 位置选择器请求码
     */
    private static final int REQUEST_LOCATION_PICKER = 100;

    /**
     * 时间格式正则表达式: YYYY-MM-DD HH:MM:SS
     */
    private static final String TIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";

    /**
     * 地址显示文本控件
     */
    private TextView tvAddress;

    /**
     * 选中的纬度
     */
    private double selectedLatitude = 0;

    /**
     * 选中的经度
     */
    private double selectedLongitude = 0;

    /**
     * 创建视图时调用
     * <p>
     * 负责：
     * 1. 加载布局
     * 2. 获取界面控件引用
     * 3. 设置定位图标点击事件（打开地图选择位置）
     * 4. 设置提交按钮点击事件（表单验证、保存活动）
     * </p>
     *
     * @param inflater           布局加载器
     * @param container          父容器
     * @param savedInstanceState 保存的实例状态
     * @return Fragment视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        // 初始化数据仓库
        EventRepository eventRepository = new EventRepository(getContext());

        // 获取界面控件引用
        EditText etTitle = view.findViewById(R.id.et_title); // 活动标题输入框
        EditText etTime = view.findViewById(R.id.et_time); // 活动时间输入框
        Spinner spinnerType = view.findViewById(R.id.spinner_type); // 活动类型选择框
        this.tvAddress = view.findViewById(R.id.tv_address); // 活动地址显示
        ImageView ivLocation = view.findViewById(R.id.iv_location); // 定位图标
        Button btnSubmit = view.findViewById(R.id.btn_submit); // 提交按钮

        // 定位图标点击事件：打开地图选择位置
        ivLocation.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LocationPickerActivity.class);
            startActivityForResult(intent, REQUEST_LOCATION_PICKER);
        });

        // 提交按钮点击事件
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

            // 打印日志
            Log.d(TAG, "提交：" + title + " | " + type + " | " + time + " | " + address);

            // 创建活动实体并保存到数据库
            EventEntity event = new EventEntity(title, time, "活动类型：" + type, false);
            event.setAddress(address);
            event.setLatitude(selectedLatitude);
            event.setLongitude(selectedLongitude);
            eventRepository.addEvent(event);

            // 跳回活动列表Fragment
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
     * <p>
     * 验证规则：
     * 1. 活动标题不能为空
     * 2. 活动时间不能为空且格式必须为 YYYY-MM-DD HH:MM:SS
     * 3. 活动位置不能为空且不能是默认提示文字
     * </p>
     *
     * @param title   活动标题
     * @param time    活动时间
     * @param address 活动地点
     * @return 验证错误信息（如果验证通过返回 null）
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

    /**
     * 处理位置选择器返回结果
     * <p>
     * 获取用户在地图上选择的地址和经纬度信息。
     * </p>
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        返回的数据
     */
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
