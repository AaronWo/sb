package com.haosu.schedulebook.model;

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
        this.date = date;
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
