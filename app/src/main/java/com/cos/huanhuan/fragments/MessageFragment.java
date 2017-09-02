package com.cos.huanhuan.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cos.huanhuan.R;
import com.cos.huanhuan.activitys.LoginActivity;
import com.cos.huanhuan.activitys.SettingActivity;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.cos.huanhuan.views.TitleBar;
import com.cos.huanhuan.views.TitleSearchBar;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by Administrator on 2017/9/2.
 */

public class MessageFragment extends Fragment{
    private static final String TAG = MessageFragment.class.getSimpleName();
    private TitleBar titleBar;
    private Conversation.ConversationType[] mConversationsTypes = null;
    public static MessageFragment instance = null;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private UserValueData userValueData;
    public static MessageFragment getInstance(){
        if(instance == null){
            instance = new MessageFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferencesHelper = new SharedPreferencesHelper(getActivity());
        userValueData = sharedPreferencesHelper.getObject("userData");//该信息只用于判断登录与否，用户信息还是重新获取
        if(userValueData != null) {
            if (AppStringUtils.isEmpty(userValueData.getRongToken()) || userValueData.getRongToken().equals("default")) {
                //未登录，无法连接到融云
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
                reconnect(userValueData.getRongToken());
            }
        }
        titleBar = (TitleBar) getActivity().findViewById(R.id.message_title_bar);
        titleBar.setBackgroundColor(getResources().getColor(R.color.white));
        titleBar.setDividerColor(R.color.dividLineColor);
        titleBar.setTitleColor(getResources().getColor(R.color.titleBarTextColor));
        titleBar.setTitle(this.getResources().getString(R.string.message));
    }
    private void reconnect(String token) {
        RongIM.connect("t6c5oYz5grEOnn20oFvaDAndU7yxQsLXA3v69KPI1FJQdVx0/jPeVr38LU+tEbqemeTJ37DbHM/1Xp1NrJG7Aw==", new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "---onTokenIncorrect--");
                AppToastMgr.shortToast(getActivity(),"onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {

                AppToastMgr.shortToast(getActivity(),"连接融云成功");
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                AppToastMgr.shortToast(getActivity(),"onError");
                Log.e(TAG, "---onError--" + e);
            }
        });

    }
}
