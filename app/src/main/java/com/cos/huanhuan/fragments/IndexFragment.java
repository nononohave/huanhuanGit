package com.cos.huanhuan.fragments;

import android.graphics.Color;
import android.graphics.Picture;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.views.AutoScrollViewPager;
import com.cos.huanhuan.views.PublicView;
import com.cos.huanhuan.views.TitleSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class IndexFragment extends Fragment{

    private TitleSearchBar titleBar;
    private ImageView mCollectView;
    private boolean mIsSelected;
    public ViewGroup contentView;
    private TabLayout tabLayout;
    private AutoScrollViewPager scrollImg_index;
    private String[] paths = {"https://ss3.baidu.com/-fo3dSag_xI4khGko9WTAnF6hhy/image/h%3D200/sign=c493b482b47eca800d053ee7a1229712/8cb1cb1349540923abd671df9658d109b2de49d7.jpg",
            "https://ss0.baidu.com/94o3dSag_xI4khGko9WTAnF6hhy/image/h%3D200/sign=45fbfa5555da81cb51e684cd6267d0a4/2f738bd4b31c8701491ea047237f9e2f0608ffe3.jpg",
            "https://ss2.baidu.com/-vo3dSag_xI4khGko9WTAnF6hhy/image/h%3D200/sign=ae0e95c0fc1986185e47e8847aec2e69/0b46f21fbe096b63eb314ef108338744ebf8ac62.jpg",
            "https://ss3.baidu.com/9fo3dSag_xI4khGko9WTAnF6hhy/image/h%3D200/sign=1fad2b46952397ddc9799f046983b216/dc54564e9258d109c94bbb13d558ccbf6d814de2.jpg",
            "https://ss1.baidu.com/9vo3dSag_xI4khGko9WTAnF6hhy/image/h%3D200/sign=ff0999f6d4160924c325a51be406359b/86d6277f9e2f070861ccd4a0ed24b899a801f241.jpg"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_search_titlebar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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

        titleBar = (TitleSearchBar) getActivity().findViewById(R.id.title_search_bar);
        contentView=(ViewGroup) getActivity().findViewById(R.id.base_search_contentview);
        scrollImg_index = (AutoScrollViewPager) getActivity().findViewById(R.id.scrollImg_index);
        titleBar.setBackgroundColor(getResources().getColor(R.color.titleBarBack));
        titleBar.setDividerColor(getResources().getColor(R.color.dividLineColor));
        tabLayout = titleBar.getTabLayout();


        tabLayout.addTab(tabLayout.newTab().setText("推荐"));
        tabLayout.addTab(tabLayout.newTab().setText("古风"));
        tabLayout.addTab(tabLayout.newTab().setText("游戏"));
        tabLayout.addTab(tabLayout.newTab().setText("动漫"));
        tabLayout.addTab(tabLayout.newTab().setText("综合"));
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                PublicView.setIndicator(tabLayout,0,0);
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                AppToastMgr.shortToast(getActivity(),"选中的"+tab.getText() + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                AppToastMgr.shortToast(getActivity(),"未选中的"+tab.getText());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                AppToastMgr.shortToast(getActivity(),"复选中的"+tab.getText() + tab.getPosition());
            }
        });
        titleBar.setRightButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = titleBar.getEtText();
                AppToastMgr.shortToast(getActivity(),"发布" + text);
            }
        });
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                if (tab.getCustomView() != null) {
                    View tabView = (View) tab.getCustomView().getParent();
                    tabView.setTag(i);
                    tabView.setOnClickListener(mTabOnClickListener);
                }
            }
        }
    }

    private View.OnClickListener mTabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int pos = (int) view.getTag();
            if (pos == 0) {
                AppToastMgr.shortToast(getActivity(), "您还没有登录");
                //TODO 跳转到登录界面
            } else {
                AppToastMgr.shortToast(getActivity(), "选中 " + pos);
                TabLayout.Tab tab = tabLayout.getTabAt(pos);
                if (tab != null) {
                    tab.select();
                }
            }
        }
    };

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
