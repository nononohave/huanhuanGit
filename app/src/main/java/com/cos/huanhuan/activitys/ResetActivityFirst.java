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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.AppValidationMgr;

public class ResetActivityFirst extends  BaseActivity implements View.OnClickListener{

    private EditText et_reset_phone;

    private LinearLayout ll_reset_clearPhone;

    private ImageView iv_reset_clearPhone;

    private Button btn_reset_sendCode;

    private AppManager appManager;
    private CharSequence phoneTextChar="";

    //是否点击了编辑框
    private boolean isPhoneEdit = false;
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
        setTitle(this.getResources().getString(R.string.resetPassword1));
        setBaseContentView(R.layout.activity_reset_first);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        initView();

        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
    }

    private void initView() {

        et_reset_phone = (EditText) findViewById(R.id.et_reset_phone);
        ll_reset_clearPhone = (LinearLayout) findViewById(R.id.ll_reset_clearPhone);
        iv_reset_clearPhone = (ImageView) findViewById(R.id.iv_reset_clearPhone);
        btn_reset_sendCode = (Button) findViewById(R.id.btn_reset_sendCode);

        ll_reset_clearPhone.setOnClickListener(this);
        btn_reset_sendCode.setOnClickListener(this);

        //手机号文本框监听事件
        et_reset_phone.addTextChangedListener(new TextWatcher() {
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
                    iv_reset_clearPhone.setVisibility(View.VISIBLE);
                    iv_reset_clearPhone.setImageResource(R.mipmap.close_dark);
                    isPhoneEdit = true;
                    btn_reset_sendCode.setBackgroundResource(R.drawable.shape_corner);
                }else{
                    iv_reset_clearPhone.setVisibility(View.GONE);
                    isPhoneEdit = false;
                    btn_reset_sendCode.setBackgroundResource(R.drawable.shape_corner_dark);
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_reset_clearPhone:
                //清除文本框
                if(isPhoneEdit){
                    et_reset_phone.setText("");
                    iv_reset_clearPhone.setVisibility(View.GONE);
                    isPhoneEdit = false;
                    btn_reset_sendCode.setBackgroundResource(R.drawable.shape_corner_dark);
                }
                break;
            case R.id.btn_reset_sendCode:
                String phone = et_reset_phone.getText().toString();
                if(isPhoneEdit) {
                    if (AppValidationMgr.isPhone(phone)) {
                        Intent intent = new Intent(ResetActivityFirst.this, ResetActivitySecond.class);
                        intent.putExtra("phone",phone);
                        startActivity(intent);
                    }else{
                        AppToastMgr.shortToast(ResetActivityFirst.this, " 手机号有误！");
                    }
                }else{
                    AppToastMgr.shortToast(ResetActivityFirst.this, " 请输入手机号!");
                }
                break;
        }
    }
}
