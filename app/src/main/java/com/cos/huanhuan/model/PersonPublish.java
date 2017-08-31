package com.cos.huanhuan.model;

import com.cos.huanhuan.utils.HttpRequest;

/**
 * Created by Administrator on 2017/8/31.
 */

public class PersonPublish {

    private int id;//兑换id
    private String title;//标题
    private String examine;//状态
    private String nickname;//发布人名称
    private String cover;//封面
    private String addTime;//发布时间,
    private String logisticCode;//物流单号
    private Boolean isRefuse = false;//是否拒绝显示
    private Boolean isAgree = false;//是否同意显示
    private Boolean isSendGoods = false;//是否发货显示
    private Boolean isDelete = false;//是否删除显示

    public Boolean getRefuse() {
        return isRefuse;
    }

    public void setRefuse(Boolean refuse) {
        isRefuse = refuse;
    }

    public Boolean getAgree() {
        return isAgree;
    }

    public void setAgree(Boolean agree) {
        isAgree = agree;
    }

    public Boolean getSendGoods() {
        return isSendGoods;
    }

    public void setSendGoods(Boolean sendGoods) {
        isSendGoods = sendGoods;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
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

    public String getExamine() {
        return examine;
    }

    public void setExamine(String examine) {
        this.examine = examine;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCover() {
        return HttpRequest.IMG_HUANHUAN_HOST + cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getLogisticCode() {
        return logisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        this.logisticCode = logisticCode;
    }
}
