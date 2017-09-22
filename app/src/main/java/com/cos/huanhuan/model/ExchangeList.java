package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/14.
 */

public class ExchangeList {

    private int pageIndex;//当前页
    private int pageSize;//每页数量
    private String sea;//搜索关键词
    private String cid;//分类id
    private String Eid;//状态id
    private Boolean rec;//是否是推荐

    public Boolean getRec() {
        return rec;
    }

    public void setRec(Boolean rec) {
        this.rec = rec;
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

    public String getSea() {
        return sea;
    }

    public void setSea(String sea) {
        this.sea = sea;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getEid() {
        return Eid;
    }

    public void setEid(String eid) {
        Eid = eid;
    }
}
