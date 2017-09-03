package com.cos.huanhuan.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cos.huanhuan.R;
import com.cos.huanhuan.activitys.AllExchangeActivity;
import com.cos.huanhuan.activitys.ExchangeDetailActivity;
import com.cos.huanhuan.activitys.LoginActivity;
import com.cos.huanhuan.activitys.PublishCoopActivity;
import com.cos.huanhuan.activitys.PublishExchangeActivity;
import com.cos.huanhuan.activitys.SearchActivity;
import com.cos.huanhuan.adapter.CardGridAdapter;
import com.cos.huanhuan.model.CardExchange;
import com.cos.huanhuan.model.Classify;
import com.cos.huanhuan.model.ExchangeList;
import com.cos.huanhuan.model.SlidePhotos;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.FastBlur;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.PicassoUtils;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.PublicView;
import com.cos.huanhuan.views.SpacesItemDecoration;
import com.cos.huanhuan.views.TitleBar;
import com.cos.huanhuan.views.TitleSearchBar;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class IndexFragment extends Fragment implements View.OnClickListener{

    private TitleSearchBar titleBar;
    private ImageView mCollectView;
    private boolean mIsSelected;
    public ViewGroup contentView;
    private TabLayout tabLayout;

    private RecyclerView recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager mLayoutManager;
    private CardGridAdapter cardGridAdapter;
    private int pageIndex = 0;
    private int pageNum = 4;
    private List<CardExchange> listCard;
    private List<Classify> listClassify;
    private int selectedTab = 0;
    private List<SlidePhotos> listSlides;
    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, Bitmap> mMemoryCache;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_index, container, false);
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

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };

        titleBar = (TitleSearchBar) getActivity().findViewById(R.id.title_search_bar);
        contentView=(ViewGroup) getActivity().findViewById(R.id.base_search_contentview);
        recyclerview = (RecyclerView) getActivity().findViewById(R.id.index_grid_recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.grid_swipe_refresh);

        mLayoutManager=new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        recyclerview.setLayoutManager(mLayoutManager);
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        listCard = new ArrayList<CardExchange>();
        listSlides = new ArrayList<>();

        cardGridAdapter = new CardGridAdapter(getActivity(),listCard,listSlides);
        recyclerview.setAdapter(cardGridAdapter);
        setHeader(recyclerview);
        int leftRight = DensityUtils.dip2px(getActivity(),5);
        int topBottom = DensityUtils.dip2px(getActivity(),0);
        recyclerview.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom));

        titleBar.setBackgroundColor(getResources().getColor(R.color.titleBarBack));
        titleBar.setDividerColor(getResources().getColor(R.color.dividLineColor));

        tabLayout = titleBar.getTabLayout();
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                //AppToastMgr.shortToast(getActivity(),"选中的"+tab.getText() + tab.getPosition());
                ExchangeList exChange = new ExchangeList();
                String searchText = titleBar.getEtText();
                if(listClassify != null && listClassify.size() > 0 && selectedTab != 0){
                    exChange.setCid(listClassify.get(selectedTab-1).getClassifyId());
                }else{
                    exChange.setCid("");
                }
                exChange.setEid("");
                exChange.setSea(searchText);
                getData(exChange);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //AppToastMgr.shortToast(getActivity(),"未选中的"+tab.getText());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //AppToastMgr.shortToast(getActivity(),"复选中的"+tab.getText() + tab.getPosition());
            }
        });

        final View popPulishView = LayoutInflater.from(getActivity()).inflate(R.layout.popwindow_publish, null);
        final ImageView backBlurImg = (ImageView)popPulishView.findViewById(R.id.back_blur_pop);
        final RelativeLayout rl = (RelativeLayout) popPulishView.findViewById(R.id.back_rl_blur);
        ImageView imageClose = (ImageView) popPulishView.findViewById(R.id.close_publish_popWindow);
        LinearLayout publishExchange = (LinearLayout) popPulishView.findViewById(R.id.ll_popWindow_publishExchange);
        LinearLayout publishCoo = (LinearLayout) popPulishView.findViewById(R.id.ll_popWindow_publishCoo);
        titleBar.setRightButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.showPopupWindow(getActivity(),titleBar,5,popPulishView);
                Bitmap scaledBitmap = FastBlur.doBlur(ViewUtils.takeScreenShot(getActivity()), 15, true);
                backBlurImg.setVisibility(View.VISIBLE);
                rl.bringToFront();
                backBlurImg.setImageBitmap(scaledBitmap);
            }
        });
        imageClose.setOnClickListener(this);
        publishExchange.setOnClickListener(this);
        publishCoo.setOnClickListener(this);
        titleBar.getEtSearch().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
                startActivity(intentSearch);
            }
        });
//        titleBar.etTextWatcherListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                ExchangeList exChange = new ExchangeList();
//                String searchText = titleBar.getEtText();
//                if(listClassify != null && listClassify.size() > 0 && selectedTab != 0){
//                    exChange.setCid(listClassify.get(selectedTab-1).getClassifyId());
//                }else{
//                    exChange.setCid("");
//                }
//                exChange.setEid("");
//                exChange.setSea(searchText);
//                getData(exChange);
//            }
//        });

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

        cardGridAdapter.setOnImageClick(new CardGridAdapter.OnImageClick() {
            @Override
            public void OnImageClick(View view, int position) {
                Intent intentExchange = new Intent(getActivity(), ExchangeDetailActivity.class);
                intentExchange.putExtra("exchangeId",listCard.get(position-1).getCardId());
                startActivity(intentExchange);
            }
        });
        cardGridAdapter.setOnUserClick(new CardGridAdapter.OnUserClick() {
            @Override
            public void OnUserClick(View view, int position) {

            }
        });
        initData();

        ExchangeList exChange = new ExchangeList();
        exChange.setCid("");
        exChange.setEid("");
        exChange.setSea("");
        getData(exChange);
    }

    private void initData() {
        listClassify = new ArrayList<Classify>();
        HttpRequest.getExchangeClass(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(getActivity(),"请求分类接口失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success) {
                        JSONArray arr = jsonObject.getJSONArray("list");
                        for (int i = 0; i < arr.length(); i++) {
                            Classify classify = new Classify();
                            String id = arr.getJSONObject(i).getString("id");
                            String className = arr.getJSONObject(i).getString("className");
                            String classUsName = arr.getJSONObject(i).getString("classUsName");
                            classify.setClassifyId(id);
                            classify.setClassName(className);
                            classify.setClassUsName(classUsName);
                            listClassify.add(classify);
                        }
                        addTabClass(listClassify);
                    }else{
                        AppToastMgr.shortToast(getActivity(), " 请求分类接口失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ExchangeList exChange = new ExchangeList();
                exChange.setCid("");
                if(listClassify != null && listClassify.size() > 0 && selectedTab != 0){
                    exChange.setCid(listClassify.get(selectedTab-1).getClassifyId());
                }else{
                    exChange.setCid("");
                }
                exChange.setSea("");
                getData(exChange);
            }
        });

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==cardGridAdapter.getItemCount()){
                    pageIndex  = pageIndex + 1;
                    ExchangeList exChange = new ExchangeList();
                    String searchText = titleBar.getEtText();
                    exChange.setPageIndex(pageIndex);
                    exChange.setPageSize(pageNum);
                    if(listClassify != null && listClassify.size() > 0 && selectedTab != 0){
                        exChange.setCid(listClassify.get(selectedTab-1).getClassifyId());
                    }else{
                        exChange.setCid("");
                    }
                    exChange.setEid("");
                    if(AppStringUtils.isNotEmpty(searchText)){
                        exChange.setSea(searchText);
                    }else{
                        exChange.setSea("");
                    }
                    HttpRequest.getExchangeList(exChange, new StringCallback() {
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
                                if(success) {
                                    JSONObject obj = jsonObject.getJSONObject("data");
                                    JSONArray arr = obj.getJSONArray("data");
                                    for (int i = 0; i < arr.length(); i++) {
                                        CardExchange cardExchange = new CardExchange();
                                        String id = arr.getJSONObject(i).getString("id");
                                        String title = arr.getJSONObject(i).getString("title");
                                        String official = arr.getJSONObject(i).getString("official");
                                        String nickname = arr.getJSONObject(i).getString("nickname");
                                        String cover = arr.getJSONObject(i).getString("cover");
                                        String portrait = arr.getJSONObject(i).getString("portrait");
                                        cardExchange.setCardId(id);
                                        cardExchange.setCardTitle(title);
                                        cardExchange.setOfficial(official);
                                        cardExchange.setCreateName(nickname);
                                        cardExchange.setCardImgUrl(HttpRequest.IMG_HUANHUAN_HOST + cover);
                                        cardExchange.setPortrait(portrait);
                                        listCard.add(cardExchange);
                                    }
                                    cardGridAdapter.notifyDataSetChanged();
                                }else{
                                    AppToastMgr.shortToast(getActivity(), " 请求失败！原因：" + errorMsg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem=layoutManager.findLastVisibleItemPosition();
            }
        });

        HttpRequest.getSlides(new StringCallback() {
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
                    if(success) {
                        JSONArray arr = jsonObject.getJSONArray("list");
                        for (int i = 0; i < arr.length(); i++) {
                            SlidePhotos slides = JsonUtils.fromJson(arr.get(i).toString(),SlidePhotos.class);
                            listSlides.add(slides);
                        }
                        cardGridAdapter.notifyDataSetChanged();
                    }else{
                        AppToastMgr.shortToast(getActivity(), " 请求失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addTabClass(List<Classify> listClassify) {
        tabLayout.addTab(tabLayout.newTab().setText("推荐"));
        for (Classify claffify:listClassify ) {
            tabLayout.addTab(tabLayout.newTab().setText(claffify.getClassName()));
        }
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                 PublicView.setIndicator(tabLayout,0,0);
                //PublicView.setIndicatorNew(getActivity(),tabLayout);
            }
        });
    }

    private void getData(ExchangeList exChange) {
        pageIndex = 1;
        exChange.setPageIndex(pageIndex);
        exChange.setPageSize(pageNum);
        HttpRequest.getExchangeList(exChange, new StringCallback() {
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
                    if(success) {
                        JSONObject obj = jsonObject.getJSONObject("data");
                        JSONArray arr = obj.getJSONArray("data");
                        listCard.removeAll(listCard);
                        for (int i = 0; i < arr.length(); i++) {
                            CardExchange cardExchange = new CardExchange();
                            String id = arr.getJSONObject(i).getString("id");
                            String title = arr.getJSONObject(i).getString("title");
                            String official = arr.getJSONObject(i).getString("official");
                            String nickname = arr.getJSONObject(i).getString("nickname");
                            String cover = arr.getJSONObject(i).getString("cover");
                            String portrait = arr.getJSONObject(i).getString("portrait");
                            cardExchange.setCardId(id);
                            cardExchange.setCardTitle(title);
                            cardExchange.setOfficial(official);
                            cardExchange.setCreateName(nickname);
                            cardExchange.setCardImgUrl(HttpRequest.IMG_HUANHUAN_HOST + cover);
                            cardExchange.setPortrait(portrait);
                            listCard.add(cardExchange);
                        }
                        cardGridAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        AppToastMgr.shortToast(getActivity(), " 请求失败！原因：" + errorMsg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private View.OnClickListener mTabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int pos = (int) view.getTag();
            if (pos == 0) {
                AppToastMgr.shortToast(getActivity(), "您还没有登录");
                //TODO 跳转到登录界面
            } else {
//                AppToastMgr.shortToast(getActivity(), "选中 " + pos);
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

    private void setHeader(RecyclerView view) {
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.head_scroll_img, view, false);
        cardGridAdapter.setHeaderView(header);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_publish_popWindow:
                ViewUtils.dismissPopup();
                break;
            case R.id.ll_popWindow_publishExchange:
                ViewUtils.dismissPopup();
                Intent intent = new Intent(getActivity(), PublishExchangeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_popWindow_publishCoo:
                ViewUtils.dismissPopup();
                Intent intentCoop = new Intent(getActivity(), PublishCoopActivity.class);
                startActivity(intentCoop);
                break;
        }
    }
}
