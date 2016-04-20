package com.haosu.schedulebook.model;

/**
 * Created by haosu on 2016/4/20.
 */
public class ScheduleItem {
    private String id;
    private String date;
    private String text;
    private boolean isFinish;

    public ScheduleItem() {

    }

    public ScheduleItem(String text, boolean isFinish) {
        this.text = text;
        this.isFinish = isFinish;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }
}
