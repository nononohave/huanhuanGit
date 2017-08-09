package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppACache;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ResetActivityThird extends BaseActivity implements View.OnClickListener{

    private EditText et_reset3_newPassword,et_reset3_confirmPassword;

    private LinearLayout ll_reset3_clearPassword,ll_reset3_clearConfirmPassword;

    private ImageView iv_reset3_clearPassword,iv_reset3_clearConfirmPassword;

    private Button btn_reset3_finish;

    private AppManager appManager;

    private CharSequence phoneTextChar,confirmTextChar;

    private Boolean isPhoneEdit = false,isConfirmEdit = false;

    private String verifyCode,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyCode = getIntent().getExtras().getString("verifyCode");
        phone = getIntent().getExtras().getString("phone");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        setTitleBarColor(R.color.white);
        setLeftImageResource(R.mipmap.nav_back);
        setDividerColor(R.color.dividLineColor);
        setRightTextColor(R.color.titleBarTextColor);
        setTitleTextColor(R.color.titleBarTextColor);
        setTitle(this.getResources().getString(R.string.resetPassword3));
        setBaseContentView(R.layout.activity_reset_third);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        initViews();

        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
    }

    private void initViews() {

        et_reset3_newPassword = (EditText) findViewById(R.id.et_reset3_newPassword);
        et_reset3_confirmPassword = (EditText) findViewById(R.id.et_reset3_confirmPassword);

        ll_reset3_clearPassword = (LinearLayout) findViewById(R.id.ll_reset3_clearPassword);
        ll_reset3_clearConfirmPassword = (LinearLayout) findViewById(R.id.ll_reset3_clearConfirmPassword);

        iv_reset3_clearPassword = (ImageView) findViewById(R.id.iv_reset3_clearPassword);
        iv_reset3_clearConfirmPassword = (ImageView) findViewById(R.id.iv_reset3_clearConfirmPassword);

        btn_reset3_finish = (Button) findViewById(R.id.btn_reset3_finish);

        ll_reset3_clearPassword.setOnClickListener(this);
        ll_reset3_clearConfirmPassword.setOnClickListener(this);
        iv_reset3_clearConfirmPassword.setOnClickListener(this);
        btn_reset3_finish.setOnClickListener(this);

        et_reset3_newPassword.addTextChangedListener(new TextWatcher() {
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
                    iv_reset3_clearPassword.setVisibility(View.VISIBLE);
                    if(isConfirmEdit) {
                        btn_reset3_finish.setBackgroundResource(R.drawable.shape_corner);
                    }
                    isPhoneEdit = true;
                }else{
                    btn_reset3_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                    isPhoneEdit = false;
                }
            }
        });

        et_reset3_confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmTextChar = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(confirmTextChar.length() > 0){
                    iv_reset3_clearConfirmPassword.setVisibility(View.VISIBLE);
                    if(isPhoneEdit) {
                        btn_reset3_finish.setBackgroundResource(R.drawable.shape_corner);
                    }
                    isConfirmEdit = true;
                }else{
                    btn_reset3_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                    isConfirmEdit = false;
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_reset3_clearPassword:
                if(isPhoneEdit){
                    et_reset3_newPassword.setText("");
                    iv_reset3_clearPassword.setVisibility(View.GONE);
                    isPhoneEdit = false;
                    btn_reset3_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                }
                break;
            case R.id.ll_reset3_clearConfirmPassword:
                if(isConfirmEdit){
                    et_reset3_confirmPassword.setText("");
                    iv_reset3_clearConfirmPassword.setVisibility(View.GONE);
                    isConfirmEdit = false;
                    btn_reset3_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                }
                break;
            case R.id.btn_reset3_finish:
                final String password = et_reset3_newPassword.getText().toString();
                String confirmPass = et_reset3_confirmPassword.getText().toString();
                if(password.equals(confirmPass)){
                    if(isConfirmEdit && isPhoneEdit) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    HttpRequest.resetPassword(password, verifyCode, phone, new Callback() {
                                        @Override
                                        public void onFailure(Request request, IOException e) {
                                            AppToastMgr.shortToast(ResetActivityThird.this,"请求失败！");
                                        }

                                        @Override
                                        public void onResponse(final Response response) throws IOException {
                                                    try {
                                                        if (null != response.cacheResponse()) {
                                                            String str = response.cacheResponse().toString();
                                                            Log.i("wangshu1", "cache---" + str);
                                                        } else {
                                                            try {
                                                                String str1 = response.body().string();
                                                                Log.i("地方撒阿达啥打法是否", "1111111111111111111111111---" + str1);
                                                                JSONObject jsonObject = new JSONObject(str1);
                                                                Boolean success = jsonObject.getBoolean("success");
                                                                if(success){
                                                                    AppACache appACache = AppACache.get(ResetActivityThird.this);
                                                                    appACache.put("phone",phone);
                                                                    Intent intent = new Intent(ResetActivityThird.this, LoginActivity.class);
                                                                    startActivity(intent);
                                                                }else{
                                                                    String errorMsg = jsonObject.getString("errorMsg");
                                                                    AppToastMgr.shortToast(ResetActivityThird.this,"修改失败！原因：" + errorMsg);
                                                                }
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            String str = response.networkResponse().toString();
                                                            Log.i("wangshu3", "network---" + str);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else{
                        AppToastMgr.shortToast(ResetActivityThird.this,"请输入重设密码");
                    }
                }else{
                    AppToastMgr.shortToast(ResetActivityThird.this,"两次输入的密码不一致");
                }
                break;
        }
    }

    Runnable resetPassRun = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
        }
    };
}
