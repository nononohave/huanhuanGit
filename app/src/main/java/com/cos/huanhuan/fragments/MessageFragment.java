package com.cos.huanhuan.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cos.huanhuan.R;
import com.cos.huanhuan.activitys.IndexActivity;
import com.cos.huanhuan.activitys.LoginActivity;
import com.cos.huanhuan.activitys.SettingActivity;
import com.cos.huanhuan.adapter.ConversationListAdapterEx;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.cos.huanhuan.views.TitleBar;
import com.cos.huanhuan.views.TitleSearchBar;

import java.util.HashMap;

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

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_message);
//        initView();
//    }
//
//    private void initView() {
//        //RongIM.getInstance().startConversationList();
//        sharedPreferencesHelper = new SharedPreferencesHelper(getActivity());
//        userValueData = sharedPreferencesHelper.getObject("userData");//该信息只用于判断登录与否，用户信息还是重新获取
//        if(userValueData != null) {
//            if (AppStringUtils.isEmpty(userValueData.getRongToken()) || userValueData.getRongToken().equals("default")) {
//                //未登录，无法连接到融云
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//            } else {
//                reconnect(userValueData.getRongToken());
//            }
//        }
//        titleBar = (TitleBar) getActivity().findViewById(R.id.message_title_bar);
//        titleBar.setBackgroundColor(getResources().getColor(R.color.white));
//        titleBar.setDividerColor(R.color.dividLineColor);
//        titleBar.setTitleColor(getResources().getColor(R.color.titleBarTextColor));
//        titleBar.setTitle(this.getResources().getString(R.string.message));
//        ConversationListFragment conversationListFragment = new ConversationListFragment();
//        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
//                .appendPath("conversationlist")       .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话，该会话聚合显示
//                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//设置系统会话，该会话非聚合显示
//                .build();
//        conversationListFragment.setUri(uri);
//    }

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

        ConversationListFragment conversationListFragment = new ConversationListFragment();
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")       .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话，该会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//设置系统会话，该会话非聚合显示
                .build();
        conversationListFragment.setUri(uri);
    }
    private void reconnect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "---onTokenIncorrect--");
                AppToastMgr.shortToast(getActivity(),"onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {

                HashMap<String, Boolean> hashMap = new HashMap<>();
                //会话类型 以及是否聚合显示
                hashMap.put(Conversation.ConversationType.PRIVATE.getName(),false);
                hashMap.put(Conversation.ConversationType.PUSH_SERVICE.getName(),true);
                hashMap.put(Conversation.ConversationType.SYSTEM.getName(),true);
                RongIM.getInstance().startConversationList(getActivity(),hashMap);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                AppToastMgr.shortToast(getActivity(),"onError");
                Log.e(TAG, "---onError--" + e);
            }
        });

    }
}
