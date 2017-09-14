package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.MyExchangeAdapter;
import com.cos.huanhuan.model.MyExchange;
import com.cos.huanhuan.model.PersonCoop;
import com.cos.huanhuan.model.PersonPublish;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.views.SpacesItemDecoration;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyExchangeActivity extends BaseActivity {

    private AppManager appManager;
    private String userId;
    private RecyclerView recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager mLayoutManager;
    private int pageIndex = 1;
    private int pageSize = 5;
    private List<MyExchange> listMyExchange;
    private MyExchangeAdapter adapterMyExchange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        setTitleBarColor(R.color.white);
        setLeftImageResource(R.mipmap.nav_back);
        setDividerColor(R.color.dividLineColor);
        setRightTextColor(R.color.titleBarTextColor);
        setTitleTextColor(R.color.titleBarTextColor);
        setTitle(this.getResources().getString(R.string.my_exchange));
        setBaseContentView(R.layout.activity_my_exchange);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        userId = getUserId();
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        initView();
        initData();
    }

    private void initView() {
        recyclerview = (RecyclerView) findViewById(R.id.grid_recycle_myExchange);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_myExchange);
        mLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        recyclerview.setLayoutManager(mLayoutManager);
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        listMyExchange = new ArrayList<>();
        adapterMyExchange = new MyExchangeAdapter(MyExchangeActivity.this,listMyExchange,true);
        recyclerview.setAdapter(adapterMyExchange);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapterMyExchange.getItemCount()) {
                    pageIndex = pageIndex + 1;
                    HttpRequest.getMyExchanges(userId, String.valueOf(pageIndex), String.valueOf(pageSize),"1", new StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            toastErrorMsg(MyExchangeActivity.this, "请求失败！");
                        }

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Boolean success = jsonObject.getBoolean("success");
                                String errorMsg = jsonObject.getString("errorMsg");
                                if (success) {
                                    JSONObject obj = jsonObject.getJSONObject("data");
                                    JSONArray arr = obj.getJSONArray("data");
                                    for (int i = 0; i < arr.length(); i++) {
                                        MyExchange myExchange = JsonUtils.fromJson(arr.get(i).toString(), MyExchange.class);
                                        listMyExchange.add(myExchange);
                                    }
                                    adapterMyExchange.notifyDataSetChanged();
                                } else {
                                    toastErrorMsg(MyExchangeActivity.this, " 请求失败！原因：" + errorMsg);
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

        adapterMyExchange.setViewTracking(new MyExchangeAdapter.ViewTrackingClick() {
            @Override
            public void viewTrackingClick(View view, int position) {
                Intent intentTracking = new Intent(MyExchangeActivity.this,ViewTrackingActivity.class);
                intentTracking.putExtra("trackingId",listMyExchange.get(position).getLogisticCode());
                intentTracking.putExtra("imgUrl",listMyExchange.get(position).getCover());
                startActivity(intentTracking);
            }
        });
    }
    private void initData() {
        pageIndex = 1;
        HttpRequest.getMyExchanges(userId, String.valueOf(pageIndex), String.valueOf(pageSize), "1",new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                toastErrorMsg(MyExchangeActivity.this, "请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    listMyExchange.removeAll(listMyExchange);
                    if (success) {
                        JSONObject obj = jsonObject.getJSONObject("data");
                        if(obj.getInt("totalRecord") != 0){
                            JSONArray arr = obj.getJSONArray("data");
                            for (int i = 0; i < arr.length(); i++) {
                                MyExchange myExchange = JsonUtils.fromJson(arr.get(i).toString(), MyExchange.class);
                                listMyExchange.add(myExchange);
                            }
                            adapterMyExchange.notifyDataSetChanged();
                        }else{
                            setNoData();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        toastErrorMsg(MyExchangeActivity.this, " 请求失败！原因：" + errorMsg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
