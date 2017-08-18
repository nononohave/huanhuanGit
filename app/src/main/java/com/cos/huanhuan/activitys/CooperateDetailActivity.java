package com.cos.huanhuan.activitys;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.MyListView;
import com.cos.huanhuan.utils.ObservableScrollView;
import com.cos.huanhuan.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class CooperateDetailActivity extends BaseActivity implements ObservableScrollView.ScrollViewListener {

    private ObservableScrollView scrollView;
    private ImageView imageView,img_coop_share,img_coop_back;
    private MyListView listView;
    private RelativeLayout ll_titleBar_coop;
    private View divideLine;
    private int imageHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooperate_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        //setBaseContentView(R.layout.activity_cooperate_detail);
        initView();
        initListeners();
        initData();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.img_coopDetail);
        img_coop_share=(ImageView) findViewById(R.id.img_coop_share);
        img_coop_back=(ImageView) findViewById(R.id.img_coop_back);
        scrollView = (ObservableScrollView) findViewById(R.id.obscrollview_coopDetail);
        listView = (MyListView) findViewById(R.id.listView_coopDetail);
        ll_titleBar_coop = (RelativeLayout) findViewById(R.id.ll_titleBar_coop);
        divideLine = (View) findViewById(R.id.view_coop_divide);
        ll_titleBar_coop.setFocusable(true);
        ll_titleBar_coop.setFocusableInTouchMode(true);
        ll_titleBar_coop.requestFocus();
        ll_titleBar_coop.bringToFront();
    }

    private void initListeners() {
        // 获取顶部图片高度后，设置滚动监听
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
                imageHeight = imageView.getHeight();

                scrollView.setScrollViewListener(CooperateDetailActivity.this);
            }
        });
    }



    private void initData() {
        List<String> list = new ArrayList<>();
        list.add("111");
        list.add("222");
        list.add("111");
        list.add("222");
        list.add("111");
        list.add("222");
        list.add("111");
        list.add("222");
        list.add("111");
        list.add("222");
        list.add("111");
        list.add("222");
        list.add("111");
        list.add("222");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CooperateDetailActivity.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }

    /**
     * ScrollView滚动监听
     *
     * @param scrollView：滚动控件
     * @param x：x轴坐标
     * @param y：y轴坐标
     * @param oldx：上一个x轴坐标
     * @param oldy：上一个y轴坐标
     */
    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y <= 0) {
            divideLine.setVisibility(View.GONE);
            img_coop_back.setImageResource(R.mipmap.back_green);
            img_coop_share.setImageResource(R.mipmap.share);
            ll_titleBar_coop.setBackgroundColor(Color.argb( 0, 255, 255, 255));//AGB由相关工具获得，或者美工提供
        } else if (y > 0 && y <= imageHeight - DensityUtils.dip2px(CooperateDetailActivity.this,64)) {
            divideLine.setVisibility(View.GONE);
            img_coop_back.setImageResource(R.mipmap.back_green);
            img_coop_share.setImageResource(R.mipmap.share);
            float scale = (float) y / imageHeight;
            float alpha = (255 * scale);
            // 只是layout背景透明(仿知乎滑动效果)
            ll_titleBar_coop.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
        } else {
            divideLine.setVisibility(View.VISIBLE);
            img_coop_back.setImageResource(R.mipmap.nav_back);
            img_coop_share.setImageResource(R.mipmap.share_red);
            ll_titleBar_coop.setBackgroundColor(Color.argb( 255, 255, 255, 255));
        }
    }
}
