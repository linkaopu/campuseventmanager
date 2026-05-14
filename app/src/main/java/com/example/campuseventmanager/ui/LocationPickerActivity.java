package com.example.campuseventmanager.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.campuseventmanager.R;

/**
 * 位置选择Activity
 * <p>
 * 负责提供地图位置选择功能，支持：
 * - 定位当前位置并在地图上显示
 * - 点击地图选择位置
 * - 通过逆地理编码获取地址信息
 * - 确认选择并返回地址和经纬度
 * </p>
 */
public class LocationPickerActivity extends AppCompatActivity
        implements AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener {

    /**
     * 日志标签
     */
    private static final String TAG = "LocationPickerActivity";

    /**
     * 定位权限请求码
     */
    private static final int REQUEST_LOCATION_PERMISSION = 1002;

    /**
     * 地图视图
     */
    private MapView mapView;

    /**
     * 地图控制器
     */
    private AMap aMap;

    /**
     * 选中的位置坐标
     */
    private LatLng selectedLatLng;

    /**
     * 选中位置的地址
     */
    private String selectedAddress;

    /**
     * 地理编码搜索服务
     */
    private GeocodeSearch geocodeSearch;

    /**
     * 确认按钮
     */
    private Button btnConfirm;

    /**
     * 地址显示文本
     */
    private TextView tvAddress;

    /**
     * 创建活动时的初始化方法
     * <p>
     * 负责：
     * 1. 设置布局
     * 2. 初始化地图视图
     * 3. 检查定位权限
     * 4. 设置确认按钮点击事件
     * </p>
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        // 获取地图视图和控件
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        btnConfirm = findViewById(R.id.btn_confirm);
        tvAddress = findViewById(R.id.tv_address);

        // 初始化地图
        initMap();
        
        // 检查定位权限
        checkLocationPermission();

        // 确认按钮点击事件
        btnConfirm.setOnClickListener(v -> {
            if (selectedLatLng != null && selectedAddress != null) {
                // 返回选中的位置信息
                Intent result = new Intent();
                result.putExtra("address", selectedAddress);
                result.putExtra("latitude", selectedLatLng.latitude);
                result.putExtra("longitude", selectedLatLng.longitude);
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toast.makeText(this, "请选择一个位置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化地图
     * <p>
     * 设置地图点击监听器和地理编码服务。
     * </p>
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMapClickListener(this);
            aMap.getUiSettings().setZoomControlsEnabled(true);
        }
        
        // 初始化地理编码服务
        try {
            geocodeSearch = new GeocodeSearch(this);
            geocodeSearch.setOnGeocodeSearchListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "地理编码服务初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查定位权限
     * <p>
     * 如果没有权限则请求权限，有权限则获取当前位置。
     * </p>
     */
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }
        getCurrentLocation();
    }

    /**
     * 获取当前位置
     * <p>
     * 使用高德地图定位服务获取当前位置，并在地图上显示。
     * </p>
     */
    private void getCurrentLocation() {
        try {
            AMapLocationClient locationClient = new AMapLocationClient(this);
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setNeedAddress(true);
            option.setOnceLocation(true);

            locationClient.setLocationOption(option);
            locationClient.setLocationListener(aMapLocation -> {
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    // 定位成功
                    double lat = aMapLocation.getLatitude();
                    double lng = aMapLocation.getLongitude();
                    selectedLatLng = new LatLng(lat, lng);
                    selectedAddress = aMapLocation.getAddress();
                    tvAddress.setText(selectedAddress);
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 16));
                    addMarker(selectedLatLng);
                } else {
                    // 定位失败，使用默认位置（北京天安门）
                    selectedLatLng = new LatLng(39.9042, 116.4074);
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 16));
                    Toast.makeText(LocationPickerActivity.this, "定位失败，使用默认位置", Toast.LENGTH_SHORT).show();
                }
            });
            locationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "定位客户端初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 在地图上添加标记
     *
     * @param latLng 位置坐标
     */
    private void addMarker(LatLng latLng) {
        aMap.clear();
        aMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("选中位置"));
    }

    /**
     * 地图点击事件处理
     * <p>
     * 用户点击地图时，选中该位置并进行逆地理编码获取地址。
     * </p>
     *
     * @param latLng 点击的位置坐标
     */
    @Override
    public void onMapClick(LatLng latLng) {
        selectedLatLng = latLng;
        addMarker(latLng);
        // 逆地理编码获取地址
        RegeocodeQuery query = new RegeocodeQuery(
                new LatLonPoint(latLng.latitude, latLng.longitude),
                100,
                GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }

    /**
     * 逆地理编码结果回调
     * <p>
     * 获取地址信息并显示。
     * </p>
     *
     * @param result 逆地理编码结果
     * @param rCode  结果码（1000表示成功）
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000 && result != null) {
            selectedAddress = result.getRegeocodeAddress().getFormatAddress();
            tvAddress.setText(selectedAddress);
        } else {
            selectedAddress = "未知位置";
            tvAddress.setText(selectedAddress);
        }
    }

    /**
     * 地理编码结果回调（未使用）
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
    }

    /**
     * 权限请求结果处理
     * <p>
     * 如果权限被授予则获取当前位置，否则使用默认位置。
     * </p>
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "需要定位权限", Toast.LENGTH_SHORT).show();
                // 使用默认位置（北京天安门）
                selectedLatLng = new LatLng(39.9042, 116.4074);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 16));
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
     * 生命周期方法：暂停地图
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 生命周期方法：销毁地图
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
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
