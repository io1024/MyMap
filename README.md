# MyMap

    一个集成了百度地图SDK的自定义地图
    
## libs 说明

    arm64-v8a、armeabi、armeabi-v7a、x86、x86_64  是针对不同CPU的手机而存在的SO包（每个目录下的so包功能都是相同的，只是计算速度不同）

## 百度地图SDK接入步骤

    1. 获取API key
    2. 建立工程
    3. 添加地图引擎到工程中
    4. 添加权限
    5. 初始化地图引擎
    6. 引入布局（地图控件）

## 百度地图的绘制
        在 MapView 上绘制（包含点：地图图层、覆盖物、搜索(也是一种覆盖物)）
        地图图层--纸
        覆盖物 ---几何图形
        绘制

        地图图层分类
            1. 底图：包含:道路、街道、学校、公园等内容
            2. 实时交通信息图：baiduMap.setTrafficEnabled(true);
            3. 卫星图：baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

        覆盖物：本地覆盖物 和 搜索覆盖物
            
        本地覆盖物的抽象基类：OverlayOptions
                子类：ArcOptions, CircleOptions, DotOptions, GroundOverlayOptions, HoleOptions,
                     MarkerOptions, PolygonOptions, PolylineOptions, TextOptions
            圆形覆盖物：CircleOptions
            文字覆盖物：TextOptions
            marker覆盖物：MarkerOptions（如：地图上的出租车,都是通过该类进行展示的,出租车是图片,也可以用画笔画）
            圆点覆盖物：DotOptions
            ground覆盖物：GroundOverlayOptions
            多边形覆盖物：PolygonOptions
            折线覆盖物：PolylineOptions
            弧线覆盖物：ArcOptions
           
        《特别注意：引入 com.baidu.mapapi.overlayutil 的时候，需要一起引入一些资源文件：assets+》    
        搜索覆盖物的抽象基类：OverlayManager
            本地搜索覆盖物：PoiOverlay
                POI(Point of Interest兴趣点)搜索有三种方式
                    1. 根据范围和检索词发起范围检索 searchInBound
                    2. 周边检索 searchNearby
                    3. 城市poi检索 searchInCity
                    4. poi详细信息检索 searchPoiDetail
                    
            驾车路线覆盖物：DrivingRouteOverlay
                    1. RoutePlanSearch
            
            步行路线覆盖物：WalkingRouteOverlay
            换成路线覆盖物：TransitRouteOverlay（TransitOverlay）
            公交路线覆盖物：BusLineOverlay
                
        覆盖物操作（绘制）步骤：
            1. 创建覆盖物
            2. 给覆盖物设置数据（不同覆盖物的数据设置不同）
            3. 添加覆盖物

        地址解析服务
            根据经纬度获取位置名称  geoCoder.reverseGeoCode();
            根据位置名称获取经纬度  geoCoder.geocode();
            
        联想词检索
            SuggestionSearch
            SuggestionSearchOption
            结果处理：监听回调：suggestionSearch.setOnGetSuggestionResultListener();
        
        公交路线详细信息搜索
            BusLineSearch
            BusLineSearchOption
        


## 百度核心类
    SDKInitializer

        相当于引擎，用来初始化百度地图。构造器方法：可以指定缓存路径


	MapView

	    一个自定义View,用来显示百度地图。必须把它的生命周期和Activity的生命周期进行绑定。
        常用设置： 比例尺、缩放按钮

        获取：getMap()-BaiduMap  获取地图控制器
             getMapLevel()-int  获取当前地图级别对应比例尺大小

        设置：setZoomControlsPosition(Point p)  设置缩放控件的位置，在 onMapLoadFinish 后生效
             setScaleControlPosition(Point p)  设置比例尺控件的位置，在 onMapLoadFinish 后生效


	BaiduMap

	    相当于控制器，用于操作MapView（缩放（放大缩小）、旋转（带俯角旋转）、移动）
	    常用设置：添加热力图（城市拥堵情况）
	            添加覆盖物
	            更新地图状态（可以带动画效果）

	    获取：getAllInfoWindows()-java.util.List<InfoWindow>  获取已添加的所有InfoWindow对象(5.4.0版本新增接口 )
             getCompassPosition()-Point  获取屏幕坐标系下指南针位置
             getFocusedBaseIndoorMapInfo()-MapBaseIndoorMapInfo  获取当前聚焦的室内图信息
             getLocationConfiguration()-MyLocationConfiguration  获取定位图层配置信息
             getLocationData()-MyLocationData  获取定位数据
             getMapStatus()-MapStatus  获取地图的当前状态
             getMapStatusLimit()-LatLngBounds  获取地图可移动区域
             getMapType()-int  获取地图当前的模式，空白地图、普通地图或者卫星图
             getMarkersInBounds(LatLngBounds bounds)-java.util.List<Marker>  获取指定区域内所有的Marker点
             getMaxZoomLevel()-float  获取地图最大缩放级别
             getMinZoomLevel()-float  获取地图最小缩放级别
             getProjection()-Projection  获取地图投影坐标转换器, 当地图初始化完成之前返回 null，在 OnMapLoadedCallback.onMapLoaded() 之后才能正常
             getProjectionMatrix()-float[]  获取OpenGL投影矩阵 数组结果，适用于3D绘制场景，V5.3.0版本新增
             getUiSettings()-UiSettings  获取地图ui控制器
             getViewMatrix()-float[]  获取OpenGL视图矩阵 数组结果，适用于3D绘制场景，V5.3.0版本新增
             getZoomToBound(int l, int t, int r, int b, int width, int height)-float  根据Bound和指定宽高获取对应的缩放级别

        地图事件
            设置地图单击事件监听者  baiduMap.setOnMapClickListener(BaiduMap.OnMapClickListener listener);
            设置地图 Marker 覆盖物点击事件监听者  baiduMap.setOnMarkerClickListener(BaiduMap.OnMarkerClickListener listener);
            设置地图双击事件监听者  baiduMap.setOnMapDoubleClickListener(BaiduMap.OnMapDoubleClickListener listener);
            发起截图请求  baiduMap.snapshot(BaiduMap.SnapshotReadyCallback call);

## 百度地图定位
        
        返回的是：经纬度。采用的火星坐标系（非地球真正的经纬度）。网络定位(基站定位 + WIFI定位)
        GPS定位（卫星定位，3颗卫星定位）优先使用
        基站定位：不可控
        WIFI定位：精度更高、不受管制的定位方法