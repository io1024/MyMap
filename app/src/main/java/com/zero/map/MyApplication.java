package com.zero.map;

import android.app.Application;
import android.content.IntentFilter;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.zero.map.receiver.BaiSDKReceiver;

/**
 * 程序入口
 */
public class MyApplication extends Application {

    public static boolean baiDuSDK = false;//百度初始化是否可用
    private static MyApplication context;//全局上下文
    private BaiSDKReceiver baiSDKReceiver;

    public static MyApplication getAppContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //程序创建的时候执行
        context = this;
        initReceiver();
        initSdk();
    }

    @Override
    public void onTerminate() {
        //程序终止的时候执行
        super.onTerminate();
        if (baiSDKReceiver != null) {
            unregisterReceiver(baiSDKReceiver);
        }
    }

    @Override
    public void onLowMemory() {
        //低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        //程序在内存清理的时候执行
        super.onTrimMemory(level);
    }


    private void initReceiver() {
        baiSDKReceiver = new BaiSDKReceiver();
        IntentFilter filter = new IntentFilter();
        //key 验证成功广播 action string
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        //key 验证失败广播 action string
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        //网络错误广播 action string
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        //key 验证失败或者使用样式ID服务端加载样式失败 广播 intent 中附加信息错误码键
        filter.addAction(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE);
        //key 验证失败或者使用样式ID服务端加载样式失败 广播 intent 中附加信息错误信息键
        filter.addAction(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_MESSAGE);
//        //使用样式ID服务端加载样式失败 广播 action string
//        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_LOAD_CUSTOM_STYLE_ERROR);
//        //使用样式ID服务端加载样式成功 广播 action string
//        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_LOAD_CUSTOM_STYLE_ERROR);
        registerReceiver(baiSDKReceiver, filter);
    }

    //初始化三方SDK
    private void initSdk() {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
