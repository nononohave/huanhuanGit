package com.cos.huanhuan.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.CardGridAdapter;
import com.cos.huanhuan.model.CardExchange;
import com.cos.huanhuan.model.ExchangeList;
import com.cos.huanhuan.model.ExchangeStatus;
import com.cos.huanhuan.model.SlidePhotos;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.FastBlur;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.SpacesItemDecoration;
import com.cos.huanhuan.views.TitleBar;
import com.foamtrace.photopicker.Image;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AllExchangeActivity extends BaseActivity implements AdapterView.OnItemClickListener,View.OnClickListener{

    private AppManager appManager;
    private PopViewListAdapter adapter;

    private RecyclerView recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager mLayoutManager;
    private CardGridAdapter cardGridAdapter;
    private int pageIndex = 0;
    private int pageNum = 6;
    private List<CardExchange> listCard;
    private List<ExchangeStatus> listExchangeStatus;
    private List<String> list;
    private int selectItem = 0;
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
        setTitle(this.getResources().getString(R.string.allExchange));

        setBaseContentView(R.layout.activity_all_exchange);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);

        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });

        final View popPulishView = LayoutInflater.from(this).inflate(R.layout.popwindow_publish, null);
        final ImageView backBlurImg = (ImageView)popPulishView.findViewById(R.id.back_blur_pop);
        final RelativeLayout rl = (RelativeLayout) popPulishView.findViewById(R.id.back_rl_blur);
        ImageView imageClose = (ImageView) popPulishView.findViewById(R.id.close_publish_popWindow);
        LinearLayout publishExchange = (LinearLayout) popPulishView.findViewById(R.id.ll_popWindow_publishExchange);
        LinearLayout publishCoo = (LinearLayout) popPulishView.findViewById(R.id.ll_popWindow_publishCoo);
        setRightButton(new TitleBar.TextAction(this.getResources().getString(R.string.publish)) {
            @Override
            public void performAction(View view) {
                ViewUtils.showPopupWindow(AllExchangeActivity.this,getCenTextView(),5,popPulishView);
                Bitmap scaledBitmap = FastBlur.doBlur(ViewUtils.takeScreenShot(AllExchangeActivity.this), 15, true);
                backBlurImg.setVisibility(View.VISIBLE);
                rl.bringToFront();
                backBlurImg.setImageBitmap(scaledBitmap);
            }
        });
        final TextView tv = getCenTextView();
        Drawable rightDrawable = getResources().getDrawable(R.mipmap.popdrowdown);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        tv.setCompoundDrawables(null, null, rightDrawable, null);
        tv.setCompoundDrawablePadding(10);

        final View popupView = LayoutInflater.from(this).inflate(R.layout.popwindow, null);
        ListView listView = (ListView)popupView.findViewById(R.id.popListView);
        list = new ArrayList<String>();
        list.add("全部兑换");
        adapter = new PopViewListAdapter(this,list);
        listView.setAdapter(adapter);
        setCenLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtils.showPopupWindow(AllExchangeActivity.this,getTitleBar(),4,popupView);
            }
        });
        listView.setOnItemClickListener(this);

        recyclerview = (RecyclerView) findViewById(R.id.grid_recycle_allExchanger);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_allExchange);

        mLayoutManager=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        recyclerview.setLayoutManager(mLayoutManager);
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        listCard = new ArrayList<CardExchange>();
        cardGridAdapter = new CardGridAdapter(this,listCard,new ArrayList<SlidePhotos>());
        recyclerview.setAdapter(cardGridAdapter);
        int leftRight = DensityUtils.dip2px(AllExchangeActivity.this,5);
        int topBottom = DensityUtils.dip2px(AllExchangeActivity.this,0);
        recyclerview.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom));
        initData();

        ExchangeList exChange = new ExchangeList();
        exChange.setEid("");
        getData(exChange);

        //点击事件
        imageClose.setOnClickListener(this);
        publishExchange.setOnClickListener(this);
        publishCoo.setOnClickListener(this);

        cardGridAdapter.setOnImageClick(new CardGridAdapter.OnImageClick() {
            @Override
            public void OnImageClick(View view, int position) {
                Intent intentExchange = new Intent(AllExchangeActivity.this, ExchangeDetailActivity.class);
                intentExchange.putExtra("exchangeId",listCard.get(position).getCardId());
                startActivity(intentExchange);
            }
        });
        //时间选择器
//        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
//            @Override
//            public void onTimeSelect(Date date, View v) {//选中事件回调
//                AppToastMgr.shortToast(AllExchangeActivity.this,date.toString());
//            }
//        })
//        .setType(new boolean[]{true, true, true, true, true, false})
//        .setCancelText("取消")//取消按钮文字
//        .setSubmitText("确定")//确认按钮文字
//        .setTitleText("选择时间")//标题文字
//        .setOutSideCancelable(true)
//        .setTitleSize(20)//标题文字大小
//        .setTitleColor(getResources().getColor(R.color.black))//标题文字颜色
//        .setSubmitColor(getResources().getColor(R.color.titleBarTextColor))//确定按钮文字颜色
//        .setCancelColor(getResources().getColor(R.color.titleBarTextColor))//取消按钮文字颜色
//        .setBgColor(getResources().getColor(R.color.white))
//        .build();
//        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
//        pvTime.show();
    }

    private void initData() {
        HttpRequest.getExchangeStatus(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(AllExchangeActivity.this,"获取状态请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    listExchangeStatus = new ArrayList<ExchangeStatus>();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject != null) {
                        JSONArray arr = jsonObject.getJSONArray("list");
                        for (int i = 0; i < arr.length(); i++) {
                            ExchangeStatus exchangeStatus = new ExchangeStatus();
                            String id = arr.getJSONObject(i).getString("id");
                            String examineName = arr.getJSONObject(i).getString("examineName");
                            String examineUsName = arr.getJSONObject(i).getString("examineUsName");
                            exchangeStatus.setExchangeStatusId(id);
                            exchangeStatus.setExchangeStatusName(examineName);
                            exchangeStatus.setExchangeStatusUS(examineUsName);
                            listExchangeStatus.add(exchangeStatus);
                        }
                        for (ExchangeStatus status : listExchangeStatus) {
                            list.add(status.getExchangeStatusName());
                        }
                        adapter.notifyDataSetChanged();
                    }else{
                        AppToastMgr.shortToast(AllExchangeActivity.this,"无数据！");
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
                if(listExchangeStatus != null && listExchangeStatus.size() > 0 && selectItem != 0){
                    exChange.setEid(listExchangeStatus.get(selectItem-1).getExchangeStatusId());
                }else{
                    exChange.setEid("");
                }
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
                    exChange.setPageIndex(pageIndex);
                    exChange.setPageSize(pageNum);
                    exChange.setCid("");
                    exChange.setSea("");
                    if(listExchangeStatus != null && listExchangeStatus.size() > 0 && selectItem != 0){
                        exChange.setEid(listExchangeStatus.get(selectItem-1).getExchangeStatusId());
                    }else{
                        exChange.setEid("");
                    }
                    HttpRequest.getExchangeList(exChange, new StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            AppToastMgr.shortToast(AllExchangeActivity.this,"请求失败！");
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
                                    AppToastMgr.shortToast(AllExchangeActivity.this, " 请求失败！原因：" + errorMsg);
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

    private void getData(ExchangeList exChange) {
        pageIndex = 1;
        exChange.setPageIndex(pageIndex);
        exChange.setPageSize(pageNum);
        exChange.setCid("");
        exChange.setSea("");
        HttpRequest.getExchangeList(exChange, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(AllExchangeActivity.this,"请求失败！");
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
                        AppToastMgr.shortToast(AllExchangeActivity.this, " 请求失败！原因：" + errorMsg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ExchangeList exChange = new ExchangeList();
        if(listExchangeStatus != null && listExchangeStatus.size() > 0 && position != 0){
            exChange.setEid(listExchangeStatus.get(position-1).getExchangeStatusId());
        }else{
            exChange.setEid("");
        }
        getData(exChange);
        ViewUtils.dismissPopup();
        selectItem = position;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close_publish_popWindow:
                ViewUtils.dismissPopup();
                break;
            case R.id.ll_popWindow_publishExchange:
                ViewUtils.dismissPopup();
                Intent intent = new Intent(AllExchangeActivity.this,PublishExchangeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_popWindow_publishCoo:
                ViewUtils.dismissPopup();
                Intent intentCoop = new Intent(AllExchangeActivity.this,PublishCoopActivity.class);
                startActivity(intentCoop);
                break;
        }
    }

    class PopViewListAdapter extends BaseAdapter{

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
}
