package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/18.
 */

public class CoopList {
    private int pageIndex;//当前页
    private int pageSize;//每页数量
    private String cid;//分类id
    private String city;//市

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
