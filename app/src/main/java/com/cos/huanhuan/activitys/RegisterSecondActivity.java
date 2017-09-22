package com.cos.huanhuan.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppACache;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.AppValidationMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.ViewUtils;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class RegisterSecondActivity extends BaseActivity implements View.OnClickListener{

    private String returnVerifyCode,returnPhone,phonePre,password;

    private TextView tv_register2_sendPhone,iv_register2_getCode;
    private EditText et_register2_code;
    private Button btn_register2_finish;
    private AppManager appManager;

    private CharSequence verifyTextChar="";

    //是否点击了编辑框
    private boolean isVerifyEdit = false;

    //倒计时
    private int recLen = 60;

    private Timer timer;
    private TimerTask timerTask;


    private Dialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        phonePre = this.getIntent().getExtras().getString("phone");
        password = this.getIntent().getExtras().getString("password");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        setTitleBarColor(R.color.white);
        setLeftImageResource(R.mipmap.nav_back);
        setDividerColor(R.color.dividLineColor);
        setTitleTextColor(R.color.titleBarTextColor);
        setTitle(this.getResources().getString(R.string.register2Title));
        setBaseContentView(R.layout.activity_register_second);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });

        initViews();
        initData();
    }

    private void initData() {
        try {
            HttpRequest.loginSendMsgCode(phonePre,new StringCallback(){
                @Override
                public void onError(Request request, Exception e)
                {
                    toastErrorMsg(RegisterSecondActivity.this,"请求失败！");
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
                            JSONObject returnObj = jsonObject.getJSONObject("data");
                            returnVerifyCode = returnObj.getString("verifyCode");
                            returnPhone = returnObj.getString("phone");
                        }else{
                            toastErrorMsg(RegisterSecondActivity.this,"请求失败！原因：" + errMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {

        tv_register2_sendPhone = (TextView) findViewById(R.id.tv_register2_sendPhone);
        iv_register2_getCode = (TextView) findViewById(R.id.iv_register2_getCode);

        et_register2_code = (EditText) findViewById(R.id.et_register2_code);

        btn_register2_finish = (Button) findViewById(R.id.btn_register2_finish);

        iv_register2_getCode.setOnClickListener(this);
        btn_register2_finish.setOnClickListener(this);

        tv_register2_sendPhone.setText("+86" + phonePre);

        //手机号文本框监听事件
        et_register2_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verifyTextChar = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(verifyTextChar.length() > 0){
                    btn_register2_finish.setBackgroundResource(R.drawable.shape_corner);
                    isVerifyEdit = true;
                }else{
                    btn_register2_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                    isVerifyEdit = false;
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_register2_getCode:
                initTimer();
                timer.schedule(timerTask, 0, 1000);
                iv_register2_getCode.setEnabled(false);
                break;
            case R.id.btn_register2_finish:
                //点击完成校验验证码
                dialogLoading = ViewUtils.createLoadingDialog(RegisterSecondActivity.this);
                String verifyText = et_register2_code.getText().toString();
                if(AppStringUtils.isNotEmpty(verifyText)){
                    if(returnVerifyCode.equals(verifyText)){
//                        String UserName,String Type,String Password,String VerifyCode, StringCallback
//                        stringCallback
                        try {
                            dialogLoading.show();
                            HttpRequest.register(phonePre, "phone", password, verifyText, new StringCallback() {
                                @Override
                                public void onError(Request request, Exception e) {
                                   toastErrorMsg(RegisterSecondActivity.this,"请求失败！");
                                    dialogLoading.dismiss();
                                }

                                @Override
                                public void onResponse(String response) {
                                    //{"success":"true","error":200,"errorMsg":null}
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        Boolean success = jsonObject.getBoolean("success");
                                        if(success){
                                            dialogLoading.dismiss();
                                           toastErrorMsg(RegisterSecondActivity.this,"注册成功！");
                                            Intent intent = new Intent(RegisterSecondActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                            AppACache appACache = AppACache.get(RegisterSecondActivity.this);
                                            appACache.put("phone",phonePre);
                                        }else{
                                            String errorMsg = jsonObject.getString("errorMsg");
                                           toastErrorMsg(RegisterSecondActivity.this,"注册失败！原因：" + errorMsg);
                                            dialogLoading.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        dialogLoading.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            dialogLoading.show();
                            e.printStackTrace();
                        }
                    }else{
                       toastErrorMsg(RegisterSecondActivity.this,"验证码有误");
                    }
                }else{
                   toastErrorMsg(RegisterSecondActivity.this,"请输入验证码");
                }
                break;
        }
    }

    private void initTimer() {
        recLen = 60;
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
            iv_register2_getCode.setText(recLen + "s");
            recLen--;
            if (recLen < 0) {
                iv_register2_getCode.setEnabled(true);
                iv_register2_getCode.setText("重新获取");
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
}
