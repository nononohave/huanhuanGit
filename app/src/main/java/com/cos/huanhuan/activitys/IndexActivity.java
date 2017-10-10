package com.cos.huanhuan.activitys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.ConversationListAdapterEx;
import com.cos.huanhuan.apksupdate.ApkUpdateInfoBean;
import com.cos.huanhuan.apksupdate.ApkUpdateInfoLoadBiz;
import com.cos.huanhuan.apksupdate.ApkUpdateInfoLoadBizImp;
import com.cos.huanhuan.apksupdate.UpdateAgent;
import com.cos.huanhuan.fragments.CooperateFragment;
import com.cos.huanhuan.fragments.IndexFragment;
import com.cos.huanhuan.fragments.MessageFragment;
import com.cos.huanhuan.fragments.PersonFragment;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.GlobalStands;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    ApkUpdateInfoBean infoBean;
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
    private static final int RETURN_CAMERA_CODE = 123;
    private static final int RETURN_PHOTOS_CODE = 124;
    private UpdateAgent.OnPromptListener2 mOnPromptListener;
    private UpdateAgent.OnProgressListener mOnProgressListener;
    private UpdateAgent.OnProgressListener mOnNotificationListener;
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
        mOnPromptListener = new OnPrompt(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!GlobalStands.isUpdateShow) {
                    ApkUpdateInfoLoadBiz loadBiz = new ApkUpdateInfoLoadBizImp();
                    infoBean = loadBiz.apkUpdateInfoLoad(getAppVersionName(IndexActivity.this));
                    SystemClock.sleep(200);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (infoBean != null) {
                                check();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void initView() {
        mVpContent = (ViewPager) findViewById(R.id.vp_content);
        mVpContent.setOffscreenPageLimit(4);
        mBottomBarLayout = (BottomBarLayout) findViewById(R.id.bbl);
        index_title_bar = (TitleBar) findViewById(R.id.index_title_bar);
        RongIM.setUserInfoProvider(this, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断该应用是否有写SD卡权限，如果没有再去申请
            if (ContextCompat.checkSelfPermission(IndexActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(IndexActivity.this, new String[]{Manifest.permission.CAMERA}, RETURN_CAMERA_CODE);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断该应用是否有写SD卡权限，如果没有再去申请
            if (ContextCompat.checkSelfPermission(IndexActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(IndexActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RETURN_PHOTOS_CODE);
            }
        }
    }

    private void initData() {

        IndexFragment homeFragment = new IndexFragment();
        mFragmentList.add(homeFragment);

        CooperateFragment cooperateFragment = new CooperateFragment();
        mFragmentList.add(cooperateFragment);


//        MessageFragment messageFragment = new MessageFragment();
//        mFragmentList.add(messageFragment);
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

    void check() {
        //如果版本不一致
        if(infoBean.getUpdate() != null && infoBean.getApkUrl() != null) {
            if (infoBean.getUpdate()) {
                GlobalStands.isUpdateShow = true;
                mOnPromptListener.onPrompt2(infoBean);
            }
        }
    }

    /**
     * 返回当前程序版本名
     */

    public String getAppVersionName(Context context) {
        String versionName = "";
        int versioncode;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    private class OnPrompt implements UpdateAgent.OnPromptListener2 {

        private Context mContext;

        public OnPrompt(Context context) {
            mContext = context;
        }

        @Override
        public void onPrompt2(ApkUpdateInfoBean info) {
            String content = String.format("");
            //String content = String.format("最新版本：%1$s\n新版本大小：%2$s\n\n更新内容\n%3$s", info.getVersionName(), "未知大小", info.getNewFeature());
            final AlertDialog dialog = new AlertDialog.Builder(mContext).create();

            dialog.setTitle("应用更新");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);


            float density = mContext.getResources().getDisplayMetrics().density;
            TextView tv = new TextView(mContext);
            tv.setMovementMethod(new ScrollingMovementMethod());
            tv.setVerticalScrollBarEnabled(true);
            tv.setTextSize(14);
            //tv.setMaxHeight((int) (250 * density));
            tv.setMaxHeight((int) (50 * density));
            dialog.setView(tv, (int) (25 * density), (int) (15 * density), (int) (25 * density), 0);

            DialogInterface.OnClickListener listener = new OnPromptClick(true);
            tv.setText(content);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "立即更新", listener);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "以后再说", listener);
            dialog.show();
        }
    }

    public class OnPromptClick implements DialogInterface.OnClickListener {
        private final boolean mIsAutoDismiss;

        public OnPromptClick(boolean isAutoDismiss) {
            mIsAutoDismiss = isAutoDismiss;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    onDownload();
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    dialog.dismiss();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    // not now
                    break;
            }
            if (mIsAutoDismiss) {
                dialog.dismiss();
            }
        }
    }

    protected void onDownload() {
        if (mOnNotificationListener == null) {
            mOnNotificationListener = new UpdateAgent.EmptyProgress();
        }
        if (mOnProgressListener == null) {
            mOnProgressListener = new UpdateAgent.DialogProgress(IndexActivity.this);
        }
        new MyLoadAsyncTask().execute(infoBean.getApkUrl());
    }

     /* 异步任务，后台处理与更新UI */

    class MyLoadAsyncTask extends AsyncTask<String, String, String> {
      /* 后台线程 */

        @Override
        protected String doInBackground(String... params) {
            final String fileName = "幻幻.apk";
            File tmpFile = new File(IndexActivity.this.getExternalCacheDir() + "/huanhuan/");
            if (!tmpFile.exists()) {
                tmpFile.mkdir();
            }
            final File file = new File(IndexActivity.this.getExternalCacheDir()+ "/huanhuan/" + fileName);
      /* 所下载文件的URL */

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        /* URL属性设置 */
                conn.setRequestMethod("GET");
        /* URL建立连接 */

                conn.connect();
        /* 下载文件的大小 */
                int fileOfLength = conn.getContentLength();
        /* 每次下载的大小与总下载的大小 */

                int totallength = 0;
                int length = 0;
        /* 输入流 */
                InputStream in = conn.getInputStream();
        /* 输出流 */
                FileOutputStream out = new FileOutputStream(file);
        /* 缓存模式，下载文件 */
                byte[] buff = new byte[1024 * 1024];
                while ((length = in.read(buff)) > 0) {
                    totallength += length;
                    String str1 = "" + (int) ((totallength * 100) / fileOfLength);
                    publishProgress(str1);
                    out.write(buff, 0, length);
                }
        /* 关闭输入输出流 */
                in.close();
                out.flush();
                out.close();


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        /* 预处理UI线程 */
        @Override
        protected void onPreExecute() {
            // showDialog(0);
            mOnProgressListener.onStart();
            super.onPreExecute();
        }
    /* 结束时的UI线程 */

        @Override
        protected void onPostExecute(String result) {
            mOnProgressListener.onFinish();
            File newFile = new File(IndexActivity.this.getExternalCacheDir() + "/huanhuan/", "幻幻.apk");
            openFile(newFile);
            super.onPostExecute(result);
        }
    /* 处理UI线程，会被多次调用,触发事件为publicProgress方法 */

        @Override
        protected void onProgressUpdate(String... values) {
      /* 进度显示 */
            //pdialog.setProgress(Integer.parseInt(values[0]));
            mOnProgressListener.onProgress(Integer.parseInt(values[0]));
        }
    }

    //打开APK程序代码

    private void openFile(File file) {
        // TODO Auto-generated method stub
        Log.e("OpenFile", file.getName());
   /*Intent intent = new Intent();
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setAction(android.content.Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(file),
            "application/vnd.android.package-archive");*/

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        } else {
            Uri uri = null;
            try {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(IndexActivity.this,"com.cos.huanhuan.photos.fileprovider", file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                if(position == 2){
                    sharedPreferencesHelper = new SharedPreferencesHelper(IndexActivity.this);
                    userValueData = sharedPreferencesHelper.getObject("userData");//该信息只用于判断登录与否，用户信息还是重新获取
                    if(userValueData != null) {
                        if (AppStringUtils.isEmpty(userValueData.getRongToken()) || userValueData.getRongToken().equals("default")) {
                            AppToastMgr.shortToast(IndexActivity.this,"未登录");
                        } else {
                            if((RongIM.getInstance().getCurrentConnectionStatus() != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)){
                                reconnect(userValueData.getRongToken());
                            }
                        }
                    }

//                    index_title_bar.setVisibility(View.VISIBLE);
//                    index_title_bar.setBackgroundColor(getResources().getColor(R.color.white));
//                    index_title_bar.setDividerColor(R.color.dividLineColor);
//                    index_title_bar.setTitleColor(getResources().getColor(R.color.titleBarTextColor));
//                    index_title_bar.setTitle(getResources().getString(R.string.message));
                }else{
                    //index_title_bar.setVisibility(View.GONE);
                }
//                if (position == 0){
//                    //如果是第一个，即首页
//                    if (mBottomBarLayout.getCurrentItem() == position){
//                        //如果是在原来位置上点击,更换首页图标并播放旋转动画
//                        bottomBarItem.setIconSelectedResourceId(R.mipmap.tab_loading);//更换成加载图标
//                        bottomBarItem.setStatus(true);
//
//                        //播放旋转动画
//                        if (mRotateAnimation == null) {
//                            mRotateAnimation = new RotateAnimation(0, 360,
//                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                                    0.5f);
//                            mRotateAnimation.setDuration(800);
//                            mRotateAnimation.setRepeatCount(-1);
//                        }
//                        ImageView bottomImageView = bottomBarItem.getImageView();
//                        bottomImageView.setAnimation(mRotateAnimation);
//                        bottomImageView.startAnimation(mRotateAnimation);//播放旋转动画
//
//                        //模拟数据刷新完毕
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                bottomBarItem.setIconSelectedResourceId(R.mipmap.tab_home_selected);//更换成首页原来图标
//                                bottomBarItem.setStatus(true);//刷新图标
//                                cancelTabLoading(bottomBarItem);
//                            }
//                        },3000);
//                        return;
//                    }
//                }

                //如果点击了其他条目
                BottomBarItem bottomItem = mBottomBarLayout.getBottomItem(0);
                bottomItem.setIconSelectedResourceId(R.mipmap.tab_home_selected);//更换为原来的图标

                cancelTabLoading(bottomItem);//停止旋转动画
            }
        });

        mVpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
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
                            }
                        }
                    }

//                    index_title_bar.setVisibility(View.VISIBLE);
//                    index_title_bar.setBackgroundColor(getResources().getColor(R.color.white));
//                    index_title_bar.setDividerColor(R.color.dividLineColor);
//                    index_title_bar.setTitleColor(getResources().getColor(R.color.titleBarTextColor));
//                    index_title_bar.setTitle(getResources().getString(R.string.message));
                }else{
                    //index_title_bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
        private FragmentManager manager;
        public MyAdapter(FragmentManager fm) {
            super(fm);
            this.manager = fm;
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
            }

            @Override
            public void onSuccess(String s) {
                RongIM.getInstance().refreshUserInfoCache(new UserInfo(String.valueOf(userValueData.getId()),userValueData.getNickname(),Uri.parse(userValueData.getPortrait())));
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
            }
        });

    }
}
