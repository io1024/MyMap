package com.zero.map.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.zero.map.MyApplication;


/**
 * 百度地图 初始化 广播接收
 */
public class BaiSDKReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR:
                    //返回状态：网络错误
                    Log.e("SDKInitializer", "网络错误");
                    Toast.makeText(MyApplication.getAppContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                case SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR:
                    //返回状态：key 校验失败
                    Log.e("SDKInitializer", "key fail");

                    break;
                case SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK:
                    //返回状态：key 校验成功
                    MyApplication.baiDuSDK = true;
                    Log.e("SDKInitializer", "key success");
                    break;
                default:
                    //返回状态：其他错误
                    Log.e("SDKInitializer", "error：" + action);
                    break;
            }
        }
    }
}
