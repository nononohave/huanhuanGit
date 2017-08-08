package com.cos.huanhuan.activitys;

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
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.AppValidationMgr;
import com.cos.huanhuan.utils.HttpRequest;
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
    private int recLen = 10;

    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        returnVerifyCode = this.getIntent().getExtras().getString("returnVerifyCode");
        returnPhone = this.getIntent().getExtras().getString("returnPhone");
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
    }

    private void initViews() {

        tv_register2_sendPhone = (TextView) findViewById(R.id.tv_register2_sendPhone);
        iv_register2_getCode = (TextView) findViewById(R.id.iv_register2_getCode);

        et_register2_code = (EditText) findViewById(R.id.et_register2_code);

        btn_register2_finish = (Button) findViewById(R.id.btn_register2_finish);

        iv_register2_getCode.setOnClickListener(this);
        btn_register2_finish.setOnClickListener(this);

        tv_register2_sendPhone.setText("+86" + phonePre);

        initTimer();
        timer.schedule(timerTask, 0, 1000);

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
                String phone = et_register2_code.getText().toString();
                initTimer();
                timer.schedule(timerTask, 0, 1000);
                break;
            case R.id.btn_register2_finish:
                //点击完成校验验证码
                String verifyText = et_register2_code.getText().toString();
                if(AppStringUtils.isNotEmpty(verifyText)){
                    if(returnVerifyCode.equals(verifyText)){
//                        String UserName,String Type,String Password,String VerifyCode, StringCallback
//                        stringCallback
                        try {
                            HttpRequest.register(phonePre, "phone", password, verifyText, new StringCallback() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    AppToastMgr.shortToast(RegisterSecondActivity.this,"请求失败！");
                                }

                                @Override
                                public void onResponse(String response) {
                                    //{"success":"true","error":200,"errorMsg":null}
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        Boolean success = jsonObject.getBoolean("success");
                                        if(success){
                                            AppToastMgr.shortToast(RegisterSecondActivity.this,"注册成功！");
                                        }else{
                                            String errorMsg = jsonObject.getString("errorMsg");
                                            AppToastMgr.shortToast(RegisterSecondActivity.this,"注册失败！原因：" + errorMsg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(RegisterSecondActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }else{
                        AppToastMgr.shortToast(RegisterSecondActivity.this,"验证码有误");
                    }
                }else{
                    AppToastMgr.shortToast(RegisterSecondActivity.this,"请输入验证码");
                }
                break;
        }
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

    class MyTask extends TimerTask{

        @Override
        public void run() {
            runOnUiThread(new Runnable() {   // UI thread
                @Override
                public void run() {
                    recLen--;
                    iv_register2_getCode.setText(recLen + "s");
                    if(recLen < 0){
                        if (timer != null){
                            timer.cancel();  //将原任务从队列中移除
                        }
                        iv_register2_getCode.setEnabled(true);
                        iv_register2_getCode.setText("重新获取");
                    }
                }
            });
        }
    }
}
