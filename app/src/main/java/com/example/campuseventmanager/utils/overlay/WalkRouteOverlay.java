package com.example.campuseventmanager.utils.overlay;

import android.content.Context;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.*;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkPath;
import java.util.List;

/**
 * 步行路线覆盖物类
 * <p>
 * 继承自RouteOverlay，专门用于在地图上绘制步行路径。
 * 功能包括：
 * - 在地图上绘制步行路线（蓝色线条）
 * - 添加起点标记（绿色）和终点标记（红色）
 * </p>
 */
public class WalkRouteOverlay extends RouteOverlay {

    /**
     * 步行路径数据
     */
    private WalkPath walkPath;

    /**
     * 构造函数
     *
     * @param context  上下文
     * @param aMap     地图控制器
     * @param walkPath 步行路径数据
     * @param start    起点坐标
     * @param end      终点坐标
     */
    public WalkRouteOverlay(Context context, AMap aMap, WalkPath walkPath, LatLonPoint start, LatLonPoint end) {
        super(context, aMap);
        this.walkPath = walkPath;
        // 添加起点和终点标记
        addStartMarker(new LatLng(start.getLatitude(), start.getLongitude()));
        addEndMarker(new LatLng(end.getLatitude(), end.getLongitude()));
    }

    /**
     * 将步行路径添加到地图
     * <p>
     * 遍历路径上的所有坐标点，创建折线覆盖物并添加到地图上。
     * </p>
     */
    public void addToMap() {
        PolylineOptions polylineOptions = new PolylineOptions();
        List<LatLonPoint> points = walkPath.getPolyline();
        // 将所有路径点添加到折线选项中
        for (LatLonPoint point : points) {
            polylineOptions.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }
        // 设置折线样式：宽度12像素，蓝色，层级10
        polylineOptions.width(12).color(0xFF007AFF).zIndex(10);
        // 添加到地图
        aMap.addPolyline(polylineOptions);
    }

    /**
     * 添加起点标记
     * <p>
     * 使用绿色标记表示起点位置。
     * </p>
     *
     * @param latLng 起点坐标
     */
    private void addStartMarker(LatLng latLng) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("起点");
        aMap.addMarker(options);
    }

    /**
     * 添加终点标记
     * <p>
     * 使用红色标记表示终点位置。
     * </p>
     *
     * @param latLng 终点坐标
     */
    private void addEndMarker(LatLng latLng) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("终点");
        aMap.addMarker(options);
    }
}