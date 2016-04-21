package com.haosu.schedulebook.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by haosu on 2016/4/20.
 */
@Table(name = "schedule_item")
public class ScheduleItem implements Serializable {

    @Column(name = "id", isId = true, autoGen = true)
    private int id;
    @Column(name = "date")
    private String date;
    @Column(name = "text")
    private String text;
    @Column(name = "finish")
    private boolean isFinish;

    public ScheduleItem() {

    }

    public ScheduleItem(String text, boolean isFinish) {
        this.text = text;
        this.isFinish = isFinish;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
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
