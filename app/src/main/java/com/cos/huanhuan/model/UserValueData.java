package com.cos.huanhuan.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/23.
 */

public class UserValueData implements Serializable{
    private int id;//用户id
    private String portrait;//用户头像
    private String nickname;//昵称
    private Double shenJia;//身家
    private int surplus;//剩余兑换次数
    private Boolean isVip;//是否是会员

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPortrait() {
        return portrait;
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

    public Double getShenJia() {
        return shenJia;
    }

    public void setShenJia(Double shenJia) {
        this.shenJia = shenJia;
    }

    public int getSurplus() {
        return surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public Boolean getVip() {
        return isVip;
    }

    public void setVip(Boolean vip) {
        isVip = vip;
    }
}
