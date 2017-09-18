package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class AddNewCouponActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private EditText et_coupon_code;
    private Button btn_coupon_comfirm;
    private String userId;
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
        setTitle(this.getResources().getString(R.string.add_coupon));
        setBaseContentView(R.layout.activity_add_new_coupon);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        userId = getUserId();
        initView();
    }

    private void initView() {

        et_coupon_code = (EditText) findViewById(R.id.et_coupon_code);
        btn_coupon_comfirm = (Button) findViewById(R.id.btn_coupon_comfirm);
        btn_coupon_comfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String code = et_coupon_code.getText().toString();
        if(AppStringUtils.isNotEmpty(code)){
            HttpRequest.addNewCoupon(userId, code, new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    toastErrorMsg(AddNewCouponActivity.this, "请求失败！");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        String errorMsg = jsonObject.getString("errorMsg");
                        if(success){
                            AddNewCouponActivity.this.finish();
                        }else{
                            toastErrorMsg(AddNewCouponActivity.this, errorMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            toastErrorMsg(AddNewCouponActivity.this, "请输入兑换码！");
        }
    }
}
