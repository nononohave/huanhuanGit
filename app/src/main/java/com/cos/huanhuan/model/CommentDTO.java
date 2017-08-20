package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/20.
 */

public class CommentDTO {

    private int exId;//兑换id
    private int userId;//登录用户id
    private int pageIndex;//当前页
    private int pageSize;//每页数据数

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

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
