package com.cos.huanhuan.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.CardGridAdapter;
import com.cos.huanhuan.model.CardExchange;
import com.cos.huanhuan.model.ExchangeList;
import com.cos.huanhuan.model.ExchangeStatus;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.FastBlur;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.SpacesItemDecoration;
import com.cos.huanhuan.views.TitleBar;
import com.cos.huanhuan.views.TitleSearchBarSecond;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements View.OnClickListener {

    private AppManager appManager;
    private SearchResultActivity.PopViewListAdapter adapter;

    private RecyclerView recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager mLayoutManager;
    private CardGridAdapter cardGridAdapter;
    private int pageIndex = 0;
    private int pageNum = 6;
    private List<CardExchange> listCard;
    private int selectItem = 0;
    private String sea;
    private TitleSearchBarSecond titleSearchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
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
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        sea = getIntent().getExtras().getString("sea");
        recyclerview = (RecyclerView) findViewById(R.id.grid_recycle_allExchanger);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_allExchange);
        titleSearchBar = (TitleSearchBarSecond) findViewById(R.id.title_search_all_bar);
        mLayoutManager=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        recyclerview.setLayoutManager(mLayoutManager);
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        listCard = new ArrayList<CardExchange>();
        cardGridAdapter = new CardGridAdapter(this,listCard);
        recyclerview.setAdapter(cardGridAdapter);
        int leftRight = DensityUtils.dip2px(SearchResultActivity.this,5);
        int topBottom = DensityUtils.dip2px(SearchResultActivity.this,0);
        recyclerview.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom));
        titleSearchBar.setEtText(sea);
        titleSearchBar.setRightVisiable(0);
        initData();
        getData(titleSearchBar.getEtText());
        titleSearchBar.setLeftButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        cardGridAdapter.setOnImageClick(new CardGridAdapter.OnImageClick() {
            @Override
            public void OnImageClick(View view, int position) {
                Intent intentExchange = new Intent(SearchResultActivity.this, ExchangeDetailActivity.class);
                intentExchange.putExtra("exchangeId",listCard.get(position).getCardId());
                startActivity(intentExchange);
            }
        });
    }

    private void initData() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(titleSearchBar.getEtText());
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
                    exChange.setPageIndex(pageIndex);
                    exChange.setPageSize(pageNum);
                    exChange.setCid("");
                    exChange.setSea(titleSearchBar.getEtText());
                    exChange.setEid("");
                    HttpRequest.getExchangeList(exChange, new StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            AppToastMgr.shortToast(SearchResultActivity.this,"请求失败！");
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
                                        cardExchange.setCardId(id);
                                        cardExchange.setCardTitle(title);
                                        cardExchange.setOfficial(official);
                                        cardExchange.setCreateName(nickname);
                                        cardExchange.setCardImgUrl(HttpRequest.IMG_HUANHUAN_HOST + cover);
                                        listCard.add(cardExchange);
                                    }
                                    cardGridAdapter.notifyDataSetChanged();
                                }else{
                                    AppToastMgr.shortToast(SearchResultActivity.this, " 请求失败！原因：" + errorMsg);
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
    }

    private void getData(String sea) {
        pageIndex = 1;
        ExchangeList exChange = new ExchangeList();
        exChange.setPageIndex(pageIndex);
        exChange.setPageSize(pageNum);
        exChange.setCid("");
        exChange.setEid("");
        exChange.setSea(sea);
        HttpRequest.getExchangeList(exChange, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(SearchResultActivity.this,"请求失败！");
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
                            cardExchange.setCardId(id);
                            cardExchange.setCardTitle(title);
                            cardExchange.setOfficial(official);
                            cardExchange.setCreateName(nickname);
                            cardExchange.setCardImgUrl(HttpRequest.IMG_HUANHUAN_HOST + cover);
                            listCard.add(cardExchange);
                        }
                        cardGridAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        AppToastMgr.shortToast(SearchResultActivity.this, " 请求失败！原因：" + errorMsg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_popWindow_publishExchange:
                ViewUtils.dismissPopup();
                Intent intent = new Intent(SearchResultActivity.this,PublishExchangeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_popWindow_publishCoo:
                ViewUtils.dismissPopup();
                Intent intentCoop = new Intent(SearchResultActivity.this,PublishCoopActivity.class);
                startActivity(intentCoop);
                break;
        }
    }

    class PopViewListAdapter extends BaseAdapter {

        private Context context;
        private List<String> list;

        public PopViewListAdapter(Context context,List<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.pop_list_item, null);
            }
            TextView tv = (TextView)convertView.findViewById(R.id.popWindowText);
            tv.setText(list.get(position));
            return convertView;
        }
    }
    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
