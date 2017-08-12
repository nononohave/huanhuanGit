package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/11.
 */

public class UserLogin {
    String UserName;
    String Type;
    String Nickname;
    String Gender;
    String Figureurl;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getFigureurl() {
        return Figureurl;
    }

    public void setFigureurl(String figureurl) {
        Figureurl = figureurl;
    }
}
