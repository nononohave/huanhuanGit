package com.cos.huanhuan.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;

public class CrashActivity extends BaseActivity implements View.OnClickListener{

    private TextView crashClick;
    private AppManager appManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBarColor(R.color.white);
        setDividerColor(R.color.dividLineColor);
        setRightTextColor(R.color.titleBarTextColor);
        setTitleTextColor(R.color.titleBarTextColor);
        setTitle(this.getResources().getString(R.string.crashExit));
        setBaseContentView(R.layout.activity_crash);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        initView();
    }

    private void initView() {
        crashClick = (TextView) findViewById(R.id.crashClick);
        crashClick.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.crashClick:
                appManager.finishAllActivity();
            break;
        }
    }
}
