package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;

public class  BorrowAndExchangeActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private RelativeLayout rl_personal_exchange,rl_personal_borrow;
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
        setBaseContentView(R.layout.activity_borrow_and_exchange);
        setTitle(this.getResources().getString(R.string.person_exchange));
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
        rl_personal_exchange = (RelativeLayout) findViewById(R.id.rl_personal_exchange);
        rl_personal_borrow = (RelativeLayout) findViewById(R.id.rl_personal_borrow);

        rl_personal_exchange.setOnClickListener(this);
        rl_personal_borrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_personal_exchange:
                Intent intentExchange = new Intent(BorrowAndExchangeActivity.this,MyExchangeActivity.class);
                startActivity(intentExchange);
                break;
            case R.id.rl_personal_borrow:
                Intent intentBorrow = new Intent(BorrowAndExchangeActivity.this,PersonBorrowActivity.class);
                startActivity(intentBorrow);
                break;
        }
    }
}
