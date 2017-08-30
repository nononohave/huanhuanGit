package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/30.
 */

public class Recharge {
    private int userId;//用户id
    private Double Money;//充值金额
    private String Type;//充值类型 身家充值|会员充值
    private String PayType;//支付类型 Ali|Wx

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Double getMoney() {
        return Money;
    }

    public void setMoney(Double money) {
        Money = money;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getPayType() {
        return PayType;
    }

    public void setPayType(String payType) {
        PayType = payType;
    }
}
