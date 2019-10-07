package com.zero.map.view;

import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.zero.map.MyApplication;

import java.util.List;

/**
 * 自定义
 * 范围内搜索，重写 onPoiClick 方法 实现点击反馈
 */
public class MyPoiOverlay extends PoiOverlay {

    private PoiSearch poiSearch;

    /**
     * 构造函数
     *
     * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
     */
    public MyPoiOverlay(BaiduMap baiduMap, PoiSearch poiSearch) {
        super(baiduMap);
        this.poiSearch = poiSearch;
    }

    @Override
    public boolean onPoiClick(int index) {
        //获取所有搜索到的 PoiOverlay 结果
        PoiResult poiResult = getPoiResult();
        //获取返回来的所有POI（Point of Interest兴趣点）
        List<PoiInfo> allPoi = poiResult.getAllPoi();
        //获取点击的位置信息
        PoiInfo poiInfo = allPoi.get(index);
        String address = poiInfo.address;//地址
        String city = poiInfo.city;//城市
        String name = poiInfo.name;//poi名称
        //poi类型：0-普通点,1-公交站,2-公交线路,3-地铁站,4-地铁线路
        PoiInfo.POITYPE type = poiInfo.type;
        //uid 是百度数据库内存储该poi的一个key值，通过uid可以获取该poi的详细信息
        String uid = poiInfo.uid;

        Toast.makeText(MyApplication.getAppContext(), city + ":" + address, Toast.LENGTH_SHORT).show();

        //获取 poi详细信息检索 对象
        PoiDetailSearchOption detailOption = new PoiDetailSearchOption();
        detailOption.poiUid(uid); //设置 poi 的 UID
        poiSearch.searchPoiDetail(detailOption);

        return super.onPoiClick(index);
    }
}
