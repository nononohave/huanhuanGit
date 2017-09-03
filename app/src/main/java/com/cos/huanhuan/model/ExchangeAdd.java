package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/9/3.
 */

public class ExchangeAdd {

    private int AddressId;//收货地址id
    private int userId;//登录用户id
    private int ExId;//兑换id
    private String Examine;//兑换方式
    private String PayType;//支付类型

    public int getAddressId() {
        return AddressId;
    }

    public void setAddressId(int addressId) {
        AddressId = addressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getExId() {
        return ExId;
    }

    public void setExId(int exId) {
        ExId = exId;
    }

    public String getExamine() {
        return Examine;
    }

    public void setExamine(String examine) {
        Examine = examine;
    }

    public String getPayType() {
        return PayType;
    }

    public void setPayType(String payType) {
        PayType = payType;
    }
}
