package com.cos.huanhuan.model;

import com.cos.huanhuan.utils.HttpRequest;

/**
 * Created by Administrator on 2017/8/25.
 */

public class UserLoginData {

    private int id;//用户id
    private String portrait;//用户头像
    private String nickname;//用户昵称
    private String rongToken;//融云token

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPortrait() {
        return HttpRequest.IMG_HUANHUAN_HOST + portrait;
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

    public String getRongToken() {
        return rongToken;
    }

    public void setRongToken(String rongToken) {
        this.rongToken = rongToken;
    }
}
