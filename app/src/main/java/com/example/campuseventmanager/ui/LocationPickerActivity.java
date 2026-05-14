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

public class LocationPickerActivity extends AppCompatActivity
        implements AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener {

    private static final String TAG = "LocationPickerActivity";
    private static final int REQUEST_LOCATION_PERMISSION = 1002;

    private MapView mapView;
    private AMap aMap;
    private LatLng selectedLatLng;
    private String selectedAddress;
    private GeocodeSearch geocodeSearch;
    private Button btnConfirm;
    private TextView tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        btnConfirm = findViewById(R.id.btn_confirm);
        tvAddress = findViewById(R.id.tv_address);

        initMap();
        checkLocationPermission();

        btnConfirm.setOnClickListener(v -> {
            if (selectedLatLng != null && selectedAddress != null) {
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

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMapClickListener(this);
            aMap.getUiSettings().setZoomControlsEnabled(true);
        }
        try {
            geocodeSearch = new GeocodeSearch(this);
            geocodeSearch.setOnGeocodeSearchListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "地理编码服务初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_LOCATION_PERMISSION);
            return;
        }
        getCurrentLocation();
    }

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
                    double lat = aMapLocation.getLatitude();
                    double lng = aMapLocation.getLongitude();
                    selectedLatLng = new LatLng(lat, lng);
                    selectedAddress = aMapLocation.getAddress();
                    tvAddress.setText(selectedAddress);
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 16));
                    addMarker(selectedLatLng);
                } else {
                    // 默认位置
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

    private void addMarker(LatLng latLng) {
        aMap.clear();
        aMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("选中位置"));
    }

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

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "需要定位权限", Toast.LENGTH_SHORT).show();
                // 使用默认位置
                selectedLatLng = new LatLng(39.9042, 116.4074);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 16));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}