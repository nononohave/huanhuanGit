package com.cos.huanhuan.apksupdate;

import com.cos.huanhuan.utils.HttpRequest;

/**
 * Created by SHI on 2017/4/1 14:34
 */

public interface ApkUpdateInfoLoadBiz {

    String url_webHtml = HttpRequest.TEXT_HUANHUAN_HOST + "Members/GetVersion?terminal=Android";

    public ApkUpdateInfoBean apkUpdateInfoLoad(String versionName);
}
