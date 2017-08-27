package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.DataCleanManager;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.squareup.okhttp.internal.framed.Settings;

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private RelativeLayout rl_setting_address,rl_setting_save,rl_setting_aboutUs,rl_settint_clearCache,rl_setting_quit;
    private TextView tv_setting_cacheSize;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        setTitleBarColor(R.color.white);
        setLeftImageResource(R.mipmap.nav_back);
        setDividerColor(R.color.dividLineColor);
        setRightTextColor(R.color.titleBarTextColor);
        setTitleTextColor(R.color.titleBarTextColor);
        setTitle(this.getResources().getString(R.string.setting));
        setBaseContentView(R.layout.activity_setting);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(SettingActivity.this);
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

        rl_setting_address = (RelativeLayout) findViewById(R.id.rl_setting_address);
        rl_setting_save = (RelativeLayout) findViewById(R.id.rl_setting_save);
        rl_setting_aboutUs = (RelativeLayout) findViewById(R.id.rl_setting_aboutUs);
        rl_settint_clearCache = (RelativeLayout) findViewById(R.id.rl_settint_clearCache);
        rl_setting_quit = (RelativeLayout) findViewById(R.id.rl_setting_quit);
        tv_setting_cacheSize = (TextView) findViewById(R.id.tv_setting_cacheSize);

        rl_setting_address.setOnClickListener(this);
        rl_setting_save.setOnClickListener(this);
        rl_setting_aboutUs.setOnClickListener(this);
        rl_settint_clearCache.setOnClickListener(this);
        rl_setting_quit.setOnClickListener(this);
    }
    private void initData() {
        String cacheSize = "0.0";
        try {
            cacheSize = DataCleanManager.getTotalCacheSize(SettingActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_setting_cacheSize.setText(cacheSize);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_setting_address:
                Intent intentAddress = new Intent(SettingActivity.this,ManageAddressActivity.class);
                startActivity(intentAddress);
                break;
            case R.id.rl_setting_save:
                break;
            case R.id.rl_setting_aboutUs:
                break;
            case R.id.rl_settint_clearCache:
                DataCleanManager.clearAllCache(SettingActivity.this);
                initData();
                break;
            case R.id.rl_setting_quit:
                sharedPreferencesHelper.clear();
                Intent intentLogin = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intentLogin);
                break;
        }
    }
}
