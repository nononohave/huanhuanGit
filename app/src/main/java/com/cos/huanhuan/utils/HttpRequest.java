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
        OkHttpUtils.get().url(url)
                .addParams("phone", phone)
                .build()
                .execute(stringCallback);
    }

    public static void register(String UserName,String Type,String Password,String VerifyCode,StringCallback stringCallback) throws JSONException{
        String url = TEXT_HUANHUAN_HOST + "Members/Register";
        OkHttpUtils.post().url(url)
                .addParams("UserName",UserName)
                .addParams("Type",Type)
                .addParams("Password",Password)
                .addParams("VerifyCode",VerifyCode)
                .build()
                .execute(stringCallback);
    }

    public static void login(String UserName, String Type, String Password,StringCallback stringCallback){
        String url = TEXT_HUANHUAN_HOST + "Members/Login";
        OkHttpUtils.post().url(url)
                .addParams("UserName",UserName)
                .addParams("Type",Type)
                .addParams("Password",Password)
                .build()
                .execute(stringCallback);
    }

    public static void resetPassword(String Password, String VerifyCode, String Phone,StringCallback stringCallback){
        String url = TEXT_HUANHUAN_HOST + "Members/Password";
        OkHttpUtils.post().url(url)
                .addParams("Password",Password)
                .addParams("VerifyCode",VerifyCode)
                .addParams("Phone",Phone)
                .build()
                .execute(stringCallback);
    }

    public static void resetPassword(StringCallback stringCallback){
        String url = TEXT_HUANHUAN_HOST + "Members/UserAgreement";
        OkHttpUtils.get().url(url)
                .build()
                .execute(stringCallback);
    }
}
