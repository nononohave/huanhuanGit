package com.cos.huanhuan.model;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/18.
 */

public class PublishCoop {

    private int UserId;
    private String Title;
    private String Describe;
    private int Cover;
    private String ImgList;
    private Date EnrollEnd;
    private String Will;
    private int LimitPerson;
    private String Prov;
    private String City;
    private String Dist;
    private String Address;

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescribe() {
        return Describe;
    }

    public void setDescribe(String describe) {
        Describe = describe;
    }

    public int getCover() {
        return Cover;
    }

    public void setCover(int cover) {
        Cover = cover;
    }

    public String getImgList() {
        return ImgList;
    }

    public void setImgList(String imgList) {
        ImgList = imgList;
    }

    public Date getEnrollEnd() {
        return EnrollEnd;
    }

    public void setEnrollEnd(Date enrollEnd) {
        EnrollEnd = enrollEnd;
    }

    public String getWill() {
        return Will;
    }

    public void setWill(String will) {
        Will = will;
    }

    public int getLimitPerson() {
        return LimitPerson;
    }

    public void setLimitPerson(int limitPerson) {
        LimitPerson = limitPerson;
    }

    public String getProv() {
        return Prov;
    }

    public void setProv(String prov) {
        Prov = prov;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getDist() {
        return Dist;
    }

    public void setDist(String dist) {
        Dist = dist;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
