package com.haosu.schedulebook;

import android.support.v7.widget.Toolbar;

/**
 * Created by haosu on 2016/4/20.
 */
public class CreateSchedulItemActivity extends  BaseActivity {

    @Override
    public void initWidgt() {
        setContentView(R.layout.schedule_create_layout);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void initData() {

    }
}
