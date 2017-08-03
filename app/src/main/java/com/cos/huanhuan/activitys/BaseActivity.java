package com.cos.huanhuan.activitys;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.views.TitleBar;

/**
 * Created by yofi on 2017\8\3 0003.
 */

public class BaseActivity extends AppCompatActivity
{
    private TitleBar titleBar;
    private ImageView mCollectView;
    private boolean mIsSelected;
    public ViewGroup contentView;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titlebar);
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

        titleBar = (TitleBar) findViewById(R.id.title_bar);
        contentView=(ViewGroup) findViewById(R.id.base_contentview);


    }

    public void setBaseContentView(int res){
        contentView.addView(View.inflate(this, res, null));
    }

    /**
     * 设置titlebar背景色
     * @param res
     */
    public void setTitleBarColor(int res){
        titleBar.setBackgroundColor(res);
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
        titleBar.setLeftTextColor(res);
    }

    /**
     * 设置左边按钮的图片
     * @param res
     */
    public void setLeftImageResource(int res){
        titleBar.setLeftImageResource(res);
    }

    /**
     * 点击左边按钮的点击事件
     */
    public void onClickLeftButton() {

    }

    /**
     * 设置中间标题文字
     * @param text
     */
    public void setTitle(String text){
        titleBar.setTitle(text);
    }

    /**
     * 设置标题文字颜色
     * @param res
     */
    public void setTitleTextColor(int res){
        titleBar.setTitleColor(res);
    }

    /**
     * 设置副标题文字颜色
     * @param res
     */
    public void setSubTitleColor(int res){
        titleBar.setSubTitleColor(res);
    }

    /**
     * 设置分割线颜色
     * @param res
     */
    public void setDividerColor(int res){
        titleBar.setDividerColor(res);
    }

    /**
     * 设置右边按钮的文本颜色
     * @param res
     */
    public void setRightTextColor(int res){
        titleBar.setActionTextColor(res);
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
     * 设置右边按钮文字
     * @param text
     */
    public void setRightButton(String text){
        titleBar.addAction(new TitleBar.TextAction(text) {
            @Override
            public void performAction(View view) {
                rightButtonClick();
            }
        });
    }

    /**
     * 右边按钮点击事件
     */
    private void rightButtonClick() {

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
}
