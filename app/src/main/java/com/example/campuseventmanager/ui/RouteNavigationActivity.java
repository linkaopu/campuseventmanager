package com.example.campuseventmanager.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.campuseventmanager.R;
import com.example.campuseventmanager.storage.EventEntity;
import com.example.campuseventmanager.utils.overlay.WalkRouteOverlay;

import java.util.List;

/**
 * 路径导航Activity
 * <p>
 * 负责实现步行导航功能，支持：
 * - 获取当前位置
 * - 根据活动地址规划步行路径
 * - 在地图上显示路径
 * - 显示路径详情（距离、预计时间、导航步骤）
 * </p>
 */
public class RouteNavigationActivity extends AppCompatActivity implements RouteSearch.OnRouteSearchListener {

    /**
     * 日志标签
     */
    private static final String TAG = "RouteNavigationActivity";

    /**
     * 定位权限请求码
     */
    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    /**
     * 地图视图
     */
    private MapView mapView;

    /**
     * 地图控制器
     */
    private AMap aMap;

    /**
     * 当前活动数据
     */
    private EventEntity event;

    /**
     * 定位客户端
     */
    private AMapLocationClient locationClient;

    /**
     * 当前位置
     */
    private LatLonPoint currentLocation;

    /**
     * 目的地位置
     */
    private LatLonPoint destination;

    /**
     * 路径搜索服务
     */
    private RouteSearch routeSearch;

    /**
     * 步行路径结果
     */
    private WalkRouteResult walkRouteResult;

    /**
     * 距离显示文本
     */
    private TextView tvDistance;

    /**
     * 预计时间显示文本
     */
    private TextView tvDuration;

    /**
     * 地址显示文本
     */
    private TextView tvAddress;

    /**
     * 导航按钮
     */
    private Button btnNavigate;

    /**
     * 创建活动时的初始化方法
     * <p>
     * 负责：
     * 1. 获取活动数据
     * 2. 初始化地图视图
     * 3. 设置界面控件
     * 4. 请求定位权限
     * 5. 设置导航按钮点击事件
     * </p>
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_navigation);

        // 获取传递的活动数据
        Intent intent = getIntent();
        event = (EventEntity) intent.getSerializableExtra("EVENT_DATA");

        // 初始化视图控件
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        tvDistance = findViewById(R.id.tv_distance);
        tvDuration = findViewById(R.id.tv_duration);
        tvAddress = findViewById(R.id.tv_address);
        btnNavigate = findViewById(R.id.btn_navigate);

        // 设置活动地址显示
        if (event != null && event.getAddress() != null) {
            tvAddress.setText(event.getAddress());
        }

        // 初始化地图
        initMap();

        // 请求定位权限
        requestLocationPermission();

        // 设置导航按钮点击事件
        btnNavigate.setOnClickListener(v -> {
            if (walkRouteResult != null && walkRouteResult.getPaths() != null
                    && !walkRouteResult.getPaths().isEmpty()) {
                showRouteDetail();
            } else {
                Toast.makeText(this, "请先获取路径规划", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化地图
     * <p>
     * 设置定位蓝点样式和地图交互选项。
     * </p>
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            // 设置定位蓝点样式
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.strokeColor(getResources().getColor(R.color.blue));
            myLocationStyle.radiusFillColor(getResources().getColor(R.color.blue_transparent));
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);
        }
    }

    /**
     * 请求定位权限
     * <p>
     * 如果没有权限则请求，有权限则开始定位。
     * </p>
     */
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }
        startLocation();
    }

    /**
     * 开始定位
     * <p>
     * 使用高德地图定位服务获取当前位置，定位成功后发起步行路径规划。
     * </p>
     */
    private void startLocation() {
        // 初始化定位客户端
        try {
            locationClient = new AMapLocationClient(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "定位客户端初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setNeedAddress(true);
        option.setOnceLocation(true);

        locationClient.setLocationOption(option);
        locationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                // 定位成功，获取当前位置
                currentLocation = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                Log.d(TAG, "当前位置: " + aMapLocation.getLatitude() + ", " + aMapLocation.getLongitude());

                // 设置目的地
                if (event != null) {
                    if (event.getLatitude() != 0 && event.getLongitude() != 0) {
                        destination = new LatLonPoint(event.getLatitude(), event.getLongitude());
                    } else {
                        // 默认使用示例坐标（北京天安门）
                        destination = new LatLonPoint(39.9042, 116.4074);
                    }
                }

                // 移动相机到当前位置
                LatLng currentLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));

                // 发起步行路径规划
                searchWalkRoute();
            } else {
                Log.e(TAG, "定位失败: " + aMapLocation.getErrorInfo());
                Toast.makeText(RouteNavigationActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
            }
        });
        locationClient.startLocation();
    }

    /**
     * 搜索步行路径
     * <p>
     * 使用高德地图路径搜索服务规划从当前位置到目的地的步行路线。
     * </p>
     */
    private void searchWalkRoute() {
        if (currentLocation == null || destination == null) {
            Toast.makeText(this, "位置信息不足", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            routeSearch = new RouteSearch(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "路径搜索初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }
        routeSearch.setRouteSearchListener(this);

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(currentLocation, destination);
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
        routeSearch.calculateWalkRouteAsyn(query);
    }

    /**
     * 步行路径搜索结果回调
     * <p>
     * 处理路径规划结果，显示距离、预计时间，并在地图上绘制路径。
     * </p>
     *
     * @param result 步行路径结果
     * @param rCode  结果码（1000表示成功）
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
        if (rCode == 1000 && result != null && result.getPaths() != null && !result.getPaths().isEmpty()) {
            walkRouteResult = result;
            WalkPath walkPath = result.getPaths().get(0);

            // 显示路径信息
            float distance = walkPath.getDistance() / 1000f; // 转换为公里
            long duration = walkPath.getDuration() / 60; // 转换为分钟

            tvDistance.setText(String.format("距离: %.2f 公里", distance));
            tvDuration.setText(String.format("预计时间: %d 分钟", duration));

            // 清除之前的覆盖物
            aMap.clear();

            // 添加步行路线覆盖物
            WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                    this, aMap, walkPath, currentLocation, destination);
            walkRouteOverlay.addToMap();
            walkRouteOverlay.zoomToSpan();

            Toast.makeText(this, "路径规划成功", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "步行路径规划失败: " + rCode);
            Toast.makeText(this, "路径规划失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 驾车路径搜索结果回调（未使用）
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
    }

    /**
     * 公交路径搜索结果回调（未使用）
     */
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
    }

    /**
     * 骑行路径搜索结果回调（未使用）
     */
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
    }

    /**
     * 显示路径详情
     * <p>
     * 将步行导航步骤显示在对话框中。
     * </p>
     */
    private void showRouteDetail() {
        if (walkRouteResult == null || walkRouteResult.getPaths() == null || walkRouteResult.getPaths().isEmpty()) {
            return;
        }

        WalkPath walkPath = walkRouteResult.getPaths().get(0);
        List<com.amap.api.services.route.WalkStep> steps = walkPath.getSteps();

        StringBuilder detailBuilder = new StringBuilder();
        detailBuilder.append("步行导航路线:\n\n");

        for (int i = 0; i < steps.size(); i++) {
            com.amap.api.services.route.WalkStep step = steps.get(i);
            detailBuilder.append(String.format("%d. %s\n", i + 1, step.getInstruction()));
        }

        // 显示路径详情对话框
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("导航详情")
                .setMessage(detailBuilder.toString())
                .setPositiveButton("确定", null)
                .show();
    }

    /**
     * 权限请求结果处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocation();
            } else {
                Toast.makeText(this, "需要定位权限来获取当前位置", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 生命周期方法：恢复地图
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 生命周期方法：暂停地图和定位
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationClient != null) {
            locationClient.stopLocation();
        }
    }

    /**
     * 生命周期方法：销毁地图和定位客户端
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy();
        }
    }

    /**
     * 生命周期方法：保存地图状态
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
