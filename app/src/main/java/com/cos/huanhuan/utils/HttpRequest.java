package com.cos.huanhuan.utils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

/**
 * Created by Administrator on 2017/8/7.
 */

public class HttpRequest {

    public static String TEXT_HUANHUAN_HOST = "http://api.52cos.cn/api/v1/";

    public static void loginSendMsgCode( String phone,StringCallback stringCallback) throws JSONException {
        String url = TEXT_HUANHUAN_HOST + "Members/SmsCode";
        OkHttpUtils.get().url(url).addParams("phone", phone).build().execute(stringCallback);
    }
}
