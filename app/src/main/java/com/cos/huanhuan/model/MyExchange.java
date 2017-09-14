package com.cos.huanhuan.model;

import com.cos.huanhuan.utils.HttpRequest;

/**
 * Created by Administrator on 2017/8/31.
 */

public class MyExchange {

    private int id;//兑换id
    private String serialNum;//兑换编号
    private String logisticCode;//物流编号
    private String state;//物流状态
    private String cover;//封面
    private String title;//标题
    private String nickname;//昵称
    private String addTime;//时间
    private String portrait;//头像
    private int examineId;//兑换状态，租赁时，如果为4则需要输入物流单号

    public int getExamineId() {
        return examineId;
    }

    public void setExamineId(int examineId) {
        this.examineId = examineId;
    }

    public String getPortrait() {
        return HttpRequest.IMG_HUANHUAN_HOST +portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getLogisticCode() {
        return logisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        this.logisticCode = logisticCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
}
