package com.example.campuseventmanager.utils.overlay;

import android.content.Context;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLngBounds;

public class RouteOverlay {
    protected Context context;
    protected AMap aMap;

    public RouteOverlay(Context context, AMap aMap) {
        this.context = context;
        this.aMap = aMap;
    }

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

    protected LatLngBounds getBounds() {
        return null;
    }
}