package com.cos.huanhuan.model;

import com.cos.huanhuan.utils.HttpRequest;

/**
 * Created by Administrator on 2017/8/20.
 */

public class Comment {

    private int id;//评论id
    private int exId;//兑换Id
    private int userId;//发布该评论的用户id
    private String portrait;//发布该评论的用户头像
    private String nickname;//发布该评论的用户昵称
    private String text;//评论内容
    private String addTime;//评论发布时间
    private int likeNum;//点赞数
    private Boolean isLike;//登录用户是否已经点赞
    private int replyUserId;//该评论回复的评论用户id
    private String replyNickname;//该评论回复的评论用户昵称

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public Boolean getLike() {
        return isLike;
    }

    public void setLike(Boolean like) {
        isLike = like;
    }

    public int getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(int replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getReplyNickname() {
        return replyNickname;
    }

    public void setReplyNickname(String replyNickname) {
        this.replyNickname = replyNickname;
    }
}
