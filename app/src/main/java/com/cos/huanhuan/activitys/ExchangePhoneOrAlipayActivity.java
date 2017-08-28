package com.cos.huanhuan.activitys;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;

public class ExchangePhoneOrAlipayActivity extends BaseActivity {

    private AppManager appManager;
    private int type;
    private TextView tv_phone_alipay_text,tv_phone_exchange,tv_exchange_tips;
    private Button btn_exchange_phone_alipay;
    private String phoneNumber,aliPayNumber;
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
        type = getIntent().getExtras().getInt("type");
        if(type == 1){
            //更换手机号
            setTitle(this.getResources().getString(R.string.exchange_phone));
            phoneNumber = getIntent().getExtras().getString("phoneNum");

        }else{
            //更换支付宝
            setTitle(this.getResources().getString(R.string.exchange_alipay));
            aliPayNumber = getIntent().getExtras().getString("alipayNum");
        }
        setBaseContentView(R.layout.activity_exchange_phone_or_alipay);
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
        tv_phone_alipay_text = (TextView) findViewById(R.id.tv_phone_alipay_text);
        tv_phone_exchange = (TextView) findViewById(R.id.tv_phone_exchange);
        btn_exchange_phone_alipay = (Button) findViewById(R.id.btn_exchange_phone_alipay);
        tv_exchange_tips = (TextView) findViewById(R.id.tv_exchange_tips);
        if(type == 1){
            tv_phone_exchange.setText(phoneNumber);
            if(AppStringUtils.isNotEmpty(phoneNumber)){
                btn_exchange_phone_alipay.setText("更换手机号");
                tv_phone_alipay_text.setText("当前绑定手机号");
            }else{
                btn_exchange_phone_alipay.setText("去绑定手机号");
                tv_phone_alipay_text.setText("未绑定手机号");
            }
            tv_exchange_tips.setText("手机号用于提现等操作");
        }else{
            tv_phone_exchange.setText(aliPayNumber);
            if(AppStringUtils.isNotEmpty(aliPayNumber)){
                btn_exchange_phone_alipay.setText("更换支付宝");
                tv_phone_alipay_text.setText("当前绑定支付宝");
            }else{
                btn_exchange_phone_alipay.setText("去绑定支付宝");
                tv_phone_alipay_text.setText("未绑定支付宝");
            }
            tv_exchange_tips.setText("支付宝用于提现等操作");
        }
    }
}
