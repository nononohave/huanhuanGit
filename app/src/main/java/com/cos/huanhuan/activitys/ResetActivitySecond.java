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

import java.util.Timer;
import java.util.TimerTask;

public class ResetActivitySecond extends BaseActivity implements View.OnClickListener{

    private TextView tv_reset2_sendPhone,tv_reset2_getCode;

    private EditText et_reset2_code;

    private Button btn_reset2_next;

    private AppManager appManager;

    //是否点击了编辑框
    private boolean isVerifyEdit = false;
    private CharSequence verifyTextChar="";

    private String phone = "";

    //倒计时
    private int recLen = 10;

    private Timer timer;
    private TimerTask timerTask;

    private String returnVerifyCode,intentPhone,returnPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        returnVerifyCode = getIntent().getExtras().getString("returnVerifyCode");
        intentPhone = getIntent().getExtras().getString("intentPhone");
        returnPhone = getIntent().getExtras().getString("returnPhone");

        //从前一个界面获取手机号到这里


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        setTitleBarColor(R.color.white);
        setLeftImageResource(R.mipmap.nav_back);
        setDividerColor(R.color.dividLineColor);
        setRightTextColor(R.color.titleBarTextColor);
        setTitleTextColor(R.color.titleBarTextColor);
        setTitle(this.getResources().getString(R.string.resetPassword2));
        setBaseContentView(R.layout.activity_reset_second);
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

        tv_reset2_sendPhone = (TextView) findViewById(R.id.tv_reset2_sendPhone);
        tv_reset2_getCode = (TextView) findViewById(R.id.tv_reset2_getCode);

        et_reset2_code = (EditText) findViewById(R.id.et_reset2_code);

        btn_reset2_next = (Button) findViewById(R.id.btn_reset2_next);

        tv_reset2_getCode.setOnClickListener(this);
        btn_reset2_next.setOnClickListener(this);

        //设置传入的手机号
        tv_reset2_sendPhone.setText("+86" + intentPhone);

        initTimer();
        timer.schedule(timerTask, 0, 1000);

        //手机号文本框监听事件
        et_reset2_code.addTextChangedListener(new TextWatcher() {
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
                    btn_reset2_next.setBackgroundResource(R.drawable.shape_corner);
                    isVerifyEdit = true;
                }else{
                    btn_reset2_next.setBackgroundResource(R.drawable.shape_corner_dark);
                    isVerifyEdit = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_reset2_getCode:
                initTimer();
                timer.schedule(timerTask, 0, 1000);
                tv_reset2_getCode.setEnabled(false);
                break;
            case R.id.btn_reset2_next:
                //点击完成校验验证码
                String verifyCode = et_reset2_code.getText().toString();
                if(AppStringUtils.isNotEmpty(verifyCode)){
                    if(verifyCode.equals(returnVerifyCode)) {
                        Intent intent = new Intent(ResetActivitySecond.this, ResetActivityThird.class);
                        intent.putExtra("verifyCode",verifyCode);
                        intent.putExtra("phone",intentPhone);
                        startActivity(intent);
                    }else{
                        toastErrorMsg(ResetActivitySecond.this, " 验证码有误！");
                    }
                }else{
                    toastErrorMsg(ResetActivitySecond.this, " 请输入验证码！");
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
            tv_reset2_getCode.setText(recLen + "s");
            recLen--;
            if (recLen < 0) {
                tv_reset2_getCode.setEnabled(true);
                tv_reset2_getCode.setText("重新获取");
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
