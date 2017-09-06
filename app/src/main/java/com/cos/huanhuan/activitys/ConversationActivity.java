package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.cos.huanhuan.utils.SoftHideKeyBoardUtil;
import com.cos.huanhuan.views.TitleBar;
import com.squareup.okhttp.Request;
import com.ta.utdid2.android.utils.StringUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongExtension;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Administrator on 2017/9/2.
 */

public class ConversationActivity extends FragmentActivity implements RongIM.UserInfoProvider{
    private AppManager appManager;
    private TitleBar titleBar;
    private String userId,sUserName;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private UserValueData userValueData;
    private Handler handler;
    private RongExtension rc_extension;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        userId = getIntent().getData().getQueryParameter("targetId");//用户id
        sUserName = getIntent().getData().getQueryParameter("title");//昵称
        initView();
        initData();
        sharedPreferencesHelper = new SharedPreferencesHelper(ConversationActivity.this);
        handler=new MyHandler();
        userValueData = sharedPreferencesHelper.getObject("userData");//该信息只用于判断登录与否，用户信息还是重新获取
        appManager = AppManager.getAppManager();
        SoftHideKeyBoardUtil.assistActivity(this);
        appManager.addActivity(this);    }

    private void initView() {
        boolean isImmersive = false;
        if (hasKitKat() && !hasLollipop()) {
            isImmersive = true;
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isImmersive = true;
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        titleBar = (TitleBar) ConversationActivity.this.findViewById(R.id.conversation_title_bar);
        rc_extension = (RongExtension) findViewById(R.id.rc_extension);
        if(userId.equals("34")){
            rc_extension.setVisibility(View.GONE);
        }else{
            rc_extension.setVisibility(View.VISIBLE);
        }

        titleBar.setBackgroundColor(getResources().getColor(R.color.white));
        titleBar.setDividerColor(R.color.dividLineColor);
        titleBar.setTitleColor(getResources().getColor(R.color.titleBarTextColor));
        titleBar.setTitle(this.getResources().getString(R.string.message));
        titleBar.setLeftImageResource(R.mipmap.nav_back);
        titleBar.setLeftClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        RongIM.setUserInfoProvider(this, true);
        if(AppStringUtils.isNotEmpty(sUserName)){
            titleBar.setTitle(sUserName);
        }
    }
    class MyHandler extends Handler
    {
        //接受message的信息
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if(msg.what==0){
                String userId = data.getString("userId");
                HttpRequest.getMembers(userId, new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        AppToastMgr.shortToast(ConversationActivity.this,"请求失败！");
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean success = jsonObject.getBoolean("success");
                            String errorMsg = jsonObject.getString("errorMsg");
                            if(success){
                                JSONObject obj =jsonObject.getJSONObject("data");
                                UserValueData userValueDataItem = JsonUtils.fromJson(obj.toString(), UserValueData.class);
                                RongIM.getInstance().refreshUserInfoCache(new UserInfo(String.valueOf(userValueDataItem.getId()),userValueDataItem.getNickname(),Uri.parse(userValueDataItem.getPortrait())));
                            }else{
                                AppToastMgr.shortToast(ConversationActivity.this, " 接口调用失败！原因：" + errorMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
    private void initData() {
        HttpRequest.getMembers(userId, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {

            }
        });
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public UserInfo getUserInfo(String s) {
        Message message=new Message();
        Bundle bundle = new Bundle();
        bundle.putString("userId",s);
        message.setData(bundle);
        handler.sendMessage(message);//发送message信息
        message.what=0;//标志是哪个线程传数据
        return null;
    }
}
