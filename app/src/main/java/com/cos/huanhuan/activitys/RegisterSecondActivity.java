package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
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
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.AppValidationMgr;

import java.util.Timer;
import java.util.TimerTask;

public class RegisterSecondActivity extends BaseActivity implements View.OnClickListener{

    private String returnVerifyCode,returnPhone,phone,password;

    private TextView tv_register2_sendPhone,iv_register2_getCode;
    private EditText et_register2_code;
    private Button btn_register2_finish;
    private AppManager appManager;

    private CharSequence verifyTextChar="";

    //是否点击了编辑框
    private boolean isVerifyEdit = false;

    //倒计时
    private int recLen = 10;
    private Timer timer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        returnVerifyCode = this.getIntent().getExtras().getString("returnVerifyCode");
        returnPhone = this.getIntent().getExtras().getString("returnPhone");
        phone = this.getIntent().getExtras().getString("phone");
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

        tv_register2_sendPhone.setText("+86" + phone);

        timer.schedule(task, 1000, 1000);    // timeTask
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

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {   // UI thread
                @Override
                public void run() {
                    recLen--;
                    tv_register2_sendPhone.setText(recLen + "s");
                    if(recLen < 0){
                        timer.cancel();
                        tv_register2_sendPhone.setText("重新获取");
                    }
                }
            });
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_register2_getCode:
                String phone = et_register2_code.getText().toString();
                if(isVerifyEdit){
                    timer.schedule(task, 1000, 1000);    // timeTask
                }else{
                    AppToastMgr.shortToast(RegisterSecondActivity.this, " 请输入验证码！");
                }
                break;
            case R.id.btn_register2_finish:
                //点击完成校验验证码
                Intent intent = new Intent(RegisterSecondActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
