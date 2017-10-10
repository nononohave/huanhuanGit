package com.cos.huanhuan.apksupdate;

/**
 * Created by SHI on 2017/4/1 14:34
 */

public interface ApkUpdateInfoLoadBiz {

    String url_webHtml = "http://erp.9n19.com/mobile_proxy!proxyUrl.action?param=''&url='http://erp.9n19.com/getVersion.action'";

    public ApkUpdateInfoBean apkUpdateInfoLoad();
}
