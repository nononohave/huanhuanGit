package com.cos.huanhuan.activitys;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cos.huanhuan.R;
import com.cos.huanhuan.fragments.PayDetailFragment;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;

public class OtherMoneyActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private EditText et_recharge_money;
    private Button btn_recharge_now;
    private String userId;
    private Double rechargeMoney = 0.0;
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
        userId = getUserId();
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
                String decimal = et_recharge_money.getText().toString();
                if(AppStringUtils.isNotEmpty(decimal)) {
                    rechargeMoney = Double.parseDouble(decimal);
                    if(rechargeMoney > 0) {
                        PayDetailFragment payDetailFragment = new PayDetailFragment();
                        Bundle args = new Bundle();
                        args.putString("userId", userId);
                        args.putInt("type", 1);
                        args.putDouble("rechargeMoney", rechargeMoney);
                        payDetailFragment.setArguments(args);
                        payDetailFragment.show(getSupportFragmentManager(), "payDetailFragment");
                    }else{
                        AppToastMgr.shortToast(OtherMoneyActivity.this,"请输入正确的充值金额");
                    }
                }else{
                    AppToastMgr.shortToast(OtherMoneyActivity.this,"请输入充值金额");
                }
                break;
        }
    }
}
