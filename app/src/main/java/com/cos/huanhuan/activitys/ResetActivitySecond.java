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

public class ResetActivitySecond extends BaseActivity implements View.OnClickListener{

    private TextView tv_reset2_sendPhone,tv_reset2_getCode;

    private EditText et_reset2_code;

    private Button btn_reset2_next;

    private AppManager appManager;

    //是否点击了编辑框
    private boolean isVerifyEdit = false;
    private CharSequence verifyTextChar="";

    private String phone = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //从前一个界面获取手机号到这里

        phone = this.getIntent().getExtras().getString("phone");

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
        tv_reset2_sendPhone.setText("+86" + phone);

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
                String phone = et_reset2_code.getText().toString();
                if(isVerifyEdit){
                    AppToastMgr.shortToast(ResetActivitySecond.this, " 重新获取验证码！");
                }else{
                    AppToastMgr.shortToast(ResetActivitySecond.this, " 请输入验证码！");
                }
                break;
            case R.id.btn_reset2_next:
                //点击完成校验验证码
                if(isVerifyEdit){
                    Intent intent = new Intent(ResetActivitySecond.this,ResetActivityThird.class);
                    startActivity(intent);
                }else{
                    AppToastMgr.shortToast(ResetActivitySecond.this, " 请输入验证码！");
                }
                break;
        }
    }
}
