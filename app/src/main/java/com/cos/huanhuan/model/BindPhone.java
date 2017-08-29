package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/29.
 */

public class BindPhone {

    private int Id;//用户id
    private String VerifyCode;//短信验证码
    private String Phone;//手机号

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getVerifyCode() {
        return VerifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        VerifyCode = verifyCode;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
