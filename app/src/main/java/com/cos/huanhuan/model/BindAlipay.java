package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/29.
 */

public class BindAlipay {

    private int Id;//用户id
    private String RealName;//真实姓名
    private String ImAlipay;//支付宝账号

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getImAlipay() {
        return ImAlipay;
    }

    public void setImAlipay(String imAlipay) {
        ImAlipay = imAlipay;
    }
}
