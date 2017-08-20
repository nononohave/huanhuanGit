package com.cos.huanhuan.model;

import com.cos.huanhuan.utils.HttpRequest;

import java.util.List;

/**
 * Created by Administrator on 2017/8/19.
 */

public class CoopDetail {
    private int id;
    private String title;
    private String enrollEnd;
    private String will;
    private String address;
    private String describe;
    private List<Image> imgList;
    private int personNum;
    private int limitPerson;
    private String portrait;
    private String nickname;
    private String userId;
    private String desc;
    private Boolean heed;
    private int commentNum;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getEnrollEnd() {
        return enrollEnd;
    }

    public void setEnrollEnd(String enrollEnd) {
        this.enrollEnd = enrollEnd;
    }

    public String getWill() {
        return will;
    }

    public void setWill(String will) {
        this.will = will;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public List<Image> getImgList() {
        return imgList;
    }

    public void setImgList(List<Image> imgList) {
        this.imgList = imgList;
    }

    public int getPersonNum() {
        return personNum;
    }

    public void setPersonNum(int personNum) {
        this.personNum = personNum;
    }

    public int getLimitPerson() {
        return limitPerson;
    }

    public void setLimitPerson(int limitPerson) {
        this.limitPerson = limitPerson;
    }

    public String getPortrait() {
        return HttpRequest.IMG_HUANHUAN_HOST + portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getHeed() {
        return heed;
    }

    public void setHeed(Boolean heed) {
        this.heed = heed;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }
}
