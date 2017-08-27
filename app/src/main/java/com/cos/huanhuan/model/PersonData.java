package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/27.
 */

public class PersonData {

    private int id;//用户id
    private String Describe;//个性签名
    private String Nickname;//昵称
    private String Gender;//性别

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescribe() {
        return Describe;
    }

    public void setDescribe(String describe) {
        Describe = describe;
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
}
