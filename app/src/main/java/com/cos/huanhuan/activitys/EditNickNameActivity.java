package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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

public class EditNickNameActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private EditText et_edit_nickName;
    private ImageView iv_edit_clearNick;
    private String userId;
    private String nickName;
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
        setTitle(this.getResources().getString(R.string.edit_nickName));
        setBaseContentView(R.layout.activity_edit_nick_name);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        userId = getUserId();
        nickName = getIntent().getExtras().getString("nickName");
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        setRightButton(new TitleBar.TextAction(this.getResources().getString(R.string.save)) {
            @Override
            public void performAction(View view) {
                String nickName = et_edit_nickName.getText().toString();
                PersonData personData = new PersonData();
                personData.setId(Integer.valueOf(userId));
                personData.setNickname(nickName);
                HttpRequest.changePersonData(personData, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        AppToastMgr.shortToast(EditNickNameActivity.this,"请求失败！");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            String str1 = response.body().string();
                            Log.i("地方撒阿达啥打法是否", "1111111111111111111111111---" + str1);
                            JSONObject jsonObject = new JSONObject(str1);
                            Boolean success = jsonObject.getBoolean("success");
                            if(success){
                                appManager.finishActivity();
                                AppToastMgr.shortToast(EditNickNameActivity.this,"保存成功！");
                            }else{
                                String errorMsg = jsonObject.getString("errorMsg");
                                AppToastMgr.shortToast(EditNickNameActivity.this,"修改失败！原因：" + errorMsg);
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

        et_edit_nickName = (EditText) findViewById(R.id.et_edit_nickName);
        iv_edit_clearNick = (ImageView) findViewById(R.id.iv_edit_clearNick);
        iv_edit_clearNick.setOnClickListener(this);
        if(AppStringUtils.isNotEmpty(nickName)){
            et_edit_nickName.setText(nickName);
            et_edit_nickName.setSelection(nickName.length());
            iv_edit_clearNick.setVisibility(View.VISIBLE);
        }else{
            et_edit_nickName.setText("");
            iv_edit_clearNick.setVisibility(View.GONE);
        }
        et_edit_nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                iv_edit_clearNick.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_edit_clearNick:
                et_edit_nickName.setText("");
                iv_edit_clearNick.setVisibility(View.GONE);
                break;
        }
    }
}
