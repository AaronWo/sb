package com.haosu.schedulebook;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.haosu.schedulebook.db.XUtil;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by haosu on 2016/4/23.
 */
public class StaticActivity extends BaseActivity {

    private Toolbar toolbar;
    private LineChartView lineChartView;

    @Override
    public void initWidgt() {
        setContentView(R.layout.static_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lineChartView = (LineChartView) findViewById(R.id.static_line_char);
        lineChartView.setInteractive(false);
        lineChartView.setZoomEnabled(false);
    }

    @Override
    public void initData() {
        new StaticTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class StaticTask extends AsyncTask<Void, Void, Void> {

        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();

        List<PointValue> finishValues = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DbManager.DaoConfig daoConfig = XUtil.getDaoConfig();
                DbManager db = x.getDb(daoConfig);
                final String sql = "select date, count(*) from schedule_item group by date";
                Cursor cursor = db.execQuery(sql);
                int i = 0;
                while (cursor.moveToNext()) {
                    values.add(new PointValue(i, cursor.getInt(1)));
                    axisValues.add(new AxisValue(i).setLabel(cursor.getString(0)));
                    i++;
                }

                final String finishSql = "select date, count(*) from schedule_item where finish = 1 group by date";
                cursor = db.execQuery(finishSql);
                i = 0;
                while (cursor.moveToNext()) {
                    Log.i("finish sql", cursor.getString(0) + ": " + cursor.getInt(1));
                    finishValues.add(new PointValue(i, cursor.getInt(1)));
                    i++;
                }
            } catch (Exception e) {
                Log.e("DB_OPERATION", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Line line = new Line(values).setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)).setCubic(false);
            Line finishLine = new Line(finishValues).setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark)).setCubic(false);
            List<Line> lines = new ArrayList<>();
            lines.add(line);
            lines.add(finishLine);
            LineChartData data = new LineChartData();
            data.setLines(lines);

            //坐标轴
            Axis axisX = new Axis(); //X轴
            axisX.setHasTiltedLabels(true);
            axisX.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            axisX.setName("date");
            axisX.setMaxLabelChars(10);
            axisX.setValues(axisValues);
            data.setAxisXBottom(axisX);

            Axis axisY = new Axis();  //Y轴
            axisY.setMaxLabelChars(7); //默认是3，只能看最后三个数字
            data.setAxisYLeft(axisY);


            //设置行为属性，支持缩放、滑动以及平移
            lineChartView.setInteractive(true);
            lineChartView.setZoomType(ZoomType.HORIZONTAL);
            lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
            lineChartView.setLineChartData(data);
            lineChartView.setVisibility(View.VISIBLE);
        }
    }

}
