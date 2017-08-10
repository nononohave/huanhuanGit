package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class UserAgreeActivity extends BaseActivity {

    private AppManager appManager;
    private WebView wv_userAgree;
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
        setTitle(this.getResources().getString(R.string.userAgreement));
        setBaseContentView(R.layout.activity_user_agree);
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
        wv_userAgree = (WebView) findViewById(R.id.wv_userAgree);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HttpRequest.getUserAgreeMent(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        AppToastMgr.shortToast(UserAgreeActivity.this,"请求失败！");
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean success = jsonObject.getBoolean("success");
                            String errMsg = jsonObject.getString("errorMsg");
                            if(success){
                                String html = jsonObject.getString("data");
                                wv_userAgree.loadDataWithBaseURL(null,html,"text/html","utf-8",null);
                            }else{
                                AppToastMgr.shortToast(UserAgreeActivity.this,"请求失败！原因：" + errMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
