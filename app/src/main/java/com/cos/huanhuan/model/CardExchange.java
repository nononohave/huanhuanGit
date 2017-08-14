package com.cos.huanhuan.model;

/**
 * 首页展示的卡片model
 * Created by Administrator on 2017/8/14.
 */

public class CardExchange {

    private String cardId;//卡片对应的ID
    private String cardImgUrl;//图片地址
    private String createName;//交换人名称
    private String cardTitle;//卡片名称
    private String official;//最终值

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
