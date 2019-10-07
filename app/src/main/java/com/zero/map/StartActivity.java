package com.zero.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * APP 启动页面
 */
public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
        initData();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

    }
}
