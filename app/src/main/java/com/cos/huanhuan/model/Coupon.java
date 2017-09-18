package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/9/18.
 */

public class Coupon {

    private int id;//兑换码id
    private String title;//标题
    private String description;//说明
    private String endTime;//结束时间
    private Boolean isValid;//是否失效

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }
}
