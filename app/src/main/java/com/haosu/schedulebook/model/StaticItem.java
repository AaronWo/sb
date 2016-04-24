package com.haosu.schedulebook.model;

import com.haosu.schedulebook.util.DateUtil;

/**
 * Created by haosu on 2016/4/24.
 */
public class StaticItem {

    private String date;
    private int totalCount = 0;
    private int finishCount = 0;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        try {
            String today = DateUtil.simpleFormat();
            String[] items = today.split("-");
            String year = items[0];
            String month = items[1];

            String[] dateItems = date.split("-");
            if (year.equals(dateItems[0])) {
                if (month.equals(dateItems[1])) {
                    this.date = dateItems[2];
                } else {
                    this.date = dateItems[1] + "-" + dateItems[2];
                }
            } else {
                this.date = date;
            }
        } catch (Exception e) {
            this.date = date;
        }
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
}
