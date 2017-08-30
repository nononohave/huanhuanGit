package com.cos.huanhuan.activitys;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;

public class OtherMoneyActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private EditText et_recharge_money;
    private Button btn_recharge_now;
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
        setTitle(this.getResources().getString(R.string.moneyRecharge));
        setBaseContentView(R.layout.activity_other_money);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        initView();
    }

    private void initView() {
        et_recharge_money = (EditText) findViewById(R.id.et_recharge_money);
        btn_recharge_now = (Button) findViewById(R.id.btn_recharge_now);
        btn_recharge_now.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_recharge_now:
                break;
        }
    }
}
