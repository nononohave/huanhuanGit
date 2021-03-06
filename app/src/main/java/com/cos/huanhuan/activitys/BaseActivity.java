package com.cos.huanhuan.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppACache;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppNetworkMgr;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.FastBlur;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yofi on 2017\8\3 0003.
 */

public class BaseActivity extends AppCompatActivity
{
    private TitleBar titleBar;
    private ImageView mCollectView;
    private boolean mIsSelected;
    public ViewGroup contentView;
    private String userId;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private AppManager appManager;
    private Handler handler;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titlebar);
        handler=new MyHandler();
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
        sharedPreferencesHelper = new SharedPreferencesHelper(BaseActivity.this);
        titleBar = (TitleBar) findViewById(R.id.title_bar);
        contentView=(ViewGroup) findViewById(R.id.base_contentview);
        appManager = AppManager.getAppManager();
    }

    public void setNoNetWOrkLayout(){
        View view = LayoutInflater.from(BaseActivity.this).inflate(R.layout.activity_no_network,null);
        contentView.addView(view);
    }
    public void toastErrorMsg(Context context, String errorMsg){
        Message message = new Message();
        Bundle data = new Bundle();
        data.putString("errorMsg",errorMsg);
        message.setData(data);
        handler.sendMessage(message);//发送message信息
        message.what = 1;//标志是哪个线程传数据
    }
    public void setNoData(){
        View view = LayoutInflater.from(BaseActivity.this).inflate(R.layout.activity_no_data,null);
        contentView.addView(view);
    }
    public void setNoCoupon(){
        View view = LayoutInflater.from(BaseActivity.this).inflate(R.layout.activity_no_coupon,null);
        contentView.addView(view);
    }
    public String getUserId(){
        UserValueData userValueData = sharedPreferencesHelper.getObject("userData");
        if(userValueData != null){
            userId = String.valueOf(userValueData.getId());
            if(AppStringUtils.isEmpty(userId)){
                AppToastMgr.shortToast(BaseActivity.this, "用户未登录");
                return "";
            }
        }else{
            appManager.finishActivity();
            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        return userId;
    }

    public void setBaseContentView(int res){
        contentView.addView(View.inflate(this, res, null));
    }

    public void setCenLayoutClickListener(View.OnClickListener l){
        titleBar.setOnClickListener(l);
    }
    public TitleBar getTitleBar(){
        return titleBar;
    }
    /**
     * 设置titlebar背景色
     * @param res
     */
    public void setTitleBarColor(int res){
        titleBar.setBackgroundColor(this.getResources().getColor(res));
    }

    /**
     * 设置左边按钮的文字
     * @param text
     */
    public void setLeftText(String text){
        titleBar.setLeftText(text);
    }

    /**
     * 设置左边的文字的颜色
     * @param res
     */
    public void setLeftTextColor(int res){
        titleBar.setLeftTextColor(this.getResources().getColor(res));
    }

    /**
     * 设置左边按钮的图片
     * @param res
     */
    public void setLeftImageResource(int res){
        titleBar.setLeftImageResource(res);
    }
    /**
     * 左边那妞点击事件
     * @param onClickListener
     */
    public void leftButtonClick(View.OnClickListener onClickListener) {
        titleBar.setLeftClickListener(onClickListener);
    }

    /**
     * 设置中间标题文字
     * @param text
     */
    public void setTitle(String text){
        titleBar.setTitle(text);
    }

    public TextView getCenTextView(){
        return titleBar.getCenTextView();
    }

    /**
     * 设置标题文字颜色
     * @param res
     */
    public void setTitleTextColor(int res){
        titleBar.setTitleColor(this.getResources().getColor(res));
    }

    /**
     * 设置副标题文字颜色
     * @param res
     */
    public void setSubTitleColor(int res){
        titleBar.setSubTitleColor(this.getResources().getColor(res));
    }

    /**
     * 设置分割线颜色
     * @param res
     */
    public void setDividerColor(int res){
        titleBar.setDividerColor(this.getResources().getColor(res));
    }

    /**
     * 设置右边按钮的文本颜色
     * @param res
     */
    public void setRightTextColor(int res){
        titleBar.setActionTextColor(this.getResources().getColor(res));
    }

    public void setRightImage(int res1, final int res2, final String text1, final String text2){
        mCollectView = (ImageView) titleBar.addAction(new TitleBar.ImageAction(res1) {
                @Override
                public void performAction(View view) {
                rightImageClick(mIsSelected, res2,text1,text2);
            }
        });
    }

    /**
     * 设置右边按钮图片点击变化图片以及文字
     * @param mIsSelected
     * @param res2
     * @param text1
     * @param text2
     */
    private void rightImageClick(Boolean mIsSelected, int res2, String text1, String text2) {
        mCollectView.setImageResource(res2);
        titleBar.setTitle(mIsSelected ? text1 : text2);
        mIsSelected = !mIsSelected;
    }

    /**
     * 设置右边按钮文字和事件
     */
    public void setRightButton(TitleBar.TextAction textAction) {
        titleBar.addAction(textAction);
    }

//    titleBar.setActionTextColor(Color.WHITE);
//    mCollectView = (ImageView) titleBar.addAction(new TitleBar.ImageAction(R.mipmap.collect) {
//    @Override
//    public void performAction(View view) {
//        Toast.makeText(MainActivity.this, "点击了收藏", Toast.LENGTH_SHORT).show();
//        mCollectView.setImageResource(R.mipmap.fabu);
//        titleBar.setTitle(mIsSelected ? "文章详情\n朋友圈" : "帖子详情");
//        mIsSelected = !mIsSelected;
//    }
//});
//
//        titleBar.addAction(new TitleBar.TextAction("发布") {
//    @Override
//    public void performAction(View view) {
//        Toast.makeText(MainActivity.this, "点击了发布", Toast.LENGTH_SHORT).show();
//    }
//});

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    class MyHandler extends Handler {
        //接受message的信息
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == 1) {
                Bundle data = msg.getData();
                String errorMsg = data.getString("errorMsg");
                AppToastMgr.shortToast(BaseActivity.this, errorMsg);
            }

        }
    }
}
