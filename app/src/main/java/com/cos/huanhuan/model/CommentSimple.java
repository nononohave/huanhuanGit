package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/21.
 */

public class CommentSimple {

    private int exId;
    private int userId;
    private String Text;

    public int getExId() {
        return exId;
    }

    public void setExId(int exId) {
        this.exId = exId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getComment() {
        return Text;
    }

    public void setComment(String Text) {
        this.Text = Text;
    }
}
