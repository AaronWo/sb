package com.haosu.schedulebook.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by haosu on 2016/4/21.
 */
public class DateUtil {

    private static SimpleDateFormat simpleDateFormat;

    public static String simpleFormat() {
        if (simpleDateFormat == null) simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }

}
