package com.haosu.schedulebook;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.haosu.schedulebook.db.XUtil;
import com.haosu.schedulebook.model.ScheduleItem;
import com.haosu.schedulebook.util.DateUtil;

import org.xutils.x;

import org.xutils.DbManager;

/**
 * Created by haosu on 2016/4/20.
 */
public class CreateSchedulItemActivity extends BaseActivity {

    private EditText editText;

    @Override
    public void initWidgt() {
        setContentView(R.layout.schedule_create_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) findViewById(R.id.create_schedule_input);
    }

    @Override
    public void initData() {

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
                if ("".equals(editText.getText().toString().trim())) {
                    Toast.makeText(this, "please write down your schedule", Toast.LENGTH_SHORT).show();
                    return true;
                }
                ScheduleItem scheduleItem = new ScheduleItem();
                scheduleItem.setFinish(false);
                scheduleItem.setText(editText.getText().toString().trim());
                scheduleItem.setDate(DateUtil.simpleFormat());
                try {
                    DbManager.DaoConfig daoConfig = XUtil.getDaoConfig();
                    DbManager db = x.getDb(daoConfig);
                    db.save(scheduleItem);
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                this.finish();
                return true;
            case android.R.id.home:
                this.finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
