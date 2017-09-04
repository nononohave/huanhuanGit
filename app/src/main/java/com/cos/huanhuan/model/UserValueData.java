package com.cos.huanhuan.model;

import com.cos.huanhuan.utils.HttpRequest;

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
    private String gender;//性别
    private String describe;//个性签名
    private String phoneMob;//手机号
    private String imAlipay;//支付宝账号
    private Double deposit;//押金
    private String RealName;//真实姓名
    private String endTime;//会员结束时间
    private String rongToken;//融云

    public String getRongToken() {
        return rongToken;
    }

    public void setRongToken(String rongToken) {
        this.rongToken = rongToken;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getPhoneMob() {
        return phoneMob;
    }

    public void setPhoneMob(String phoneMob) {
        this.phoneMob = phoneMob;
    }

    public String getImAlipay() {
        return imAlipay;
    }

    public void setImAlipay(String imAlipay) {
        this.imAlipay = imAlipay;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPortrait() {
        return portrait.substring(0,4).equals("http")?portrait : HttpRequest.IMG_HUANHUAN_HOST + portrait;
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
