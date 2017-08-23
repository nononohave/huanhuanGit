package com.cos.huanhuan.utils;

import android.util.Log;

import com.cos.huanhuan.model.AddressDTO;
import com.cos.huanhuan.model.CommentDTO;
import com.cos.huanhuan.model.CommentMuti;
import com.cos.huanhuan.model.CommentSimple;
import com.cos.huanhuan.model.CoopDetail;
import com.cos.huanhuan.model.CoopList;
import com.cos.huanhuan.model.ExchangeList;
import com.cos.huanhuan.model.PublishCoop;
import com.cos.huanhuan.model.PublishExchanges;
import com.cos.huanhuan.model.RegisterModel;
import com.cos.huanhuan.model.UserInfo;
import com.cos.huanhuan.model.UserLogin;
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

    public static String TEXT_HUANHUAN_HOST = "http://api.52cos.cn:8081/api/v1/";

    public static String IMG_HUANHUAN_HOST = "http://img.52cos.cn:8081";

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


    public static void getUserAgreeMent(StringCallback stringCallback){
        String url = TEXT_HUANHUAN_HOST + "Members/UserAgreement";
        OkHttpUtils.get().url(url)
                .build()
                .execute(stringCallback);
    }

    /**
     * 授权登录
     * @param UserName
     * @param Type
     * @param callback
     */
    public static void oauthLogin(String UserName, String Type, String Nickname, String Gender, String Figureurl, StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "Members/OauthLogin";
        UserLogin userLogin = new UserLogin();
        userLogin.setUserName(UserName);
        userLogin.setType(Type);
        userLogin.setNickname(Nickname);
        userLogin.setGender(Gender);
        userLogin.setFigureurl(Figureurl);
        OkHttpUtils.postString().url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(userLogin))
                .build()
                .execute(callback);
    }

    public static void getExchangeClass(StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "ExchangeClasses";
        OkHttpUtils.get().url(url)
                .build()
                .execute(callback);
    }

    public static void getExchangeList(ExchangeList exchange,StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "Exchanges";
        if(AppStringUtils.isEmpty(exchange.getCid()) && AppStringUtils.isEmpty(exchange.getEid())){
            OkHttpUtils.get().url(url)
                    .addParams("pageIndex", String.valueOf(exchange.getPageIndex()))
                    .addParams("pageSize", String.valueOf(exchange.getPageSize()))
                    .addParams("sea", String.valueOf(exchange.getSea()))
                    .build()
                    .execute(callback);}
        if(AppStringUtils.isNotEmpty(exchange.getCid()) && AppStringUtils.isEmpty(exchange.getEid())){
            OkHttpUtils.get().url(url)
                    .addParams("pageIndex", String.valueOf(exchange.getPageIndex()))
                    .addParams("pageSize", String.valueOf(exchange.getPageSize()))
                    .addParams("sea", String.valueOf(exchange.getSea()))
                    .addParams("cid",exchange.getCid())
                    .build()
                    .execute(callback);}
        if(AppStringUtils.isNotEmpty(exchange.getCid()) && AppStringUtils.isNotEmpty(exchange.getEid())){
            OkHttpUtils.get().url(url)
                    .addParams("pageIndex", String.valueOf(exchange.getPageIndex()))
                    .addParams("pageSize", String.valueOf(exchange.getPageSize()))
                    .addParams("sea", String.valueOf(exchange.getSea()))
                    .addParams("cid",exchange.getCid())
                    .addParams("Eid",exchange.getEid())
                    .build()
                    .execute(callback);}
        if(AppStringUtils.isEmpty(exchange.getCid()) && AppStringUtils.isNotEmpty(exchange.getEid())){
            OkHttpUtils.get().url(url)
                    .addParams("pageIndex", String.valueOf(exchange.getPageIndex()))
                    .addParams("pageSize", String.valueOf(exchange.getPageSize()))
                    .addParams("sea", String.valueOf(exchange.getSea()))
                    .addParams("Eid",exchange.getEid())
                    .build()
                    .execute(callback);}
    }

    public static void getExchangeStatus(StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "ExchangeExamines";
        OkHttpUtils.get().url(url)
                .build()
                .execute(callback);
    }

    /**
     * 发布兑换接口
     * @param publishExchanges
     * @param stringCallback
     */
    public static void publishExchanges(PublishExchanges publishExchanges, StringCallback stringCallback){
        String url = TEXT_HUANHUAN_HOST + "Exchanges";
        OkHttpUtils.postString().url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(publishExchanges))
                .build()
                .execute(stringCallback);
    }

    /**
     * 发布合作接口
     * @param publishCoop
     * @param stringCallback
     */
    public static void publishExchanges(PublishCoop publishCoop, StringCallback stringCallback){
        String url = TEXT_HUANHUAN_HOST + "Cooperations";
        OkHttpUtils.postString().url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(publishCoop))
                .build()
                .execute(stringCallback);
    }

    public static void getCooperateList(CoopList coopList, StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "Cooperations";
        OkHttpUtils.get().url(url)
                .addParams("pageIndex", String.valueOf(coopList.getPageIndex()))
                .addParams("pageSize", String.valueOf(coopList.getPageSize()))
                .build()
                .execute(callback);}

    public static void getCoopDetail(String id, String userId, StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "Cooperations/" + id;
        OkHttpUtils.get().url(url)
                .addParams("userId", userId)
                .build()
                .execute(callback);}

    public static void joinCoop(String id,String userId, Callback callback){
        String url = TEXT_HUANHUAN_HOST + "Cooperations/"  + id;
        OkHttpClient client = new OkHttpClient();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(Integer.valueOf(userId));
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(userInfo));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void getCommentList(CommentDTO commentDTO, StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "CooperationReplies";
        OkHttpUtils.get().url(url)
                .addParams("coId", String.valueOf(commentDTO.getExId()))
                .addParams("userId", String.valueOf(commentDTO.getUserId()))
                .addParams("pageIndex", String.valueOf(commentDTO.getPageIndex()))
                .addParams("pageSize", String.valueOf(commentDTO.getPageSize()))
                .build()
                .execute(callback);
    }

    /**
     * 合作发表评论
     * @param exId
     * @param userId
     * @param comment
     * @param ParentId
     * @param callback
     */
    public static void publishCoopComment(int exId,int userId, String comment,int ParentId, StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "CooperationReplies/" + exId;
        if(ParentId == -1){
            CommentSimple commentSimple = new CommentSimple();
            commentSimple.setUserId(userId);
            commentSimple.setComment(comment);
            OkHttpUtils.postString().url(url)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(new Gson().toJson(commentSimple))
                    .build()
                    .execute(callback);
        }else{
            CommentMuti commentMuti = new CommentMuti();
            commentMuti.setUserId(userId);
            commentMuti.setComment(comment);
            commentMuti.setParentId(ParentId);
            OkHttpUtils.postString().url(url)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(new Gson().toJson(commentMuti))
                    .build()
                    .execute(callback);
         }
    }

    public static void goodComments(String id,String userId, Callback callback, Boolean isExchange){
        if(isExchange){
            String url = TEXT_HUANHUAN_HOST + "ExchangeReplies/"  + id + "?userId=" + userId;
            OkHttpClient client = new OkHttpClient();
            //UserInfo userInfo = new UserInfo();
            //userInfo.setUserId(Integer.valueOf(userId));
            RequestBody body = RequestBody.create(JSON, new Gson().toJson(null));
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(callback);
        }else{
            String url = TEXT_HUANHUAN_HOST + "CooperationReplies/"  + id + "?userId=" + userId;
            OkHttpClient client = new OkHttpClient();
            //UserInfo userInfo = new UserInfo();
            //userInfo.setUserId(Integer.valueOf(userId));
            RequestBody body = RequestBody.create(JSON, new Gson().toJson(null));
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(callback);
        }
    }

    public static void getExchangeDetail(String id, String userId, StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "Exchanges/" + id;
        OkHttpUtils.get().url(url)
                .addParams("userId", userId)
                .build()
                .execute(callback);}

    public static void attentionExchange(String id,String userId, Callback callback){
        String url = TEXT_HUANHUAN_HOST + "Exchanges/"  + id;
        OkHttpClient client = new OkHttpClient();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(Integer.valueOf(userId));
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(userInfo));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void getExchangeCommentList(CommentDTO commentDTO, StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "ExchangeReplies";
        OkHttpUtils.get().url(url)
                .addParams("exId", String.valueOf(commentDTO.getExId()))
                .addParams("userId", String.valueOf(commentDTO.getUserId()))
                .addParams("pageIndex", String.valueOf(commentDTO.getPageIndex()))
                .addParams("pageSize", String.valueOf(commentDTO.getPageSize()))
                .build()
                .execute(callback);
    }

    /**
     * 兑换发表评论
     * @param exId
     * @param userId
     * @param comment
     * @param ParentId
     * @param callback
     */
    public static void publishExchangeComment(int exId,int userId, String comment,int ParentId, StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "ExchangeReplies/" + exId;
        if(ParentId == -1){
            CommentSimple commentSimple = new CommentSimple();
            commentSimple.setUserId(userId);
            commentSimple.setComment(comment);
            OkHttpUtils.postString().url(url)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(new Gson().toJson(commentSimple))
                    .build()
                    .execute(callback);
        }else{
            CommentMuti commentMuti = new CommentMuti();
            commentMuti.setUserId(userId);
            commentMuti.setComment(comment);
            commentMuti.setParentId(ParentId);
            OkHttpUtils.postString().url(url)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(new Gson().toJson(commentMuti))
                    .build()
                    .execute(callback);
        }
    }

    /**
     * 获取确认兑换的信息
     * @param Id
     * @param userId
     * @param examine
     * @param callback
     */
    public static void getConfirmDetail(String Id, String userId, String examine, StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "Exchanges/Confirm/" + Id;
        OkHttpUtils.get().url(url)
                .addParams("userId", userId)
                .addParams("examine", examine)
                .build()
                .execute(callback);
    }

    /**
     * 获取用户信息
     * @param id
     * @param callback
     */
    public static void getMembers(String id, StringCallback callback){
        String url = TEXT_HUANHUAN_HOST + "Members/" + id;
        OkHttpUtils.get().url(url)
                .build()
                .execute(callback);
    }

    public static void addNewAddress(AddressDTO addressDTO, StringCallback stringCallback){
        String url = TEXT_HUANHUAN_HOST + "Addresses";
        OkHttpUtils.postString().url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(addressDTO))
                .build()
                .execute(stringCallback);}
}
