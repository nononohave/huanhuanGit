package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.squareup.okhttp.Request;
import com.umeng.socialize.UMShareAPI;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountSecurityActivity extends BaseActivity implements View.OnClickListener{

    private String userId;
    private AppManager appManager;
    private RelativeLayout rl_phone,rl_alipay;
    private TextView tv_phone,tv_phone_bind,tv_alipay,tv_alipay_bind;
    private UserValueData userValueData;
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
        setTitle(this.getResources().getString(R.string.account_security));
        setBaseContentView(R.layout.activity_account_security);
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
        initData();
    }

    private void initView() {
        rl_phone = (RelativeLayout) findViewById(R.id.rl_account_security_phone);
        rl_alipay = (RelativeLayout) findViewById(R.id.rl_account_security_alipay);
        tv_phone = (TextView) findViewById(R.id.tv_account_security_phone);
        tv_phone_bind = (TextView) findViewById(R.id.tv_account_security_phone_bind);
        tv_alipay = (TextView) findViewById(R.id.tv_account_security_alipay);
        tv_alipay_bind = (TextView) findViewById(R.id.tv_account_security_alipay_bind);
        rl_phone.setOnClickListener(this);
        rl_alipay.setOnClickListener(this);
    }

    private void initData() {
        HttpRequest.getMembers(userId, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(AccountSecurityActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success){
                        JSONObject obj =jsonObject.getJSONObject("data");
                        userValueData = JsonUtils.fromJson(obj.toString(), UserValueData.class);
                        setData(userValueData);
                    }else{
                        AppToastMgr.shortToast(AccountSecurityActivity.this, " 接口调用失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_account_security_phone:
                Intent intentPhone = new Intent(AccountSecurityActivity.this,ExchangePhoneOrAlipayActivity.class);
                intentPhone.putExtra("type",1);
                intentPhone.putExtra("phoneNum",userValueData.getPhoneMob());
                startActivity(intentPhone);
                break;
            case R.id.rl_account_security_alipay:
                Intent intentAli = new Intent(AccountSecurityActivity.this,ExchangePhoneOrAlipayActivity.class);
                intentAli.putExtra("type",2);
                intentAli.putExtra("alipayNum",userValueData.getImAlipay());
                startActivity(intentAli);
                break;
        }
    }

    public void setData(UserValueData data) {
        if(AppStringUtils.isNotEmpty(data.getPhoneMob())){
            tv_phone.setText(data.getPhoneMob());
            tv_phone_bind.setText("更换绑定");
            tv_phone_bind.setTextColor(getResources().getColor(R.color.grey_black));
        }else{
            tv_phone.setText("未绑定手机号");
            tv_phone_bind.setText("去绑定");
            tv_phone_bind.setTextColor(getResources().getColor(R.color.titleBarTextColor));
        }
        if(AppStringUtils.isNotEmpty(data.getImAlipay())){
            tv_alipay.setText(data.getImAlipay());
            tv_alipay_bind.setText("更换绑定");
            tv_alipay_bind.setTextColor(getResources().getColor(R.color.grey_black));
        }else{
            tv_alipay.setText("未绑定支付宝");
            tv_alipay_bind.setText("去绑定");
            tv_alipay_bind.setTextColor(getResources().getColor(R.color.titleBarTextColor));
        }
    }
}
