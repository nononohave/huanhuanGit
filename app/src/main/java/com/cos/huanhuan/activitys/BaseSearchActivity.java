package com.cos.huanhuan.activitys;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.views.TitleSearchBar;

/**
 * Created by yofi on 2017\8\3 0003.
 */

public class BaseSearchActivity extends FragmentActivity
{
    private TitleSearchBar titleBar;
    private ImageView mCollectView;
    private boolean mIsSelected;
    public ViewGroup contentView;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_index);
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

        titleBar = (TitleSearchBar) findViewById(R.id.title_search_bar);
        contentView=(ViewGroup) findViewById(R.id.base_search_contentview);

    }

    public void setBaseContentView(int res){
        contentView.addView(View.inflate(this, res, null));
    }

   /* *//**
     * 设置右边按钮的文字
     * @param text
     *//*
    public void setRightText(String text){
        titleBar.setRightText(text);
    }

    *//**
     * 设置右边的文字的颜色
     * @param res
     *//*
    public void setRightTextColor(int res){
        titleBar.setRightTextColor(this.getResources().getColor(res));
    }

    *//**
     * 右边那妞点击事件
     * @param onClickListener
     *//*
    public void setRightClickListener(View.OnClickListener onClickListener) {
        titleBar.setRightClickListener(onClickListener);
    }

    public String getSearchText(){
        return titleBar.getSearchText();
    }*/

    /**
     * 设置titlebar背景色
     * @param res
     */
    public void setTitleBarColor(int res){
        titleBar.setBackgroundColor(this.getResources().getColor(res));
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
