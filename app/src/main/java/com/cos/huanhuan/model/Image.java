package com.cos.huanhuan.model;

import com.cos.huanhuan.utils.HttpRequest;

/**
 * Created by Administrator on 2017/8/19.
 */

public class Image {

    private String imgPath;
    private int width;
    private int height;

    public String getImgPath() {
        //return HttpRequest.IMG_HUANHUAN_HOST + imgPath;
        return "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1075900967,1482338035&fm=26&gp=0.jpg";
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getWidth() {
        // return width;
        return 450;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        //return height;
        return 359;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
