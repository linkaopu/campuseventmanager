package com.example.campuseventmanager.utils.overlay;

import android.content.Context;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLngBounds;

/**
 * 路线覆盖物基类
 * <p>
 * 提供地图路径覆盖物的基础功能，如缩放地图以显示完整路径。
 * 子类需实现具体路径（如步行、驾车）的绘制逻辑。
 * </p>
 */
public class RouteOverlay {
    /**
     * 上下文
     */
    protected Context context;

    /**
     * 地图控制器
     */
    protected AMap aMap;

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param aMap    地图控制器
     */
    public RouteOverlay(Context context, AMap aMap) {
        this.context = context;
        this.aMap = aMap;
    }

    /**
     * 缩放地图以显示完整路径
     * <p>
     * 通过获取路径的边界范围，调整地图相机位置，确保路径完全可见。
     * </p>
     */
    public void zoomToSpan() {
        try {
            LatLngBounds bounds = getBounds();
            if (bounds != null) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取路径边界范围（子类实现）
     *
     * @return 路径的LatLngBounds对象
     */
    protected LatLngBounds getBounds() {
        return null;
    }
}