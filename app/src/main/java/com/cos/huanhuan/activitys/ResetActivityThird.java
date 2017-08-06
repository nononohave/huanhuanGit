package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;

public class ResetActivityThird extends BaseActivity implements View.OnClickListener{

    private EditText et_reset3_newPassword,et_reset3_confirmPassword;

    private LinearLayout ll_reset3_showPassword,ll_reset3_confirmPassword;

    private ImageView iv_reset3_showPassword,iv_reset3_confirmPassword;

    private Button btn_reset3_finish;

    private AppManager appManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_third);

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

        ll_reset3_showPassword = (LinearLayout) findViewById(R.id.ll_reset3_showPassword);
        ll_reset3_confirmPassword = (LinearLayout) findViewById(R.id.ll_reset3_confirmPassword);

        iv_reset3_showPassword = (ImageView) findViewById(R.id.iv_reset3_showPassword);
        iv_reset3_confirmPassword = (ImageView) findViewById(R.id.iv_reset3_confirmPassword);

        btn_reset3_finish = (Button) findViewById(R.id.btn_reset3_finish);

        ll_reset3_showPassword.setOnClickListener(this);
        iv_reset3_confirmPassword.setOnClickListener(this);
        btn_reset3_finish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_reset3_showPassword:

                break;
            case R.id.iv_reset3_confirmPassword:

                break;
            case R.id.btn_reset3_finish:
                Intent intent = new Intent(ResetActivityThird.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
