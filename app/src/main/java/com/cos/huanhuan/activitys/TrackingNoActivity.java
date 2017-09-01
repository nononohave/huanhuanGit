package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TrackingNoActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private EditText et_tracking;
    private Button btn_tracking_now;
    private String exchangeId;
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
        setTitle(this.getResources().getString(R.string.tracking_no));
        setBaseContentView(R.layout.activity_tracking_no);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        exchangeId = getIntent().getExtras().getString("exchangeId");
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        initView();
    }

    private void initView() {

        et_tracking = (EditText) findViewById(R.id.et_tracking);
        btn_tracking_now = (Button) findViewById(R.id.btn_tracking_now);
        btn_tracking_now.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String tracking = et_tracking.getText().toString();
        if(AppStringUtils.isNotEmpty(tracking)){
            HttpRequest.commitTrackingNo(exchangeId, tracking, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    AppToastMgr.shortToast(TrackingNoActivity.this, "请求失败！");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        if (null != response.cacheResponse()) {
                            String str = response.cacheResponse().toString();
                        } else {
                            try {
                                String str1 = response.body().string();
                                JSONObject jsonObject = new JSONObject(str1);
                                Boolean success = jsonObject.getBoolean("success");
                                if(success){
                                    Intent returnIntent =new Intent();
                                    setResult(222,returnIntent);
                                    appManager.finishActivity();
                                }else{
                                    String errorMsg = jsonObject.getString("errorMsg");
                                    AppToastMgr.shortToast(TrackingNoActivity.this,"修改失败！原因：" + errorMsg);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String str = response.networkResponse().toString();
                            Log.i("wangshu3", "network---" + str);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{

        }
    }
}
