package com.cos.huanhuan.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.activitys.BaseActivity;
import com.cos.huanhuan.activitys.BorrowAndExchangeActivity;
import com.cos.huanhuan.activitys.ExchangeDetailActivity;
import com.cos.huanhuan.activitys.LoginActivity;
import com.cos.huanhuan.activitys.MyExchangeActivity;
import com.cos.huanhuan.activitys.PersonDepositActivity;
import com.cos.huanhuan.activitys.PersonPublishActivity;
import com.cos.huanhuan.activitys.PersonVIPActivity;
import com.cos.huanhuan.activitys.PersonValueActivity;
import com.cos.huanhuan.activitys.PersonalDataActivity;
import com.cos.huanhuan.activitys.SettingActivity;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.PicassoUtils;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.cos.huanhuan.views.CircleImageView;
import com.cos.huanhuan.views.TitleBar;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.internal.framed.Settings;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonFragment extends Fragment implements View.OnClickListener{

    private TitleBar titleBar;
    private RelativeLayout rl_person_login,rl_person_noLogin,rl_person_value,rl_person_vip,rl_person_money,rl_person_publish,rl_person_exchange;
    private TextView person_nickName,person_Desc;
    private ImageView person_sex;
    private CircleImageView person_headImage,person_headImage_noLogin;
    private UserValueData userValueData;
    private SharedPreferencesHelper sharedPreferencesHelper;
    public static PersonFragment instance = null;
    public static PersonFragment getInstance(){
        if(instance == null){
            instance = new PersonFragment();
        }
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean isImmersive = false;
        if (hasKitKat() && !hasLollipop()) {
            isImmersive = true;
            //透明状态栏
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isImmersive = true;
        }
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        titleBar = (TitleBar) getActivity().findViewById(R.id.person_title_bar);
        titleBar.setBackgroundColor(getResources().getColor(R.color.white));
        titleBar.setDividerColor(R.color.dividLineColor);
        titleBar.setTitleColor(getResources().getColor(R.color.titleBarTextColor));
        titleBar.setTitle(this.getResources().getString(R.string.personal));
        titleBar.addAction(new TitleBar.ImageAction(R.mipmap.person_setting) {
            @Override
            public void performAction(View view) {
                Intent intentSetting = new Intent(getActivity(),SettingActivity.class);
                startActivity(intentSetting);
            }
        });
        sharedPreferencesHelper = new SharedPreferencesHelper(getActivity());
        //初始化控件
        rl_person_login = (RelativeLayout) getActivity().findViewById(R.id.rl_person_login);
        rl_person_noLogin = (RelativeLayout) getActivity().findViewById(R.id.rl_person_noLogin);
        rl_person_value = (RelativeLayout) getActivity().findViewById(R.id.rl_person_value);
        rl_person_vip = (RelativeLayout) getActivity().findViewById(R.id.rl_person_vip);
        rl_person_money = (RelativeLayout) getActivity().findViewById(R.id.rl_person_money);
        rl_person_publish = (RelativeLayout) getActivity().findViewById(R.id.rl_person_publish);
        rl_person_exchange = (RelativeLayout) getActivity().findViewById(R.id.rl_person_exchange);
        person_nickName = (TextView) getActivity().findViewById(R.id.tv_person_nickName);
        person_sex = (ImageView) getActivity().findViewById(R.id.iv_person_sex);
        person_Desc = (TextView) getActivity().findViewById(R.id.tv_person_personDesc);
        person_headImage = (CircleImageView) getActivity().findViewById(R.id.civ_person_headImage);
        person_headImage_noLogin = (CircleImageView) getActivity().findViewById(R.id.civ_person_headImage_noLogin);

        rl_person_login.setOnClickListener(this);
        rl_person_noLogin.setOnClickListener(this);
        rl_person_value.setOnClickListener(this);
        rl_person_vip.setOnClickListener(this);
        rl_person_money.setOnClickListener(this);
        rl_person_publish.setOnClickListener(this);
        rl_person_exchange.setOnClickListener(this);

        initData();
    }

    private void initData() {
        userValueData = sharedPreferencesHelper.getObject("userData");//该信息只用于判断登录与否，用户信息还是重新获取
        final UserValueData userValueDataDetail = sharedPreferencesHelper.getObject("userDataDetail");//该信息用户详细信息
        if(userValueDataDetail!=null) {
            //先设置一遍用户信息
            setData(userValueDataDetail);
        }
        if(userValueData != null){
            rl_person_login.setVisibility(View.VISIBLE);
            rl_person_noLogin.setVisibility(View.GONE);

            HttpRequest.getMembers(String.valueOf(userValueData.getId()), new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    AppToastMgr.shortToast(getActivity(),"请求失败！");
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
                            if(userValueDataDetail != null) {
                                if (!userValueDataDetail.equals(userValueDataItem)) {
                                    sharedPreferencesHelper.saveObject("userDataDetail", userValueDataItem);
                                    setData(userValueDataItem);
                                }
                            }else{
                                setData(userValueDataItem);
                            }
                        }else{
                            AppToastMgr.shortToast(getActivity(), " 接口调用失败！原因：" + errorMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            rl_person_login.setVisibility(View.GONE);
            rl_person_noLogin.setVisibility(View.VISIBLE);
        }

    }

    private void setData(UserValueData userValueDataItem) {
        PicassoUtils.getinstance().LoadImage(getActivity(), userValueDataItem.getPortrait(), person_headImage, R.mipmap.comment_grey, R.mipmap.comment_grey, PicassoUtils.PICASSO_BITMAP_SHOW_ROUND_TYPE, 0);
        if (AppStringUtils.isNotEmpty(userValueDataItem.getNickname())) {
            person_nickName.setText(userValueDataItem.getNickname());
        } else {
            person_nickName.setText("暂无昵称");
        }
        if (AppStringUtils.isNotEmpty(userValueDataItem.getDescribe())) {
            if (userValueDataItem.getDescribe().length() >= 14) {
                person_Desc.setText(userValueDataItem.getDescribe().substring(0, 13) + "...");
            } else {
                person_Desc.setText(userValueDataItem.getDescribe());
            }
        } else {
            person_Desc.setText("暂无个性签名");
        }
        if (userValueDataItem.getGender().equals("女")) {
            person_sex.setImageResource(R.mipmap.sex_woman);
        } else {
            person_sex.setImageResource(R.mipmap.sex_man);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_person_login:
                Intent intentPersonalData = new Intent(getActivity(), PersonalDataActivity.class);
                startActivity(intentPersonalData);
                break;
            case R.id.rl_person_noLogin:
                Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
                startActivity(intentLogin);
                break;
            case R.id.rl_person_value:
                Intent intentPersonValue = new Intent(getActivity(), PersonValueActivity.class);
                startActivity(intentPersonValue);
                break;
            case R.id.rl_person_vip:
                Intent intentVIP = new Intent(getActivity(), PersonVIPActivity.class);
                startActivity(intentVIP);
                break;
            case R.id.rl_person_money:
                Intent intentDeposit = new Intent(getActivity(), PersonDepositActivity.class);
                startActivity(intentDeposit);
                break;
            case R.id.rl_person_publish:
                Intent intentPublish = new Intent(getActivity(),PersonPublishActivity.class);
                startActivity(intentPublish);
                break;
            case R.id.rl_person_exchange:
                Intent intentMyExchange = new Intent(getActivity(),BorrowAndExchangeActivity.class);
                startActivity(intentMyExchange);
                break;
        }
    }
}
