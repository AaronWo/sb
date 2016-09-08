package com.haosu.schedulebook.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by haosu on 2016/4/21.
 */
public class DateUtil {

    private static SimpleDateFormat simpleDateFormat;

    public static String simpleFormat() {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        return simpleDateFormat.format(new Date());
    }

    /***
     * format the date of the day near today
     *
     * @param gap: if gap ==1, return date for tomorrow, if gap == -1, return date of yesterday
     * @return date
     */
    public static String simpleFormatDateNearToday(int gap) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_YEAR, gap);
        return simpleFormat(c.getTime());
    }


    public static String simpleFormat(Date date) {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        return simpleDateFormat.format(date);
    }

    public static Date stringToDate(String string) {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        Date date;
        try {
            date = simpleDateFormat.parse(string);
        } catch (Exception e) {
            date = new Date();
        }
        return date;
    }

    public static String simpleDate(String date) {
        String result;
        try {
            String today = DateUtil.simpleFormat();
            String[] items = today.split("-");
            String year = items[0];
            String month = items[1];

            String[] dateItems = date.split("-");
            if (year.equals(dateItems[0])) {
                if (month.equals(dateItems[1])) {
                    result = dateItems[2];
                } else {
                    result = dateItems[1] + "-" + dateItems[2];
                }
            } else {
                result = date;
            }
        } catch (Exception e) {
            result = date;
        }
        return result;
    }


}
