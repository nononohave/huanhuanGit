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
import com.cos.huanhuan.adapter.CardGridAdapter;
import com.cos.huanhuan.adapter.MyCouponAdapter;
import com.cos.huanhuan.model.Comment;
import com.cos.huanhuan.model.CommentDTO;
import com.cos.huanhuan.model.Coupon;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.views.TitleBar;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CouponActivity extends BaseActivity{

    public static final int SELECTED_COUPON = 555;
    private AppManager appManager;
    private String userId;
    private RecyclerView recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager mLayoutManager;
    private int pageIndex = 0;
    private int pageNum = 6;
    private List<Coupon> listCoupon;
    private MyCouponAdapter adapter;
    private Boolean isChoose;
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
        setBaseContentView(R.layout.activity_coupon);
        setTitle(this.getResources().getString(R.string.person_coupon));
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        userId = getUserId();
        isChoose = getIntent().getExtras().getBoolean("isChoose");
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        setRightButton(new TitleBar.TextAction(this.getResources().getString(R.string.add)) {
            @Override
            public void performAction(View view) {
                Intent intentAdd = new Intent(CouponActivity.this,AddNewCouponActivity.class);
                startActivity(intentAdd);
            }
        });
        initView();
        initData();
    }

    private void initView() {

        recyclerview = (RecyclerView) findViewById(R.id.grid_recycle_coupon);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_coupon);

        mLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        recyclerview.setLayoutManager(mLayoutManager);
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        listCoupon = new ArrayList<>();
        adapter = new MyCouponAdapter(CouponActivity.this,listCoupon);
        recyclerview.setAdapter(adapter);

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
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==adapter.getItemCount()){
                    pageIndex  = pageIndex + 1;
                    HttpRequest.getCoupon(userId, String.valueOf(pageIndex), String.valueOf(pageNum), new StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            toastErrorMsg(CouponActivity.this,"请求失败！");
                        }

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Boolean success = jsonObject.getBoolean("success");
                                String errorMsg = jsonObject.getString("errorMsg");
                                if(success) {
                                    JSONObject obj =jsonObject.getJSONObject("data");
                                    JSONArray arr =obj.getJSONArray("data");
                                    for (int i = 0; i < arr.length(); i++) {
                                        Coupon coupon = JsonUtils.fromJson(arr.get(i).toString(), Coupon.class);
                                        listCoupon.add(coupon);
                                    }
                                    adapter.notifyDataSetChanged();
                                }else{
                                    toastErrorMsg(CouponActivity.this, " 请求失败！原因：" + errorMsg);
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

        adapter.setUseCouponClick(new MyCouponAdapter.UseCouponClick() {
            @Override
            public void useCouponClick(View view, int position) {
                if(isChoose) {
                    if(listCoupon.get(position).getValid()) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("couponId", listCoupon.get(position).getId());
                        setResult(SELECTED_COUPON, returnIntent);
                        appManager.finishActivity();
                    }else{
                        toastErrorMsg(CouponActivity.this, "该优惠券已过期");
                    }
                }else{
                    Intent intentExchange = new Intent(CouponActivity.this,AllExchangeActivity.class);
                    startActivity(intentExchange);
                }
            }
        });

        adapter.setOnItemClick(new MyCouponAdapter.OnItemClick() {
            @Override
            public void OnItemClick(View view, int position) {
                if(isChoose) {
                    if(listCoupon.get(position).getValid()) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("couponId", listCoupon.get(position).getId());
                        setResult(SELECTED_COUPON, returnIntent);
                        appManager.finishActivity();
                    }else{
                        toastErrorMsg(CouponActivity.this, "该优惠券已过期");
                    }
                }else{
                    toastErrorMsg(CouponActivity.this, "no选择");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        pageIndex = 1;
        HttpRequest.getCoupon(userId, String.valueOf(pageIndex), String.valueOf(pageNum), new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                toastErrorMsg(CouponActivity.this,"请求失败！");
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response) {
               try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success) {
                        listCoupon.removeAll(listCoupon);
                        JSONObject obj =jsonObject.getJSONObject("data");
                        JSONArray arr =obj.getJSONArray("data");
                        if(arr.length() > 0) {
                            for (int i = 0; i < arr.length(); i++) {
                                Coupon coupon = JsonUtils.fromJson(arr.get(i).toString(), Coupon.class);
                                listCoupon.add(coupon);
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            setNoCoupon();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        toastErrorMsg(CouponActivity.this, " 请求失败！原因：" + errorMsg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
