package com.haosu.schedulebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haosu.schedulebook.db.XUtil;
import com.haosu.schedulebook.listeners.GestureDetectorListener;
import com.haosu.schedulebook.model.ScheduleItem;
import com.haosu.schedulebook.util.DateUtil;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

import org.xutils.x;

import org.xutils.DbManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by haosu on 2016/4/20.
 */
public class CreateSchedulItemActivity extends BaseActivity {

    private EditText editText;
    private ScheduleItem scheduleItem;

    private GestureDetectorCompat gestureDetector;

    @Override
    public void initWidgt() {
        setContentView(R.layout.schedule_create_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editText = (EditText) findViewById(R.id.create_schedule_input);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.i(CreateSchedulItemActivity.class.getSimpleName(), "action done");
                    return save();
                } else {
                    Log.i(CreateSchedulItemActivity.class.getSimpleName(), "other action");
                }
                return false;
            }
        });
        Bundle bundle = getIntent().getExtras();
        try {
            ScheduleItem item = (ScheduleItem) bundle.getSerializable("item");
            if (item != null) {
                scheduleItem = item;
                editText.setText(item.getText());
            }
        } catch (Exception e) {
            Log.v(getClass().getSimpleName(), e.getMessage(), e);
        }

        gestureDetector = new GestureDetectorCompat(this, new GestureDetectorListener(this));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(this, this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit_schedule:
                Log.i(CreateSchedulItemActivity.class.getSimpleName(), "create schedule menu click");
                save();
                break;
            default:
                Log.i(CreateSchedulItemActivity.class.getSimpleName(), "other menu {} click");
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean save() {
        if ("".equals(editText.getText().toString().trim())) {
            Toast.makeText(this, "please write down your schedule", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (scheduleItem == null) {
            scheduleItem = new ScheduleItem();
            scheduleItem.setFinish(false);
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            Log.i(CreateSchedulItemActivity.class.getSimpleName(), "now hour is: " + hour);
            if (hour > 20) {
                // is night, create schedule for tomorrow
                Date today = new Date();
                c.setTime(today);
                c.add(Calendar.DAY_OF_YEAR, +1);
                String tomorrowStr = DateUtil.simpleFormat(c.getTime());
                Log.i(CreateSchedulItemActivity.class.getSimpleName(), "tomorrow is: " + tomorrowStr);
                scheduleItem.setDate(tomorrowStr);
            } else {
                scheduleItem.setDate(DateUtil.simpleFormat());
            }
        }
        scheduleItem.setText(editText.getText().toString().trim());
        try {
            DbManager.DaoConfig daoConfig = XUtil.getDaoConfig();
            DbManager db = x.getDb(daoConfig);
            db.saveOrUpdate(scheduleItem);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
        }

        this.finish();
        return true;
    }
}
