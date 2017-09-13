package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.BindPhone;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.AppValidationMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class BindPhoneActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private EditText et_bind_phone;
    private LinearLayout ll_clearPhone;
    private ImageView iv_clearPhone;
    private EditText et_bind_code;
    private Button btn_bind_phone_finish;
    private TextView tv_bind_getCode;
    //倒计时
    private int recLen = 10;

    private Timer timer;
    private TimerTask timerTask;
    private CharSequence phoneChar;
    private CharSequence verifyChar;
    private boolean isPhoneEdit = false;
    private boolean isVerifyEdit = false;
    private String userId;
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
        setBaseContentView(R.layout.activity_bind_phone);
        setTitle(this.getResources().getString(R.string.bind_phone));
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        userId = getUserId();
        initView();
    }

    private void initView() {
        et_bind_phone = (EditText) findViewById(R.id.et_bind_phone);
        et_bind_code = (EditText) findViewById(R.id.et_bind_code);
        ll_clearPhone = (LinearLayout) findViewById(R.id.ll_clearPhone);
        iv_clearPhone = (ImageView) findViewById(R.id.iv_clearPhone);
        btn_bind_phone_finish = (Button) findViewById(R.id.btn_bind_phone_finish);
        tv_bind_getCode = (TextView) findViewById(R.id.tv_bind_getCode);

        btn_bind_phone_finish.setOnClickListener(this);
        tv_bind_getCode.setOnClickListener(this);
        ll_clearPhone.setOnClickListener(this);
        et_bind_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneChar = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(phoneChar.length() > 0){
                    if(isVerifyEdit) {
                        btn_bind_phone_finish.setBackgroundResource(R.drawable.shape_corner);
                    }
                    isPhoneEdit = true;
                    iv_clearPhone.setVisibility(View.VISIBLE);
                }else{
                    btn_bind_phone_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                    isPhoneEdit = false;
                    iv_clearPhone.setVisibility(View.GONE);
                }
            }
        });
        et_bind_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verifyChar = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(verifyChar.length() > 0){
                    if(isPhoneEdit) {
                        btn_bind_phone_finish.setBackgroundResource(R.drawable.shape_corner);
                    }
                    isVerifyEdit = true;
                }else{
                    btn_bind_phone_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                    isVerifyEdit = false;
                }
            }
        });
    }
    private void initTimer() {
        recLen = 10;
        timer = new Timer();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                handler.sendEmptyMessage(0000);
            }
        };
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            tv_bind_getCode.setText(recLen + "s");
            recLen--;
            if (recLen < 0) {
                tv_bind_getCode.setEnabled(true);
                tv_bind_getCode.setText("重新获取");
                clearTimer();
            }
        };
    };

    private void clearTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_bind_phone_finish:
                String phoneFinsh = et_bind_phone.getText().toString();
                String verifyFinish = et_bind_code.getText().toString();
                if(AppStringUtils.isNotEmpty(phoneFinsh)){
                    if(AppValidationMgr.isPhone(phoneFinsh)){
                        if(AppStringUtils.isNotEmpty(verifyFinish)){
                            BindPhone bindPhone = new BindPhone();
                            bindPhone.setId(Integer.valueOf(userId));
                            bindPhone.setVerifyCode(verifyFinish);
                            bindPhone.setPhone(phoneFinsh);
                            HttpRequest.changeBindPhone(bindPhone, new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    toastErrorMsg(BindPhoneActivity.this, "请求失败！");
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    try {
                                        if (null != response.cacheResponse()) {
                                        } else {
                                            try {
                                                String str1 = response.body().string();
                                                JSONObject jsonObject = new JSONObject(str1);
                                                Boolean success = jsonObject.getBoolean("success");
                                                if (success) {
                                                    toastErrorMsg(BindPhoneActivity.this,"修改成功！");
                                                    appManager.finishActivity(ExchangePhoneOrAlipayActivity.exchangeInstance);
                                                    appManager.finishActivity();
                                                } else {
                                                    String errorMsg = jsonObject.getString("errorMsg");
                                                    toastErrorMsg(BindPhoneActivity.this,errorMsg);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }else{
                            toastErrorMsg(BindPhoneActivity.this,"请输入验证码");
                        }
                    }else{
                        toastErrorMsg(BindPhoneActivity.this,"手机号码有误");
                    }
                }else{
                    toastErrorMsg(BindPhoneActivity.this,"请输入手机号");
                }
                break;
            case R.id.ll_clearPhone:
                et_bind_phone.setText("");
                isPhoneEdit = false;
                btn_bind_phone_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                iv_clearPhone.setVisibility(View.GONE);
                break;
            case R.id.tv_bind_getCode:
                final String phone = et_bind_phone.getText().toString();
                if(AppStringUtils.isNotEmpty(phone)){
                    if(AppValidationMgr.isPhone(phone)){
                        try {
                            HttpRequest.loginSendMsgCode(phone,new StringCallback(){
                                @Override
                                public void onError(Request request, Exception e)
                                {
                                    toastErrorMsg(BindPhoneActivity.this,"请求失败！");
                                }

                                @Override
                                public void onResponse(String response)
                                {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        Boolean success = jsonObject.getBoolean("success");
                                        String errMsg = jsonObject.getString("errorMsg");
                                        if(success){
                                            initTimer();
                                            timer.schedule(timerTask, 0, 1000);
                                            tv_bind_getCode.setEnabled(false);
                                            et_bind_code.requestFocus();
                                            toastErrorMsg(BindPhoneActivity.this,"已发送验证码至手机号");
//                                            JSONObject returnObj = jsonObject.getJSONObject("data");
//                                            String returnVerifyCode = returnObj.getString("verifyCode");
//                                            String returnPhone = returnObj.getString("phone");
                                        }else{
                                            toastErrorMsg(BindPhoneActivity.this,"请求失败！原因：" + errMsg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
//
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        toastErrorMsg(BindPhoneActivity.this,"手机号码有误");
                    }
                }else{
                    toastErrorMsg(BindPhoneActivity.this,"请输入手机号");
                }
                break;
        }
    }
}
