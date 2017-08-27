package com.cos.huanhuan.activitys;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.PersonData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.views.TitleBar;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class EditPersonDataActivity extends BaseActivity {

    private AppManager appManager;
    private String userId;
    private EditText et_edit_personDesc;
    private int charLength = 0;
    private TextView tv_editTextNums;
    private String personDesc;
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
        setTitle(this.getResources().getString(R.string.edit_personDesc));
        setBaseContentView(R.layout.activity_edit_person_data);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        userId = getUserId();
        personDesc = getIntent().getExtras().getString("personDesc");
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        setRightButton(new TitleBar.TextAction(this.getResources().getString(R.string.save)) {
            @Override
            public void performAction(View view) {
                String desc = et_edit_personDesc.getText().toString();
                PersonData personData = new PersonData();
                personData.setId(Integer.valueOf(userId));
                personData.setDescribe(desc);
                HttpRequest.changePersonData(personData, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        AppToastMgr.shortToast(EditPersonDataActivity.this,"请求失败！");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            String str1 = response.body().string();
                            JSONObject jsonObject = new JSONObject(str1);
                            Boolean success = jsonObject.getBoolean("success");
                            if(success){
                                appManager.finishActivity();
                                AppToastMgr.shortToast(EditPersonDataActivity.this,"保存成功！");
                            }else{
                                String errorMsg = jsonObject.getString("errorMsg");
                                AppToastMgr.shortToast(EditPersonDataActivity.this,"修改失败！原因：" + errorMsg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        initView();
    }

    private void initView() {
        et_edit_personDesc = (EditText) findViewById(R.id.et_edit_personDesc);
        tv_editTextNums = (TextView) findViewById(R.id.tv_editTextNums);
        if(AppStringUtils.isNotEmpty(personDesc)){
            et_edit_personDesc.setText(personDesc);
            tv_editTextNums.setText(personDesc.length() + "/70");
            et_edit_personDesc.setSelection(personDesc.length());
        }
        et_edit_personDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                charLength = et_edit_personDesc.getText().toString().length();
                tv_editTextNums.setText(charLength + "/70");
            }
        });
    }
}
