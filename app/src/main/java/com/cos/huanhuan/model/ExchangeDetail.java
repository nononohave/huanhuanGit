package com.cos.huanhuan.model;

import com.cos.huanhuan.utils.HttpRequest;

import java.util.List;

/**
 * Created by Administrator on 2017/8/21.
 */

public class ExchangeDetail {
    private int id;//兑换id
    private String title;//兑换标题
    private String describe;//详细说明
    private String itemName;//物品名称
    private String itemCharacter;//物品角色
    private List<Image> imgList;//图片地址
    private String constitute;//服装组成
    private String price;//原价
    private String className;//分类
    private String examineName;//状态
    private String official;//最终值
    private String addTime;//发布时间
    private int commentNum;//评论数量
    private Boolean heed;//是否关注
    private int userId;//用户id
    private String nickname;//昵称
    private String portrait;//头像
    private String desc;//个人描述

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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCharacter() {
        return itemCharacter;
    }

    public void setItemCharacter(String itemCharacter) {
        this.itemCharacter = itemCharacter;
    }

    public List<Image> getImgList() {
        return imgList;
    }

    public void setImgList(List<Image> imgList) {
        this.imgList = imgList;
    }

    public String getConstitute() {
        return constitute;
    }

    public void setConstitute(String constitute) {
        this.constitute = constitute;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getExamineName() {
        return examineName;
    }

    public void setExamineName(String examineName) {
        this.examineName = examineName;
    }

    public String getOfficial() {
        return official;
    }

    public void setOfficial(String official) {
        this.official = official;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public Boolean getHeed() {
        return heed;
    }

    public void setHeed(Boolean heed) {
        this.heed = heed;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortrait() {
        return HttpRequest.IMG_HUANHUAN_HOST + portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
