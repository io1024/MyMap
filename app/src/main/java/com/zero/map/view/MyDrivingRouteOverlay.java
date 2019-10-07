package com.zero.map.view;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.route.RoutePlanSearch;

/**
 * 自定义
 * 线路线中实现特别的业务需求
 */
public class MyDrivingRouteOverlay extends DrivingRouteOverlay {

    private RoutePlanSearch planSearch;
    /**
     * 构造函数
     *
     * @param baiduMap 该DrivingRouteOvelray引用的 BaiduMap
     */
    public MyDrivingRouteOverlay(BaiduMap baiduMap, RoutePlanSearch planSearch) {
        super(baiduMap);
        this.planSearch = planSearch;
    }


    @Override
    public BitmapDescriptor getStartMarker() {
        //重写方法：可以修改默认的起点图标
//        return BitmapDescriptorFactory.fromResource();
        return super.getStartMarker();
    }

    @Override
    public BitmapDescriptor getTerminalMarker() {
        //重写方法：可以修改默认的终点图标
        return super.getTerminalMarker();
    }
}
