package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.AppValidationMgr;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_register_phone,et_register_password;
    private LinearLayout ll_register_clearPhone,ll_register_showPassword;
    private TextView tv_accessLogin,tv_serviceAgree;
    private ImageView iv_register_clearPhone,iv_register_showPassword;
    private Button btn_register_next;

    private AppManager appManager;

    //是否点击了编辑框
    private boolean isPhoneEdit = false;
    private boolean isPassEdit = false;
    //密码是否显示
    private boolean isShow=true;

    private CharSequence phoneTextChar="";
    private CharSequence passTextChar="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        setTitleBarColor(R.color.white);
        setLeftImageResource(R.mipmap.nav_back);
        setDividerColor(R.color.dividLineColor);
        setTitleTextColor(R.color.titleBarTextColor);
        setTitle(this.getResources().getString(R.string.register1Title));
        setBaseContentView(R.layout.activity_register);
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
        et_register_phone = (EditText) findViewById(R.id.et_register_phone);
        et_register_password = (EditText) findViewById(R.id.et_register_password);

        ll_register_clearPhone = (LinearLayout) findViewById(R.id.ll_register_clearPhone);
        ll_register_showPassword = (LinearLayout) findViewById(R.id.ll_register_showPassword);

        tv_accessLogin = (TextView) findViewById(R.id.tv_accessLogin);
        tv_serviceAgree = (TextView) findViewById(R.id.tv_serviceAgree);

        iv_register_clearPhone = (ImageView) findViewById(R.id.iv_register_clearPhone);
        iv_register_showPassword = (ImageView) findViewById(R.id.iv_register_showPassword);

        btn_register_next = (Button) findViewById(R.id.btn_register_next);

        ll_register_clearPhone.setOnClickListener(this);
        ll_register_showPassword.setOnClickListener(this);
        tv_accessLogin.setOnClickListener(this);
        tv_serviceAgree.setOnClickListener(this);
        btn_register_next.setOnClickListener(this);

        //手机号文本框监听事件
        et_register_phone.addTextChangedListener(new TextWatcher() {
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
                    iv_register_clearPhone.setVisibility(View.VISIBLE);
                    iv_register_clearPhone.setImageResource(R.mipmap.close_dark);
                    isPhoneEdit = true;
                    if(isPassEdit){
                        btn_register_next.setBackgroundResource(R.drawable.shape_corner);
                    }
                }else{
                    iv_register_clearPhone.setVisibility(View.GONE);
                    isPhoneEdit = false;
                    btn_register_next.setBackgroundResource(R.drawable.shape_corner_dark);
                }
            }
        });

        //密码文本框监听事件
        et_register_password.addTextChangedListener(new TextWatcher() {
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
                        btn_register_next.setBackgroundResource(R.drawable.shape_corner);
                    }
                }else{
                    isPassEdit = false;
                    btn_register_next.setBackgroundResource(R.drawable.shape_corner_dark);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_register_clearPhone:
                //清除文本框
                if(isPhoneEdit){
                    et_register_phone.setText("");
                    iv_register_clearPhone.setVisibility(View.GONE);
                    isPhoneEdit = false;
                    btn_register_next.setBackgroundResource(R.drawable.shape_corner_dark);
                }
                break;
            case R.id.ll_register_showPassword:
                if(isShow){
                    iv_register_showPassword.setImageResource(R.mipmap.key_light);
                    et_register_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_register_password.setSelection(et_register_password.getText().length());
                    isShow = false;
                }else{
                    iv_register_showPassword.setImageResource(R.mipmap.key_dark);
                    et_register_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_register_password.setSelection(et_register_password.getText().length());
                    isShow = true;
                }
                break;
            case R.id.tv_accessLogin:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_serviceAgree:

                break;
            case R.id.btn_register_next:
                Intent intentNext = new Intent(RegisterActivity.this,RegisterSecondActivity.class);
                startActivity(intentNext);
                String phone = et_register_phone.getText().toString();
                String password = et_register_password.getText().toString();
                if(isPhoneEdit) {
                    if(isPassEdit) {
                        if (AppValidationMgr.isPhone(phone)) {
                        } else {
                            AppToastMgr.longToast(RegisterActivity.this, " 手机号有误！");
                        }
                    }else{
                        AppToastMgr.longToast(RegisterActivity.this, " 请输入密码!");
                    }
                }else{
                    AppToastMgr.longToast(RegisterActivity.this, " 请输入手机号!");
                }
                break;
        }
    }
}
