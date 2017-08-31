package com.cos.huanhuan.activitys;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.UserInfo;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.PicassoUtils;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonDepositActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private String userId;
    private TextView person_deposit;
    private Button btn_person_deposit;
    private UserValueData userValueItem;
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
        setTitle(this.getResources().getString(R.string.person_deposit));
        setBaseContentView(R.layout.activity_person_deposit);
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
        person_deposit = (TextView) findViewById(R.id.tv_person_deposit);
        btn_person_deposit = (Button) findViewById(R.id.btn_person_deposit);
        btn_person_deposit.setOnClickListener(this);
    }

    private void initData() {
        HttpRequest.getMembers(userId, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(PersonDepositActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success){
                        JSONObject obj =jsonObject.getJSONObject("data");
                        userValueItem = JsonUtils.fromJson(obj.toString(), UserValueData.class);
                        person_deposit.setText(userValueItem.getDeposit() + "元");
                    }else{
                        AppToastMgr.shortToast(PersonDepositActivity.this, " 接口调用失败！原因：" + errorMsg);
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
            case R.id.btn_person_deposit:
                if(userValueItem != null && userValueItem.getDeposit() != 0) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserId(Integer.valueOf(userId));
                    HttpRequest.returnDeposit(userInfo, new StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            AppToastMgr.shortToast(PersonDepositActivity.this, "请求失败！");
                        }

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Boolean success = jsonObject.getBoolean("success");
                                String errorMsg = jsonObject.getString("errorMsg");
                                if (success) {
                                    AppToastMgr.longToast(PersonDepositActivity.this, "已提交退还处理，请等待后台审核");
                                    initData();
                                } else {
                                    AppToastMgr.shortToast(PersonDepositActivity.this, " 接口调用失败！原因：" + errorMsg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    AppToastMgr.shortToast(PersonDepositActivity.this, "您没有押金可退还");
                }
            break;
        }
    }
}
