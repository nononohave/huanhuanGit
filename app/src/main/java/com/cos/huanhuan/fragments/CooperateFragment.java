package com.cos.huanhuan.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.cos.huanhuan.R;
import com.cos.huanhuan.activitys.CooperateDetailActivity;
import com.cos.huanhuan.activitys.LoginActivity;
import com.cos.huanhuan.activitys.PublishCoopActivity;
import com.cos.huanhuan.adapter.CardGridAdapter;
import com.cos.huanhuan.adapter.CoopCardGridAdapter;
import com.cos.huanhuan.model.CardCoop;
import com.cos.huanhuan.model.CardExchange;
import com.cos.huanhuan.model.CoopList;
import com.cos.huanhuan.model.ExchangeList;
import com.cos.huanhuan.model.PublishCoop;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.views.SpacesItemDecoration;
import com.cos.huanhuan.views.TitleBar;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CooperateFragment extends Fragment {

    private TitleBar titleBar;
    private ViewGroup contentView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager mLayoutManager;

    private CoopCardGridAdapter coopCardGridAdapter;
    private List<CardCoop> listCardCoop;
    private int pageIndex = 0;
    private int pageNum = 6;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cooperate, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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
        titleBar = (TitleBar) getActivity().findViewById(R.id.coop_title_bar);

        contentView = (ViewGroup) getActivity().findViewById(R.id.base_coop_contentView);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.coop_grid_recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.grid_swipe_coop_refresh);
        mLayoutManager=new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        recyclerView.setLayoutManager(mLayoutManager);
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        int leftRight = DensityUtils.dip2px(getActivity(),5);
        int topBottom = DensityUtils.dip2px(getActivity(),0);
        recyclerView.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom));

        //设置标题
        titleBar.setBackgroundColor(getResources().getColor(R.color.white));
        titleBar.setDividerColor(R.color.dividLineColor);
        titleBar.setActionTextColor(getResources().getColor(R.color.titleBarTextColor));
        titleBar.setTitleColor(getResources().getColor(R.color.titleBarTextColor));
        titleBar.setTitle(this.getResources().getString(R.string.cooperate));
        titleBar.addAction(new TitleBar.TextAction(this.getResources().getString(R.string.publish)) {
            @Override
            public void performAction(View view) {
                Intent intent = new Intent(getActivity(), PublishCoopActivity.class);
                startActivity(intent);
            }
        });
        listCardCoop = new ArrayList<CardCoop>();
        coopCardGridAdapter = new CoopCardGridAdapter(getActivity(),listCardCoop);
        recyclerView.setAdapter(coopCardGridAdapter);

        coopCardGridAdapter.setOnImageClick(new CoopCardGridAdapter.OnImageClick() {
            @Override
            public void OnImageClick(View view, int position) {
                AppToastMgr.shortToast(getActivity(),"选中"+position);
                Intent intent = new Intent(getActivity(), CooperateDetailActivity.class);
                intent.putExtra("coopId",listCardCoop.get(position).getCardId());//传入合作id
                startActivity(intent);
            }
        });
        coopCardGridAdapter.setOnUserClick(new CoopCardGridAdapter.OnUserClick() {
            @Override
            public void OnUserClick(View view, int position) {
                AppToastMgr.shortToast(getActivity(),"选中"+position);
            }
        });
        initData();
        getData();
    }

    private void initData() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==coopCardGridAdapter.getItemCount()) {
                    getDataAdd();
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
    }

    private void getData() {
        CoopList coopList = new CoopList();
        pageIndex = 1;
        coopList.setPageIndex(pageIndex);
        coopList.setPageSize(pageNum);
        HttpRequest.getCooperateList(coopList, new StringCallback() {
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
                        listCardCoop.removeAll(listCardCoop);
                        for (int i = 0; i < arr.length(); i++) {
                            CardCoop cardCoop = new CardCoop();
                            String id = arr.getJSONObject(i).getString("id");
                            String title = arr.getJSONObject(i).getString("title");
                            String nickname = arr.getJSONObject(i).getString("nickname");
                            String cover = arr.getJSONObject(i).getString("cover");
                            int userId = arr.getJSONObject(i).getInt("userId");
                            String city = arr.getJSONObject(i).getString("city");
                            int personNum = arr.getJSONObject(i).getInt("personNum");
                            String portrait = arr.getJSONObject(i).getString("portrait");
                            cardCoop.setPortrait(portrait);
                            cardCoop.setCardId(id);
                            cardCoop.setCardTitle(title);
                            cardCoop.setCreateName(nickname);
                            cardCoop.setCardImgUrl(HttpRequest.IMG_HUANHUAN_HOST + cover);
                            cardCoop.setUserId(userId);
                            cardCoop.setAddress(city);
                            cardCoop.setPersonNum(personNum);
                            listCardCoop.add(cardCoop);
                        }
                        coopCardGridAdapter.notifyDataSetChanged();
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

    private void getDataAdd() {
        CoopList coopList = new CoopList();
        pageIndex = pageIndex + 1;
        coopList.setPageIndex(pageIndex);
        coopList.setPageSize(pageNum);
        HttpRequest.getCooperateList(coopList, new StringCallback() {
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
                            CardCoop cardCoop = new CardCoop();
                            String id = arr.getJSONObject(i).getString("id");
                            String title = arr.getJSONObject(i).getString("title");
                            String nickname = arr.getJSONObject(i).getString("nickname");
                            String cover = arr.getJSONObject(i).getString("cover");
                            int userId = arr.getJSONObject(i).getInt("userId");
                            String city = arr.getJSONObject(i).getString("city");
                            int personNum = arr.getJSONObject(i).getInt("personNum");
                            String portrait = arr.getJSONObject(i).getString("portrait");
                            cardCoop.setPortrait(portrait);
                            cardCoop.setCardId(id);
                            cardCoop.setCardTitle(title);
                            cardCoop.setCreateName(nickname);
                            cardCoop.setCardImgUrl(HttpRequest.IMG_HUANHUAN_HOST + cover);
                            cardCoop.setUserId(userId);
                            cardCoop.setAddress(city);
                            cardCoop.setPersonNum(personNum);
                            listCardCoop.add(cardCoop);
                        }
                        coopCardGridAdapter.notifyDataSetChanged();
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

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
