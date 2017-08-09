package com.cos.huanhuan.utils;

import android.util.Log;

import com.cos.huanhuan.model.RegisterModel;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Administrator on 2017/8/7.
 */

public class HttpRequest {

    public static String TEXT_HUANHUAN_HOST = "http://api.52cos.cn/api/v1/";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void loginSendMsgCode( String phone,StringCallback stringCallback) throws JSONException {
        String url = TEXT_HUANHUAN_HOST + "Members/SmsCode";
        OkHttpUtils.get().url(url)
                .addParams("phone", phone)
                .build()
                .execute(stringCallback);
    }

    public static void register(String UserName,String Type,String Password,String VerifyCode,StringCallback stringCallback) throws JSONException{
        String url = TEXT_HUANHUAN_HOST + "Members/Register";
        RegisterModel registerModel = new RegisterModel();
        registerModel.setUserName(UserName);
        registerModel.setType(Type);
        registerModel.setPassword(Password);
        registerModel.setVerifyCode(VerifyCode);
        OkHttpUtils.postString().url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(registerModel))
                .build()
                .execute(stringCallback);
    }

    public static void login(String UserName, String Type, String Password,StringCallback stringCallback){
        String url = TEXT_HUANHUAN_HOST + "Members/Login";
        RegisterModel registerModel = new RegisterModel();
        registerModel.setUserName(UserName);
        registerModel.setType(Type);
        registerModel.setPassword(Password);
        OkHttpUtils.postString().url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(registerModel))
                .build()
                .execute(stringCallback);
    }

    public static void resetPassword(String Password, String VerifyCode, String Phone,Callback callback) throws IOException {

//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        OkHttpClient client = new OkHttpClient();
//
//        RequestBody body = RequestBody.create(JSON, jsonStr);
//        Request request = new Request.Builder()
//                .url(url)
//                .put(body)
//                .build();

        String url = TEXT_HUANHUAN_HOST + "Members/Password";
        OkHttpClient client = new OkHttpClient();
        RegisterModel registerModel = new RegisterModel();
        registerModel.setPassword(Password);
        registerModel.setVerifyCode(VerifyCode);
        registerModel.setPhone(Phone);
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(registerModel));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }


    public static void resetPassword(StringCallback stringCallback){
        String url = TEXT_HUANHUAN_HOST + "Members/UserAgreement";
        OkHttpUtils.get().url(url)
                .build()
                .execute(stringCallback);
    }
}
