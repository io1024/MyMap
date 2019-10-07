package com.zero.map;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.BusLineOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.zero.map.view.MyDrivingRouteOverlay;
import com.zero.map.view.MyPoiOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 * 百度地图的说明：
 * .    1. 百度地图缩放级别：4~21(2D)、19~21(3D)、4~20(卫星图)、11~21(路况交通图、城市热力图)、17~22(室内图)
 * .    2. 百度地图缩放级别越大，地图越详细。百度的默认缩放级别是：12
 * .    3. 百度地图默认中心点是：天安门
 * .    4. 百度地图使用的位置计算是：经纬度坐标（LatLng-注意：纬度在前，经度在后）
 * .
 * <>
 * 百度常用类：
 * .    1. MapStatusUpdateFactory
 * .    2. MapStatusUpdate
 * .    3. LatLng（LatLng(double latitude,double longitude):latitude-纬度,longitude-经度）
 * .    4. OverlayOptions（本地覆盖物的抽象基类）
 * .    5. BitmapDescriptorFactory
 * .    6. OverlayManager
 * .    7. PoiSearch + OnGetPoiSearchResultListener;
 * .    8. RoutePlanSearch + OnGetRoutePlanResultListener;
 * <>
 * 百度搜索
 * .    本地搜索覆盖物、驾车路线覆盖物：实现思路
 * .        1. 初始化 PoiSearch, 通过 setOnGetPoiSearchResultListener 方法注册搜索结果的监听,实现异步搜索服务
 * .        2. 通过自定义 MySearchListener 实现类, 处理不同的回调方法, 获取搜索结果
 * .        注意：：OnGetPoiSearchResultListener 只支持一个，以最后一次设置为准。
 * .
 * <>
 * .
 */
public class MainActivity extends BaseActivity {

    private MapView mapView;
    private BaiduMap baiduMap;
    //朝阳大悦城经纬度：39.9307908547(纬度),116.5254348902(经度)
    private final double latitude = 39.9307908547; //纬度
    private final double longitude = 116.5254348902; //经度
    private LatLng myHome, myCompany;
    private View mapPopView;
    private PoiSearch poiSearch;
    private RoutePlanSearch planSearch;
    private LocationClient locationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myHome = new LatLng(latitude, longitude);
        myCompany = new LatLng(39.9966346727, 116.4300054146);
        initView();
        initPop();
        initData();
    }


    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView.onResume()，实现地图生命周期管理
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //停止定位
        locationClient.stop();
        //关闭地图的定位图层
        baiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        mapPopView = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView() {
        mapView = findViewById(R.id.mapView);
        //获取 BaiduMap（管理具体的某一个MapView,可以对其操作：旋转、缩放、移动）
        baiduMap = mapView.getMap();
        //设置缩放级别
        MapStatusUpdate zoomTo = MapStatusUpdateFactory.zoomTo(15);
        baiduMap.setMapStatus(zoomTo);
        //设置指定的中心点
        //LatLng latLng = new LatLng(latitude, longitude);//指定一个位置
        MapStatusUpdate newLatLng = MapStatusUpdateFactory.newLatLng(myHome);
        baiduMap.setMapStatus(newLatLng);
        //设置比例尺是否显示。默认是true(显示)
        mapView.showScaleControl(false);
        //设置缩放按钮是否显示。默认是true(显示)
        mapView.showZoomControls(false);

        //设置显示实时交通信息图或卫星图(能同时显示)
        //baiduMap.setTrafficEnabled(true);
        //baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
    }

    @Override
    public void initData() {
//        setCircleOptions();
//        setTextOptions();
//        setMarkerOptions();
//        searchInBoundPoiOverlay();
//        searchNearbyPoiOverlay();
//        searchInCityPoiOverlay();
        routePlanSearchCar();
        routePlanSearchBus();
        routePlanSearchWalk();
        searchGeoCoder();
        //开启定位
        startLocate();
    }

    //开启定位
    private void startLocate() {
        //百度地图定位：开启地图的定位图层(百度地图定位必须开启)
        baiduMap.setMyLocationEnabled(true);
        //定位初始化
        locationClient = new LocationClient(this);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption lco = new LocationClientOption();
        //设置定位模式：默认高精度。高精度(网络定位+GPS定位)、低功耗(不使用GPS,仅用网络定位)、仅设备(只用GPS,不支持室内环境的定位)
        lco.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        lco.setOpenGps(true); //设置是否打开GPS。默认关闭=false
        lco.setCoorType("bd09ll"); //设置坐标类型（百度类型：bd09ll, 默认：火星类型：gcj02）
        lco.setScanSpan(1000);//设置发起定位请求的间隔需要大于等于1000ms才是有效的。默认0表示定位一次，
        lco.setIsNeedAddress(true);//设置是否需要地址信息。默认不需要
        lco.setLocationNotify(true);//设置是否当GPS有效时按照1秒1次频率输出GPS结果。默认false
        //设置是否需要位置语文化结果，可以在 BDLocation.getLocationDescribe 里获得。类似"在北京天安门附近"。默认false
        lco.setIsNeedLocationDescribe(true);
        //设置是否需要POI结果，可以在 BDLocation.getPoiList 里得到。默认false
        lco.setIsNeedLocationPoiList(true);
        //设置是否在stop的时候杀死定位这个进程（定位SDK内部是一个 service,并放到了独立进程）。默认杀死(false)
        lco.setIgnoreKillProcess(false);
        //设置是否收集 crash 信息。默认收集(false)
        lco.SetIgnoreCacheException(false);
        //设置是否需要过滤 GPS 仿真结果。默认需要(false)
        lco.setEnableSimulateGps(false);
        //设置locationClientOption
        locationClient.setLocOption(lco);
        //注册LocationListener监听器
//        locationClient.registerLocationListener(new BDLocationListener() {
//            @Override
//            public void onReceiveLocation(BDLocation bdLocation) {
//
//            }
//        });
        //我们通过继承抽象类BDAbstractListener并重写其onReceiveLocation方法来获取定位数据，并将其传给MapView。
        locationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                //mapView 销毁后不在处理新接收的位置
                if (location == null || mapView == null) {
                    return;
                }
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        //此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(location.getDirection())
                        .longitude(location.getLongitude())  //设置经度
                        .latitude(location.getLatitude())  //设置纬度
                        .build();
                baiduMap.setMyLocationData(locData);
            }
        });
        //开启地图定位图层
        locationClient.start();
        //设置定位参数
        BitmapDescriptor myIcon = BitmapDescriptorFactory.fromAsset("Icon_walk_route"); //自定义一个图像
        /**
         * //参数1：定位图层显示方式(FOLLOWING-定位跟随态、FOLLOWING-定位罗盘态、NORMAL-普通态,默认状态)
         * MyLocationConfiguration.LocationMode
         * boolean   //参数2：是否允许显示方向信息
         * BitmapDescriptor   //参数3：用户自定义定位图标
         * int accuracyCircleFillColor,  //参数4：自定义精度圈填充颜色
         * int accuracyCircleStrokeColor  //参数5：自定义精度圈边框颜色
         */
        MyLocationConfiguration configuration = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.COMPASS,
                true, myIcon, 0xAAFFFF88, 0xAA00FF00
        );
        //设置定位的显示模式
        baiduMap.setMyLocationConfiguration(configuration);
    }

    private void searchGeoCoder() {
        //地址解析服务
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                //回调：根据地址名称返回地址信息
                LatLng latLng = result.getLocation();
                String str = "经度：" + latLng.longitude + "纬度：" + latLng.latitude;
                //Toast.makeText(MyApplication.getAppContext(), str, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                //回调返回：指定经纬度的地址信息
                String address = result.getAddress();
                //Toast.makeText(MyApplication.getAppContext(), address, Toast.LENGTH_SHORT).show();
            }
        });
        //根据地址名称返回地址信息
        GeoCodeOption gco = new GeoCodeOption();
        gco.city("北京");//必须指定城市
        gco.address("朝阳门");
        geoCoder.geocode(gco);
        //根据指定经纬度的地址信息
        ReverseGeoCodeOption rgco = new ReverseGeoCodeOption();
        rgco.location(myCompany);
        geoCoder.reverseGeoCode(rgco);
    }

    //步行路线
    private void routePlanSearchWalk() {
        planSearch = RoutePlanSearch.newInstance();
        planSearch.setOnGetRoutePlanResultListener(planResultListener);

        WalkingRoutePlanOption wrpo = new WalkingRoutePlanOption();
        PlanNode planNodeFrom = PlanNode.withLocation(myHome);
        wrpo.from(planNodeFrom);
        PlanNode planNodeTo = PlanNode.withLocation(myCompany);
        wrpo.to(planNodeTo);
        //发起步行路线请求
        planSearch.walkingSearch(wrpo);
    }

    //公交换乘
    private void routePlanSearchBus() {
        planSearch = RoutePlanSearch.newInstance();
        planSearch.setOnGetRoutePlanResultListener(planResultListener);
        TransitRoutePlanOption trpo = new TransitRoutePlanOption();
        //设置策略：不含地铁、时间优先、最少换乘、最少步行距离
        trpo.policy(TransitRoutePlanOption.TransitPolicy.EBUS_TIME_FIRST);
        //设置城市
        trpo.city("北京");
        //设置起点、终点
        //PlanNode.withCityCodeAndPlaceName();
        //PlanNode.withCityNameAndPlaceName();
        PlanNode planNodeFrom = PlanNode.withLocation(myHome);
        trpo.from(planNodeFrom);
        PlanNode planNodeTo = PlanNode.withLocation(myCompany);
        trpo.to(planNodeTo);
        //发起公交换乘请求
        planSearch.transitSearch(trpo);
    }

    //驾车路线
    private void routePlanSearchCar() {
        planSearch = RoutePlanSearch.newInstance();
        planSearch.setOnGetRoutePlanResultListener(planResultListener);
        DrivingRoutePlanOption drpo = new DrivingRoutePlanOption();
        //设置策略（驾车策略：躲避拥堵、最短距离、较少费用、时间优先）TODO 设置错误会导致无法获取路线
        drpo.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST);
        //设置起点、终点
        //PlanNode.withCityCodeAndPlaceName();
        //PlanNode.withCityNameAndPlaceName();
        PlanNode planNodeFrom = PlanNode.withLocation(myHome);
        drpo.from(planNodeFrom);
        PlanNode planNodeTo = PlanNode.withLocation(myCompany);
        drpo.to(planNodeTo);
        //添加：途经点（可添加多个）
        List<PlanNode> nodes = new ArrayList<>();
        PlanNode node = PlanNode.withCityNameAndPlaceName("北京", "朝阳门");
        nodes.add(node);
        drpo.passBy(nodes);
        //发起驾车路线的请求
        planSearch.drivingSearch(drpo);
    }

    //驾车路线覆盖物+步行路线覆盖物+公交路线覆盖物+换成路线覆盖物 监听
    private OnGetRoutePlanResultListener planResultListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult result) {
            //步行路线结果回调
            if (result == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == result.error) {
                Toast.makeText(MyApplication.getAppContext(), "未查询到驾车路线", Toast.LENGTH_SHORT).show();
                return;
            }
            //1.创建覆盖物
            WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
            //实现路线转折点可以点击效果
            baiduMap.setOnMarkerClickListener(overlay);
            //2. 给覆盖物设置数据
            List<WalkingRouteLine> routeLines = result.getRouteLines();//获取所有的路线
            overlay.setData(routeLines.get(0)); //模拟：设置一条路线
            //3. 把覆盖物添加到地图中
            overlay.addToMap();
            //缩放地图，使所有Overlay都在合适的视野内（让搜索到的结果都展示出来）
            overlay.zoomToSpan();
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult result) {
            //换乘路线结果回调
            if (result == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == result.error) {
                Toast.makeText(MyApplication.getAppContext(), "未查询到驾车路线", Toast.LENGTH_SHORT).show();
                return;
            }
            //1.创建覆盖物
            TransitRouteOverlay overlay = new TransitRouteOverlay(baiduMap);
            //实现路线转折点可以点击效果
            //baiduMap.setOnMarkerClickListener(overlay);
            //好像是在设置走直线
            //baiduMap.setOnPolylineClickListener(overlay);
            //2. 给覆盖物设置数据
            List<TransitRouteLine> routeLines = result.getRouteLines();//获取所有的路线
            overlay.setData(routeLines.get(0)); //模拟：设置一条路线
            //3. 把覆盖物添加到地图中
            overlay.addToMap();
            //缩放地图，使所有Overlay都在合适的视野内（让搜索到的结果都展示出来）
            overlay.zoomToSpan();
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            //驾车路线结果回调
            if (result == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == result.error) {
                Toast.makeText(MyApplication.getAppContext(), "未查询到驾车路线", Toast.LENGTH_SHORT).show();
                return;
            }
            //1. 创建覆盖物
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
            //DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);
            //实现路线转折点可以点击效果
            baiduMap.setOnMarkerClickListener(overlay);
            //2. 给覆盖物设置数据
            List<DrivingRouteLine> routeLines = result.getRouteLines();//获取所有的路线
            overlay.setData(routeLines.get(0)); //模拟：设置一条路线
            //3. 把覆盖物添加到地图中
            overlay.addToMap();
            //缩放地图，使所有Overlay都在合适的视野内（让搜索到的结果都展示出来）
            overlay.zoomToSpan();
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };


    //本地搜索覆盖物：城市poi检索 并在城市内：poi详细信息检索
    private void searchInCityPoiOverlay() {
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(poiSearchResultListener);
        PoiCitySearchOption pcso = new PoiCitySearchOption();
        pcso.city("北京"); //设置城市
        pcso.keyword("加油站");
        //pcso.pageNum(0);
        poiSearch.searchInCity(pcso);
    }

    //本地搜索覆盖物：周边检索
    private void searchNearbyPoiOverlay() {
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(poiSearchResultListener);
        PoiNearbySearchOption pnso = new PoiNearbySearchOption();
        pnso.location(myHome); //设置搜索中心点
        pnso.radius(3000); //设置搜索半径，单位是米
        pnso.keyword("酒店"); //设置搜索：关键字
        poiSearch.searchNearby(pnso);
    }

    //本地搜索覆盖物：范围内搜索
    private void searchInBoundPoiOverlay() {
        //获取发起poi搜索请求的对象
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(poiSearchResultListener);
        //范围内搜索参数设置(设置的是一个矩形范围搜索,设置其左下+右上)
        PoiBoundSearchOption pbso = new PoiBoundSearchOption();
        LatLngBounds llb = new LatLngBounds.Builder()
                .include(new LatLng(39.90089524515786, 116.47500206527452))  //纬度:39.90089524515786, 经度:116.47500206527452
                .include(new LatLng(39.96554361999564, 116.56840787220203))  //纬度:39.96554361999564, 经度:116.56840787220203
                .build();
        pbso.bound(llb); //设置搜索范围
        pbso.keyword("大厦"); //设置搜索关键字
        pbso.pageNum(1);//控制显示第几页的数据，默认只显示当前页，默认从0开始
        poiSearch.searchInBound(pbso);//开启检索
    }

    //本地搜索覆盖物 监听
    private OnGetPoiSearchResultListener poiSearchResultListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult result) {
            //回调：搜索到的所有具体点的信息
            searchOnGetPoiResult(result);
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult result) {
            //回调：搜索具体的某一个点的详情（设置：poi详细信息检索的回调）
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult result) {
            //回调：搜索具体的某一个点的详情（设置：poi详细信息检索的回调）
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult result) {
        }
    };


    private void searchOnGetPoiResult(PoiResult result) {
        if (result == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == result.error) {
            Toast.makeText(MyApplication.getAppContext(), "未查询到信息", Toast.LENGTH_SHORT).show();
            return;
        }
        int currentPageCapacity = result.getCurrentPageCapacity(); //当前页有多少条
        int currentPageNum = result.getCurrentPageNum(); //当前页
        int totalPageNum = result.getTotalPageNum(); //总页数
        int totalPoiNum = result.getTotalPoiNum(); //总条数
        String str = "共" + totalPageNum + "页, 共" + totalPoiNum + "条,当前第" + currentPageNum + "页，本页共" + currentPageCapacity + "条";
        Toast.makeText(MyApplication.getAppContext(), str, Toast.LENGTH_SHORT).show();

        //清除上一页数据，只显示当前页
        baiduMap.clear();

        //搜索到结果后：1.创建覆盖物。2.设置数据。3.添加到地图上
        //TODO 自定义 PoiOverlay 实现覆盖物点击效果（重写方法onPoiClick）
//        PoiOverlay pol = new PoiOverlay(baiduMap); //创建覆盖物，点击无反馈
        PoiOverlay pol = new MyPoiOverlay(baiduMap, poiSearch); //创建覆盖物,自定义类，点击有反馈
        pol.setData(result); //设置数据(只显示当前页的数据)
        pol.addToMap(); //添加到地图上
        //缩放地图，使所有Overlay都在合适的视野内（让搜索到的结果都展示出来）
        pol.zoomToSpan();
        //自定义一个类继承PoiOverlay，重写其方法：onPoiClick（可以填充处理自己的业务逻辑）
        baiduMap.setOnMarkerClickListener(pol);
    }


    //CircleOptions 圆形覆盖物
    private void setCircleOptions() {
        //绘制圆
        //1. 创建覆盖物对象
        CircleOptions co = new CircleOptions();
        //2. 设置数据
        //co.center(new LatLng(latitude, longitude))  //设置圆心
        co.center(myHome)  //设置圆心
                .radius(1000)  //设置圆半径，单 位是米
                .fillColor(0x60ff0000)  //设置圆填充颜色(用16进制表示.也可以设置color引用)
                .stroke(new Stroke(5, 0x600000ff));   //设置圆边框信息=Stroke(int width,int color):边框宽度-单位像素,边框颜色
        //3. 添加到地图中
        //Overlay addOverlay(OverlayOptions var1)
        baiduMap.addOverlay(co);
    }

    //TextOptions 文字覆盖物
    private void setTextOptions() {
        TextOptions to = new TextOptions();
        to.text("朝阳大悦城") //设置文字
                .fontColor(0x600000ff)  //设置文字颜色
                .position(myHome)  //设置文字显示位置
                .typeface(Typeface.DEFAULT_BOLD)  //设置文字字体
                .fontSize(28); //设置文字大小
        baiduMap.addOverlay(to);
    }

    //MarkerOptions 点击位置显示点击Pop
    private void initPop() {
        //预加载Pop(默认设置为隐藏)
        mapPopView = View.inflate(MyApplication.getAppContext(), R.layout.view_map_pop, null);
        MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder();
        builder.layoutMode(MapViewLayoutParams.ELayoutMode.mapMode); //设置为经纬度模式()
        builder.position(myHome); //必须初始一个经纬度位置，后续再根据点击变换位置
        //builder.yOffset();//设置Y轴偏移量
        builder.width(MapViewLayoutParams.WRAP_CONTENT).height(MapViewLayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams params = builder.build();
        mapView.addView(mapPopView, params);
        mapPopView.setVisibility(View.INVISIBLE);
    }

    //MarkerOptions marker覆盖物
    private void setMarkerOptions() {
//        Bundle bundle = new Bundle();
//        bundle.putString("", "新的大悦城");
        //通过 BitmapDescriptorFactory 获取一个图片
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_start);
        MarkerOptions mo = new MarkerOptions();
        //是否允许拖拽到其他位置上
        //mo.draggable(false);
        //标志一个图标
        //mo.icon();
        //标志多个图片（多个图片做帧动画切换）
        //mo.icons();
        //mo.period(1000);//设置多个图标后，图标切换的间隔时间（相当于帧动画里面的时间）
        mo.position(myHome)  //设置位置
                .icon(bitmap) //设置图标
                .title("新的大悦城"); //设置标题
        // .extraInfo(bundle); //设置标题
        baiduMap.addOverlay(mo);

        //点击自己设定的覆盖物的监听回调方法
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //marker：当前点击位置的 mark 信息
                //获取当前点击的位置信息（经纬度）
                LatLng position = marker.getPosition();
                //更新 Pop 位置
                MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder();
                builder.layoutMode(MapViewLayoutParams.ELayoutMode.mapMode);
                builder.position(position);
                builder.width(MapViewLayoutParams.WRAP_CONTENT).height(MapViewLayoutParams.WRAP_CONTENT);
                ViewGroup.LayoutParams params = builder.build();
                mapView.updateViewLayout(mapPopView, params);
                mapPopView.setVisibility(View.VISIBLE);
                return true;//返回true,表示自己处理了点击事件（消费事件）
                //return false;
            }
        });
        //点击地图的监听（除了自己设定的覆盖物除外）
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override //点击空旷位置回调该方法
            public void onMapClick(LatLng latLng) {
                Log.e("MapClickListener", "纬度:" + latLng.latitude + ", 经度:" + latLng.longitude);
            }

            @Override //点击有名字的位置，回调该方法
            public void onMapPoiClick(MapPoi mapPoi) {
                //MapPoi:点击地图 Poi 点时，该兴趣点的描述信息
                String name = mapPoi.getName();//获取该兴趣点的名称
                LatLng latLng = mapPoi.getPosition();//获取该兴趣点的地理坐标
                String uid = mapPoi.getUid();//获取兴趣点的UID
                Log.e("MapClickListener", "纬度:" + latLng.latitude + ", 经度:" + latLng.longitude
                        + ", name:" + name + ", uid:" + uid);
            }
        });
    }

    //DotOptions 圆点覆盖物
    private void setDotOptions() {

    }

    //GroundOverlayOptions ground覆盖物
    private void setGroundOverlayOptions() {

    }

    //PolygonOptions 多边形覆盖物
    private void setPolygonOptions() {

    }

    //PolylineOptions 折线覆盖物
    private void setPolylineOptions() {

    }

    //ArcOptions 弧线覆盖物
    private void setArcOptions() {

    }

    private void set() {
        //放大一个级别
        MapStatusUpdate in = MapStatusUpdateFactory.zoomIn();
        baiduMap.setMapStatus(in);

        //缩小一个级别
        MapStatusUpdate out = MapStatusUpdateFactory.zoomOut();
        baiduMap.setMapStatus(out);

        //以一个点为中心 旋转（rotate）
        MapStatus msRotate = baiduMap.getMapStatus();//获取地图的状态
        float rotate = msRotate.rotate;//获取地图的旋转角度(0~360)
        Log.e("MapStatus", "rotate = " + rotate);
        //设置一个新的角度
        MapStatus msRotateNew = new MapStatus.Builder().rotate(rotate + 30).build();
        MapStatusUpdate rotateUpdate = MapStatusUpdateFactory.newMapStatus(msRotateNew);
        baiduMap.setMapStatus(rotateUpdate);

        //以一条直线为轴旋转 俯角（overlook）
        MapStatus msOver = baiduMap.getMapStatus();
        float overlook = msOver.overlook;//获取地图的俯角角度（0~45）
        Log.e("MapStatus", "overlook = " + overlook);
        //设置一个新的俯角
        MapStatus msOverNew = new MapStatus.Builder().overlook(overlook - 5).build();
        MapStatusUpdate overlookUpdate = MapStatusUpdateFactory.newMapStatus(msOverNew);
        baiduMap.setMapStatus(overlookUpdate);

        //移动到指定位置（带动画效果）
        MapStatusUpdate move = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        baiduMap.animateMapStatus(move);

        //获取地图UI控制器
        UiSettings us = baiduMap.getUiSettings();
        //设置指南针是否显示（指南针在3D模式下默认显示）
        us.setCompassEnabled(false);

        //设置地图单击事件监听者
        //baiduMap.setOnMapClickListener(BaiduMap.OnMapClickListener listener);
        //设置地图 Marker 覆盖物点击事件监听者
        //baiduMap.setOnMarkerClickListener(BaiduMap.OnMarkerClickListener listener);
        //设置地图双击事件监听者
        //baiduMap.setOnMapDoubleClickListener(BaiduMap.OnMapDoubleClickListener listener);
        //发起截图请求
        //baiduMap.snapshot(BaiduMap.SnapshotReadyCallback call);

        //显示实时交通信息图
        //baiduMap.setTrafficEnabled(true);

        //显示卫星图
        //baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
    }


}
