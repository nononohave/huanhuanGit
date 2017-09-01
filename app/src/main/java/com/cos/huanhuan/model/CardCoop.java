package com.cos.huanhuan.model;

import com.cos.huanhuan.utils.HttpRequest;

/**
 * 首页展示的卡片model
 * Created by Administrator on 2017/8/14.
 */

public class CardCoop {

    private String cardId;//卡片对应的ID
    private String cardImgUrl;//图片地址
    private String createName;//交换人名称
    private String cardTitle;//卡片名称
    private String official;//最终值
    private String address;//地点
    private int personNum;//参与人数
    private int userId;//用户ID
    private String portrait;//用户头像

    public String getPortrait() {
        return HttpRequest.IMG_HUANHUAN_HOST + portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPersonNum() {
        return personNum;
    }

    public void setPersonNum(int personNum) {
        this.personNum = personNum;
    }

    public String getOfficial() {
        return official;
    }

    public void setOfficial(String official) {
        this.official = official;
    }


    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardImgUrl() {
        return cardImgUrl;
    }

    public void setCardImgUrl(String cardImgUrl) {
        this.cardImgUrl = cardImgUrl;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }


}
