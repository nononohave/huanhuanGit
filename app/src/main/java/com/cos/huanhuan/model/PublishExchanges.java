package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/17.
 */

public class PublishExchanges {
    private String UserId;//发布会员id
    private String Title;//标题
    private String Describe;//内容
    private String ItemName;//物品名称
    private String ItemCharacter;//物品角色
    private int Cover;//封面
    private String ImgList;//图片列表
    private String Constitute;//服装组成
    private Double Price;//原价
    private int ClassId;//分类
    private String Source;//物品来源

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
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

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemCharacter() {
        return ItemCharacter;
    }

    public void setItemCharacter(String itemCharacter) {
        ItemCharacter = itemCharacter;
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

    public String getConstitute() {
        return Constitute;
    }

    public void setConstitute(String constitute) {
        Constitute = constitute;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public int getClassId() {
        return ClassId;
    }

    public void setClassId(int classId) {
        ClassId = classId;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }
}
