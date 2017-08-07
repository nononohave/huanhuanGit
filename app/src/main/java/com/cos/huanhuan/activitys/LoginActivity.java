package com.cos.huanhuan.activitys;

import android.content.Intent;
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

import com.cos.huanhuan.MainActivity;
import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.AppValidationMgr;
import com.cos.huanhuan.views.TitleBar;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

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
        initView();

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
                String phone = et_phone.getText().toString();
                String password = et_password.getText().toString();
                if(isPhoneEdit) {
                    if(isPassEdit) {
                        if (AppValidationMgr.isPhone(phone)) {
                            AppToastMgr.shortToast(LoginActivity.this, " 登录");
                        } else {
                            AppToastMgr.shortToast(LoginActivity.this, " 手机号有误！");
                        }
                    }else{
                        AppToastMgr.shortToast(LoginActivity.this, " 请输入密码!");
                    }
                }else{
                    AppToastMgr.longToast(LoginActivity.this, " 请输入手机号!");
                }
                break;
            case R.id.iv_wxLogin:
                //微信授权登录
                AppToastMgr.shortToast(LoginActivity.this,"微信授权登录");
                break;
            case R.id.iv_wbLogin:
                //微博授权登录
                AppToastMgr.shortToast(LoginActivity.this,"微博授权登录");
                break;
            case R.id.iv_qqLogin:
                //qq授权登录
                AppToastMgr.shortToast(LoginActivity.this,"qq授权登录");
                break;
            case R.id.tv_forgetPassword:
                Intent intent = new Intent(LoginActivity.this, ResetActivityFirst.class);
                startActivity(intent);
                break;
        }
    }
}
