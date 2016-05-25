package com.haosu.schedulebook.model;

import android.database.Cursor;
import android.util.Log;

import com.haosu.schedulebook.db.XUtil;
import com.haosu.schedulebook.util.DateUtil;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haosu on 2016/4/24.
 */
public class StaticItem implements Comparable<StaticItem> {

    private String fullDate;
    private String date;
    private int totalCount = 0;
    private int finishCount = 0;

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = DateUtil.simpleDate(date);
        this.fullDate = date;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getFinishCount() {
        return finishCount;
    }

    public void setFinishCount(int finishCount) {
        this.finishCount = finishCount;
    }

    public static List<StaticItem> sort(List<StaticItem> list) {
        Collections.sort(list);
        return list;
    }

    @Override
    public int compareTo(StaticItem another) {
        return this.fullDate.compareTo(another.fullDate);
    }

    final static String FIND_ALL_SQL = "select date, count(*) from schedule_item group by date";
    final static String FINISH_SQL = "select date, count(*) from schedule_item where finish = 1 group by date";

    public static Map<String, StaticItem> load() {
        Map<String, StaticItem> map = new HashMap<>();

        try {
            DbManager.DaoConfig daoConfig = XUtil.getDaoConfig();
            DbManager db = x.getDb(daoConfig);
            Cursor cursor = db.execQuery(FIND_ALL_SQL);
            while (cursor.moveToNext()) {
                String date = cursor.getString(0);
                int count = cursor.getInt(1);
                StaticItem item = new StaticItem();
                item.setDate(date);
                item.setTotalCount(count);
                map.put(date, item);
            }

            cursor = db.execQuery(FINISH_SQL);
            while (cursor.moveToNext()) {
                String date = cursor.getString(0);
                int count = cursor.getInt(1);
                StaticItem item = null;
                if (map.containsKey(date)) {
                    item = map.get(date);
                } else {
                    item = new StaticItem();
                }
                if (item != null) {
                    item.setDate(date);
                    item.setFinishCount(count);
                    map.put(date, item);
                }
            }
        } catch (Exception e) {
            Log.e(StaticItem.class.getSimpleName(), e.getMessage());
        }
        map = completeMap(map);
        return map;
    }

    private static Map<String, StaticItem> completeMap(Map<String, StaticItem> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        String min = DateUtil.simpleFormat();
        String max = DateUtil.simpleFormat();
        for (StaticItem item : map.values()) {
            if (item.fullDate.compareTo(min) < 0) {
                min = item.fullDate;
            }
            if (item.fullDate.compareTo(max) > 0) {
                max = item.fullDate;
            }
        }
        Log.i(StaticItem.class.getSimpleName(), "min: " + min);
        Log.i(StaticItem.class.getSimpleName(), "max:" + max);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.stringToDate(min));

        while (calendar.getTime().compareTo(DateUtil.stringToDate(max)) <= 0) {
            String key = DateUtil.simpleFormat(calendar.getTime());
            if (!map.containsKey(key)) {
                Log.d(StaticItem.class.getSimpleName(), "key:" + key);
                StaticItem item = new StaticItem();
                item.setDate(key);
                item.setTotalCount(0);
                item.setFinishCount(0);
                map.put(key, item);
            }
            calendar.add(Calendar.DATE, 1);
        }
        return map;
    }


}
