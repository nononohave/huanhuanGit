package com.cos.huanhuan.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cos.huanhuan.MainActivity;
import com.cos.huanhuan.R;
import com.cos.huanhuan.fragments.MessageFragment;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppACache;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppNetworkMgr;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.AppValidationMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.TitleBar;
import com.squareup.okhttp.Request;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.NativeObject;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = MessageFragment.class.getSimpleName();
    private EditText et_phone,et_password;
    private ImageView iv_clearPhone,iv_showPassword,iv_wxLogin,iv_wbLogin,iv_qqLogin;
    private Button btn_login;
    private TextView tv_forgetPassword;
    private LinearLayout ll_clearPhone,ll_showPassword;

    private AppManager appManager;

    //是否点击了编辑框
    private boolean isPhoneEdit = false;
    private boolean isPassEdit = false;
    //密码是否显示
    private boolean isShow=true;

    private CharSequence phoneTextChar="";
    private CharSequence passTextChar="";

    private UMShareAPI mShareAPI;

    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        setTitleBarColor(R.color.white);
        setLeftImageResource(R.mipmap.nav_back);
        setDividerColor(R.color.dividLineColor);
        setRightTextColor(R.color.titleBarTextColor);
        setTitleTextColor(R.color.titleBarTextColor);
        setTitle(this.getResources().getString(R.string.login));
        setBaseContentView(R.layout.activity_login);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);

        //存储类
        sharedPreferencesHelper = new SharedPreferencesHelper(LoginActivity.this);
        mShareAPI = UMShareAPI.get(this);

        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        setRightButton(new TitleBar.TextAction(this.getResources().getString(R.string.register)) {
            @Override
            public void performAction(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
         });

//        int netWorkState = AppNetworkMgr.getNetworkState(LoginActivity.this);
//        if(netWorkState == 0){
//            setNoNetWOrkLayout();
//        }else{
//            initView();
//        }
        initView();
        //setNoData();
    }

    private void initView() {

        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);

        btn_login = (Button) findViewById(R.id.btn_login);

        iv_clearPhone = (ImageView) findViewById(R.id.iv_clearPhone);
        iv_showPassword = (ImageView) findViewById(R.id.iv_showPassword);
        iv_wxLogin = (ImageView) findViewById(R.id.iv_wxLogin);
        iv_wbLogin = (ImageView) findViewById(R.id.iv_wbLogin);
        iv_qqLogin = (ImageView) findViewById(R.id.iv_qqLogin);

        ll_clearPhone = (LinearLayout) findViewById(R.id.ll_clearPhone);
        ll_showPassword = (LinearLayout) findViewById(R.id.ll_showPassword);

        tv_forgetPassword = (TextView) findViewById(R.id.tv_forgetPassword);

        tv_forgetPassword.setOnClickListener(this);
        ll_clearPhone.setOnClickListener(this);
        ll_showPassword.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        iv_qqLogin.setOnClickListener(this);
        iv_wxLogin.setOnClickListener(this);
        iv_wbLogin.setOnClickListener(this);
        iv_qqLogin.setOnClickListener(this);
        //填充手机号
        if(sharedPreferencesHelper.get("phoneItem","") != null) {
            String phoneSp = (String) sharedPreferencesHelper.get("phoneItem", "");
            if (AppStringUtils.isNotEmpty(phoneSp)) {
                et_phone.setText(phoneSp);
                isPhoneEdit = true;
                et_phone.setSelection(phoneSp.length());
            }
        }
        //手机号文本框监听事件
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneTextChar = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(phoneTextChar.length() > 0){
                    iv_clearPhone.setVisibility(View.VISIBLE);
                    iv_clearPhone.setImageResource(R.mipmap.close_dark);
                    isPhoneEdit = true;
                    if(isPassEdit){
                        btn_login.setBackgroundResource(R.drawable.shape_corner);
                    }
                }else{
                    iv_clearPhone.setVisibility(View.GONE);
                    isPhoneEdit = false;
                    btn_login.setBackgroundResource(R.drawable.shape_corner_dark);
                }
            }
        });

        //密码文本框监听事件
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passTextChar = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(passTextChar.length() > 0){
                    isPassEdit = true;
                    if(isPhoneEdit){
                        btn_login.setBackgroundResource(R.drawable.shape_corner);
                    }
                }else{
                    isPassEdit = false;
                    btn_login.setBackgroundResource(R.drawable.shape_corner_dark);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_clearPhone:
                //清除文本框
                if(isPhoneEdit){
                    et_phone.setText("");
                    iv_clearPhone.setVisibility(View.GONE);
                    isPhoneEdit = false;
                    btn_login.setBackgroundResource(R.drawable.shape_corner_dark);
                }
                break;
            case R.id.ll_showPassword:
                //展示密码与否
                if(isShow){
                    iv_showPassword.setImageResource(R.mipmap.key_light);
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_password.setSelection(et_password.getText().length());
                    isShow = false;
                }else{
                    iv_showPassword.setImageResource(R.mipmap.key_dark);
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_password.setSelection(et_password.getText().length());
                    isShow = true;
                }
                break;
            case R.id.btn_login:
                final String phone = et_phone.getText().toString();
                final String password = et_password.getText().toString();
                if(AppStringUtils.isNotEmpty(phone)) {
                    if(AppStringUtils.isNotEmpty(password)) {
                        if (AppValidationMgr.isPhone(phone)) {
                            HttpRequest.login(phone, "phone", password, new StringCallback() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    toastErrorMsg(LoginActivity.this,"请求失败！");
                                }

                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        Boolean success = jsonObject.getBoolean("success");
                                        String errorMsg = jsonObject.getString("errorMsg");
                                        if(success){
                                            JSONObject obj = jsonObject.getJSONObject("data");
                                            UserValueData userValueData = JsonUtils.fromJson(obj.toString(),UserValueData.class);
                                            sharedPreferencesHelper.saveObject("userData",userValueData);
                                            sharedPreferencesHelper.put("phoneItem",phone);//记录手机号，下次登录自动填充
                                            if(userValueData.getRongToken() != null && AppStringUtils.isNotEmpty(userValueData.getRongToken())){
                                                reconnect(userValueData.getRongToken());
                                            }else{
                                                regetToken(userValueData);
                                            }
                                            Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
                                            startActivity(intent);
                                            appManager.finishActivity();
                                        }else{
                                            toastErrorMsg(LoginActivity.this,errorMsg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        } else {
                            toastErrorMsg(LoginActivity.this, " 手机号有误！");
                        }
                    }else{
                        toastErrorMsg(LoginActivity.this, " 请输入密码!");
                    }
                }else{
                    AppToastMgr.longToast(LoginActivity.this, " 请输入手机号!");
                }
                break;
            case R.id.iv_wxLogin:
                //微信授权登录
                mShareAPI.doOauthVerify(LoginActivity.this, SHARE_MEDIA.WEIXIN, verifyAuthListener);
                break;
            case R.id.iv_wbLogin:
                //微博授权登录
                mShareAPI.doOauthVerify(LoginActivity.this, SHARE_MEDIA.SINA, verifyAuthListener);
                break;
            case R.id.iv_qqLogin:
                //qq授权登录
                mShareAPI.doOauthVerify(LoginActivity.this, SHARE_MEDIA.QQ, verifyAuthListener);
                break;
            case R.id.tv_forgetPassword:
                Intent intent = new Intent(LoginActivity.this, ResetActivityFirst.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 重新获取token
     * @param userValueData
     */
    private void regetToken(final UserValueData userValueData) {
        HttpRequest.reGetToken(String.valueOf(userValueData.getId()), userValueData.getNickname(), new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                toastErrorMsg(LoginActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success){
                        String token = jsonObject.getString("data");
                        userValueData.setRongToken(token);
                        sharedPreferencesHelper.saveObject("userData",userValueData);
                        reconnect(token);
                    }else{
                        toastErrorMsg(LoginActivity.this, " 登录失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //注意先需要调用一下获取doOauthVerify进行授权然后再掉获取用户资料的方法
    private UMAuthListener verifyAuthListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if(platform == SHARE_MEDIA.QQ){
                UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, authListener);
            }else if(platform == SHARE_MEDIA.SINA){
                UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, authListener);
            }else {
                UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, authListener);
            }
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            toastErrorMsg(LoginActivity.this,"取消授权");
        }
    };

    //注意先需要调用一下获取doOauthVerify进行授权然后再掉获取用户资料的方法
    private UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            String uid = data.get("uid");
            String type = "";
            String nickName = "";
            String gender = "";
            String figureUrl = "";
            if(platform == SHARE_MEDIA.QQ){
                type = "QQ";
                nickName = data.get("name");
                gender = data.get("gender");
                figureUrl = data.get("iconurl");
            }else if(platform == SHARE_MEDIA.SINA){
                type = "sina";
                nickName = data.get("name");
                gender = data.get("gender");
                figureUrl = data.get("iconurl");
            }else if(platform == SHARE_MEDIA.WEIXIN){
                type = "wechat";
                nickName = data.get("name");
                gender = data.get("gender");
                figureUrl = data.get("iconurl");
            }
            HttpRequest.oauthLogin(uid, type,nickName,gender, figureUrl, new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    toastErrorMsg(LoginActivity.this,"请求失败！");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        String errorMsg = jsonObject.getString("errorMsg");
                        if(success){
                            JSONObject obj = jsonObject.getJSONObject("data");
                            Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
                            startActivity(intent);
                            final UserValueData userValueData = JsonUtils.fromJson(obj.toString(),UserValueData.class);
                            sharedPreferencesHelper.saveObject("userData",userValueData);
                            reconnect(userValueData.getRongToken());
                            RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                                @Override
                                public UserInfo getUserInfo(String s) {
                                    //, String name, Uri portraitUri
                                    UserInfo userInfo = new UserInfo(userValueData.getNickname(),String.valueOf(userValueData.getId()), Uri.parse(userValueData.getPortrait()));
                                    return userInfo;
                                }
                            },true);
                        }else{
                            toastErrorMsg(LoginActivity.this, " 登录失败！原因：" + errorMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            toastErrorMsg(LoginActivity.this,"取消授权");
        }
    };
    private void reconnect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
            }

            @Override
            public void onSuccess(String s) {
                RongIM.getInstance().setConversationToTop(Conversation.ConversationType.PRIVATE, "34", true, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                Log.e(TAG, "---onError--" + e);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
