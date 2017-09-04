package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.ConversationListAdapterEx;
import com.cos.huanhuan.fragments.CooperateFragment;
import com.cos.huanhuan.fragments.IndexFragment;
import com.cos.huanhuan.fragments.MessageFragment;
import com.cos.huanhuan.fragments.PersonFragment;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.PicassoUtils;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.cos.huanhuan.views.BottomBarItem;
import com.cos.huanhuan.views.BottomBarLayout;
import com.cos.huanhuan.views.TabFragment;
import com.cos.huanhuan.views.TitleBar;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class IndexActivity extends FragmentActivity implements RongIM.UserInfoProvider{
    private ViewPager mVpContent;
    private BottomBarLayout mBottomBarLayout;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private RotateAnimation mRotateAnimation;
    private Handler mHandler = new Handler();
    private TitleBar index_title_bar;
    private AppManager appManager;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private UserValueData userValueData;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            setImmersive(true);
//        }
        setContentView(R.layout.activity_index);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        handler=new MyHandler();
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mVpContent = (ViewPager) findViewById(R.id.vp_content);
        mBottomBarLayout = (BottomBarLayout) findViewById(R.id.bbl);
        index_title_bar = (TitleBar) findViewById(R.id.index_title_bar);
        RongIM.setUserInfoProvider(this, true);
    }

    private void initData() {

        IndexFragment homeFragment = new IndexFragment();
        mFragmentList.add(homeFragment);

        CooperateFragment cooperateFragment = new CooperateFragment();
        mFragmentList.add(cooperateFragment);


//        MessageFragment messageFragment = new MessageFragment();
//        mFragmentList.add(messageFragment);

        RongIM.getInstance().setConversationToTop(Conversation.ConversationType.SYSTEM,"34",true);
        ConversationListFragment conversationListFragment = new ConversationListFragment();
        Uri uri = Uri.parse("rong://" + IndexActivity.this.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")       .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话，该会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//设置系统会话，该会话非聚合显示
                .build();
        conversationListFragment.setUri(uri);
        conversationListFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
        mFragmentList.add(conversationListFragment);

        PersonFragment meFragment = new PersonFragment();
        mFragmentList.add(meFragment);
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
                        AppToastMgr.shortToast(IndexActivity.this,"请求失败！");
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
                                AppToastMgr.shortToast(IndexActivity.this, " 接口调用失败！原因：" + errorMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
    private void initListener() {
        mVpContent.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mBottomBarLayout.setViewPager(mVpContent);
        mBottomBarLayout.setOnItemSelectedListener(new BottomBarLayout.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final BottomBarItem bottomBarItem, int position) {
                if (position == 0){
                    //如果是第一个，即首页
                    if (mBottomBarLayout.getCurrentItem() == position){
                        //如果是在原来位置上点击,更换首页图标并播放旋转动画
                        bottomBarItem.setIconSelectedResourceId(R.mipmap.tab_loading);//更换成加载图标
                        bottomBarItem.setStatus(true);

                        //播放旋转动画
                        if (mRotateAnimation == null) {
                            mRotateAnimation = new RotateAnimation(0, 360,
                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                    0.5f);
                            mRotateAnimation.setDuration(800);
                            mRotateAnimation.setRepeatCount(-1);
                        }
                        ImageView bottomImageView = bottomBarItem.getImageView();
                        bottomImageView.setAnimation(mRotateAnimation);
                        bottomImageView.startAnimation(mRotateAnimation);//播放旋转动画

                        //模拟数据刷新完毕
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bottomBarItem.setIconSelectedResourceId(R.mipmap.tab_home_selected);//更换成首页原来图标
                                bottomBarItem.setStatus(true);//刷新图标
                                cancelTabLoading(bottomBarItem);
                            }
                        },3000);
                        return;
                    }
                }

                //如果点击了其他条目
                BottomBarItem bottomItem = mBottomBarLayout.getBottomItem(0);
                bottomItem.setIconSelectedResourceId(R.mipmap.tab_home_selected);//更换为原来的图标

                cancelTabLoading(bottomItem);//停止旋转动画
                if(position == 2){
                    sharedPreferencesHelper = new SharedPreferencesHelper(IndexActivity.this);
                    userValueData = sharedPreferencesHelper.getObject("userData");//该信息只用于判断登录与否，用户信息还是重新获取
                    if(userValueData != null) {
                        if (AppStringUtils.isEmpty(userValueData.getRongToken()) || userValueData.getRongToken().equals("default")) {
                            //未登录，无法连接到融云
                            //startActivity(new Intent(IndexActivity.this, LoginActivity.class));
                            AppToastMgr.shortToast(IndexActivity.this,"未登录");
                        } else {
                            if((RongIM.getInstance().getCurrentConnectionStatus() != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)){
                                reconnect(userValueData.getRongToken());
                                //RongIM.getInstance().setConversationToTop(uiConversation.getConversationType(),uiConversation.getConversationTargetId(),true);
                            }else{
                                AppToastMgr.shortToast(IndexActivity.this,"已连接");
                            }
                        }
                    }

                    index_title_bar.setVisibility(View.VISIBLE);
                    index_title_bar.setBackgroundColor(getResources().getColor(R.color.white));
                    index_title_bar.setDividerColor(R.color.dividLineColor);
                    index_title_bar.setTitleColor(getResources().getColor(R.color.titleBarTextColor));
                    index_title_bar.setTitle(getResources().getString(R.string.message));
                }else{
                    index_title_bar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**停止首页页签的旋转动画*/
    private void cancelTabLoading(BottomBarItem bottomItem) {
        Animation animation = bottomItem.getImageView().getAnimation();
        if (animation != null){
            animation.cancel();
        }
    }

    @Override
    public UserInfo getUserInfo(String s) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("userId", s);
        message.setData(bundle);
        handler.sendMessage(message);//发送message信息
        message.what = 0;//标志是哪个线程传数据
        return null;
    }

    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
    private void reconnect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                AppToastMgr.shortToast(IndexActivity.this,"onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {

//                HashMap<String, Boolean> hashMap = new HashMap<>();
//                //会话类型 以及是否聚合显示
//                hashMap.put(Conversation.ConversationType.PRIVATE.getName(),false);
//                hashMap.put(Conversation.ConversationType.PUSH_SERVICE.getName(),true);
//                hashMap.put(Conversation.ConversationType.SYSTEM.getName(),true);
//                RongIM.getInstance().startConversationList(IndexActivity.this,hashMap);
                AppToastMgr.shortToast(IndexActivity.this,"连接融云成功");
                RongIM.getInstance().refreshUserInfoCache(new UserInfo(String.valueOf(userValueData.getId()),userValueData.getNickname(),Uri.parse(userValueData.getPortrait())));
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                AppToastMgr.shortToast(IndexActivity.this,"onError");
            }
        });

    }
}
