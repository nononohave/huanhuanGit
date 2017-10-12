package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.SharedPreferencesHelper;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class StartActivity extends AppCompatActivity {

    private UserValueData userValueData;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        sharedPreferencesHelper = new SharedPreferencesHelper(StartActivity.this);
        initConnectRIM();
        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 1500);
    }

    private void initConnectRIM() {
        userValueData = sharedPreferencesHelper.getObject("userData");//该信息只用于判断登录与否，用户信息还是重新获取
        if(userValueData != null) {
            if (AppStringUtils.isEmpty(userValueData.getRongToken()) || userValueData.getRongToken().equals("default")) {
                AppToastMgr.shortToast(StartActivity.this,"未登录");
            } else {
                if((RongIM.getInstance().getCurrentConnectionStatus() != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)){
                    reconnect(userValueData.getRongToken());
                }
            }
        }
    }

    private void reconnect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
            }

            @Override
            public void onSuccess(String s) {
                RongIM.getInstance().refreshUserInfoCache(new UserInfo(String.valueOf(userValueData.getId()), userValueData.getNickname(), Uri.parse(userValueData.getPortrait())));
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
            }
        });
    }

    class splashhandler implements Runnable {

        public void run() {
            Intent intent = new Intent(getApplication(),IndexActivity.class);
            startActivity(intent);
            StartActivity.this.finish();
        }

    }
}
