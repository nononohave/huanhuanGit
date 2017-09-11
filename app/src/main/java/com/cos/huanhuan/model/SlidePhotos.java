package com.cos.huanhuan.model;

import com.cos.huanhuan.utils.HttpRequest;

/**
 * Created by Administrator on 2017/9/3.
 */

public class SlidePhotos {
    private String imgText;//图片描述内容
    private String imgUrl;//图片地址
    private String imgHref;//图片链接

    public String getImgText() {
        return imgText;
    }

    public void setImgText(String imgText) {
        this.imgText = imgText;
    }

    public String getImgUrl() {
        return imgUrl.substring(0,4).equals("http")?imgUrl : HttpRequest.IMG_HUANHUAN_HOST + imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgHref() {
        return imgHref;
    }

    public void setImgHref(String imgHref) {
        this.imgHref = imgHref;
    }
}
