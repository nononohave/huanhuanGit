package com.cos.huanhuan.activitys;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.BindAlipay;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.AppValidationMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class BindAlipayActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private EditText et_bind_alipay,et_bind_name;
    private LinearLayout ll_alipay_clearPhone,ll_name_clearPhone;
    private ImageView iv_alipay_clearPhone,iv_name_clearPhone;
    private Button btn_bind_alipay_finish;
    private CharSequence alipayChar,nameChar;
    private Boolean isAlipayEdit = false;
    private Boolean isNameEdit = false;
    private String userId;
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
        setBaseContentView(R.layout.activity_bind_alipay);
        setTitle(this.getResources().getString(R.string.bind_alipay));
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
        et_bind_alipay = (EditText) findViewById(R.id.et_bind_alipay);
        et_bind_name = (EditText) findViewById(R.id.et_bind_name);
        ll_alipay_clearPhone = (LinearLayout) findViewById(R.id.ll_alipay_clearPhone);
        ll_name_clearPhone = (LinearLayout) findViewById(R.id.ll_name_clearPhone);
        iv_alipay_clearPhone = (ImageView) findViewById(R.id.iv_alipay_clearPhone);
        iv_name_clearPhone = (ImageView) findViewById(R.id.iv_name_clearPhone);
        btn_bind_alipay_finish = (Button) findViewById(R.id.btn_bind_alipay_finish);

        ll_alipay_clearPhone.setOnClickListener(this);
        ll_name_clearPhone.setOnClickListener(this);
        btn_bind_alipay_finish.setOnClickListener(this);
        et_bind_alipay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                alipayChar = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(alipayChar.length() > 0){
                    if(isNameEdit) {
                        btn_bind_alipay_finish.setBackgroundResource(R.drawable.shape_corner);
                    }
                    isAlipayEdit = true;
                }else{
                    btn_bind_alipay_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                    isAlipayEdit = false;
                }
            }
        });
        et_bind_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameChar = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(nameChar.length() > 0){
                    if(isAlipayEdit) {
                        btn_bind_alipay_finish.setBackgroundResource(R.drawable.shape_corner);
                    }
                    isNameEdit = true;
                }else{
                    btn_bind_alipay_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                    isNameEdit = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_alipay_clearPhone:
                et_bind_alipay.setText("");
                isAlipayEdit = false;
                btn_bind_alipay_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                iv_alipay_clearPhone.setVisibility(View.GONE);
                break;
            case R.id.ll_name_clearPhone:
                et_bind_name.setText("");
                isNameEdit = false;
                btn_bind_alipay_finish.setBackgroundResource(R.drawable.shape_corner_dark);
                iv_name_clearPhone.setVisibility(View.GONE);
                break;
            case R.id.btn_bind_alipay_finish:
                String aliPayText = et_bind_alipay.getText().toString();
                String realName = et_bind_name.getText().toString();
                if(AppStringUtils.isNotEmpty(aliPayText)){
                    if(AppStringUtils.isNotEmpty(realName)){
                        if(AppValidationMgr.isRealName(realName)){
                            BindAlipay bindAlipay = new BindAlipay();
                            bindAlipay.setId(Integer.valueOf(userId));
                            bindAlipay.setImAlipay(aliPayText);
                            bindAlipay.setRealName(realName);
                            HttpRequest.changeBindAlipay(bindAlipay, new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    AppToastMgr.shortToast(BindAlipayActivity.this, "请求失败！");
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    try {
                                        if (null != response.cacheResponse()) {
                                        } else {
                                            try {
                                                String str1 = response.body().string();
                                                JSONObject jsonObject = new JSONObject(str1);
                                                Boolean success = jsonObject.getBoolean("success");
                                                if (success) {
                                                    AppToastMgr.shortToast(BindAlipayActivity.this, "修改成功！");
                                                    appManager.finishActivity(ExchangePhoneOrAlipayActivity.exchangeInstance);
                                                    appManager.finishActivity();
                                                } else {
                                                    String errorMsg = jsonObject.getString("errorMsg");
                                                    AppToastMgr.shortToast(BindAlipayActivity.this, "修改失败！原因：" + errorMsg);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }else{
                            AppToastMgr.shortToast(BindAlipayActivity.this,"真实姓名格式有误");
                        }
                    }else{
                        AppToastMgr.shortToast(BindAlipayActivity.this,"请输入真实姓名");
                    }
                }else{
                    AppToastMgr.shortToast(BindAlipayActivity.this,"请输入支付宝账户");
                }
                break;
        }
    }
}
