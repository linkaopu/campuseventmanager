package com.example.campuseventmanager.utils.overlay;

import android.content.Context;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.*;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkPath;
import java.util.List;

public class WalkRouteOverlay extends RouteOverlay {

    private WalkPath walkPath;

    public WalkRouteOverlay(Context context, AMap aMap, WalkPath walkPath, LatLonPoint start, LatLonPoint end) {
        super(context, aMap);
        this.walkPath = walkPath;
        addStartMarker(new LatLng(start.getLatitude(), start.getLongitude()));
        addEndMarker(new LatLng(end.getLatitude(), end.getLongitude()));
    }

    public void addToMap() {
        PolylineOptions polylineOptions = new PolylineOptions();
        List<LatLonPoint> points = walkPath.getPolyline();
        for (LatLonPoint point : points) {
            polylineOptions.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }
        polylineOptions.width(12).color(0xFF007AFF).zIndex(10);
        aMap.addPolyline(polylineOptions);
    }

    private void addStartMarker(LatLng latLng) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("起点");
        aMap.addMarker(options);
    }

    private void addEndMarker(LatLng latLng) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("终点");
        aMap.addMarker(options);
    }
}