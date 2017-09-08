package com.cos.huanhuan.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.PersonCoopAdapter;
import com.cos.huanhuan.adapter.PersonExchangeAdapter;
import com.cos.huanhuan.model.PersonCoop;
import com.cos.huanhuan.model.PersonPublish;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.views.PublicView;
import com.cos.huanhuan.views.SpacesItemDecoration;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersonPublishActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private AppManager appManager;
    private FrameLayout ll_person_publish;
    private ImageView publish_back;
    private TabLayout publish_tabLayout;
    private List<PersonPublish> listPublish;
    private List<PersonCoop> listCoop;
    private RecyclerView recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager mLayoutManager;
    private PersonExchangeAdapter adapterExchange;
    private PersonCoopAdapter adapterCoop;
    private int pageIndex = 1;
    private int pageIndexCoop = 1;
    private int pageSize = 5;
    private String userId;
    private int selectedTab = 0;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_publish);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        handler=new MyHandler();
        userId = getUserId();
        initView();
        initData(0);
    }

    private void initView() {
        ll_person_publish = (FrameLayout) findViewById(R.id.ll_person_publish);
        publish_back = (ImageView) findViewById(R.id.img_person_publish_back);
        publish_tabLayout = (TabLayout) findViewById(R.id.tl_person_publish_tabLayout);
        recyclerview = (RecyclerView) findViewById(R.id.grid_recycle_personPublish);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_personPublish);
        mLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        recyclerview.setLayoutManager(mLayoutManager);
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        publish_tabLayout.setTabMode(TabLayout.MODE_FIXED);
        publish_tabLayout.addTab(publish_tabLayout.newTab().setText("发布的兑换"));
        publish_tabLayout.addTab(publish_tabLayout.newTab().setText("发布的合作"));
        publish_tabLayout.post(new Runnable() {
            @Override
            public void run() {
                PublicView.setIndicator(publish_tabLayout,20,20);
            }
        });
        publish_back.setOnClickListener(this);
        listPublish = new ArrayList<>();
        adapterExchange = new PersonExchangeAdapter(PersonPublishActivity.this,listPublish);
        recyclerview.setAdapter(adapterExchange);

        listCoop = new ArrayList<>();
        adapterCoop = new PersonCoopAdapter(PersonPublishActivity.this,listCoop);

        publish_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                if(tab.getPosition() == 0){
                    recyclerview.setAdapter(adapterExchange);
                    initData(0);
                }else{
                    recyclerview.setAdapter(adapterCoop);
                    initData(1);
                }
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(selectedTab == 0){
                    initData(0);
                }else{
                    initData(1);
                }
            }
        });

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if(selectedTab == 0) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapterExchange.getItemCount()) {
                        pageIndex = pageIndex + 1;
                        HttpRequest.getPersonExchange(userId, String.valueOf(pageIndex), String.valueOf(pageSize), new StringCallback() {
                            @Override
                            public void onError(Request request, Exception e) {
                                AppToastMgr.shortToast(PersonPublishActivity.this, "请求失败！");
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
                                            PersonPublish personPublish = JsonUtils.fromJson(arr.get(i).toString(), PersonPublish.class);
                                            listPublish.add(personPublish);
                                        }
                                        adapterExchange.notifyDataSetChanged();
                                    } else {
                                        AppToastMgr.shortToast(PersonPublishActivity.this, " 请求失败！原因：" + errorMsg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }else{
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapterCoop.getItemCount()) {
                        pageIndexCoop = pageIndexCoop + 1;
                        HttpRequest.getPersonCoop(userId, String.valueOf(pageIndexCoop), String.valueOf(pageSize), new StringCallback() {
                            @Override
                            public void onError(Request request, Exception e) {
                                AppToastMgr.shortToast(PersonPublishActivity.this, "请求失败！");
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
                                            PersonCoop personCoop = JsonUtils.fromJson(arr.get(i).toString(), PersonCoop.class);
                                            listCoop.add(personCoop);
                                        }
                                        adapterCoop.notifyDataSetChanged();
                                    } else {
                                        AppToastMgr.shortToast(PersonPublishActivity.this, " 请求失败！原因：" + errorMsg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
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

        //删除监听器
        adapterExchange.setDeleteClick(new PersonExchangeAdapter.DeleteClick() {
            @Override
            public void deleteClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonPublishActivity.this);
                builder.setTitle("确认删除兑换吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePublishExchange(position);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //同意操作
        adapterExchange.setAgreeClick(new PersonExchangeAdapter.AgreeClick() {
            @Override
            public void agreeClick(View view, final int position) {
                HttpRequest.refuseOrAgreeCoop(String.valueOf(listPublish.get(position).getId()), "同意", new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        AppToastMgr.shortToast(PersonPublishActivity.this,"请求失败！");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            if (null != response.cacheResponse()) {
                                String str = response.cacheResponse().toString();
                            } else {
                                try {
                                    String str1 = response.body().string();
                                    JSONObject jsonObject = new JSONObject(str1);
                                    Boolean success = jsonObject.getBoolean("success");
                                    if(success){
                                        Intent intentTracking = new Intent(PersonPublishActivity.this,TrackingNoActivity.class);
                                        intentTracking.putExtra("exchangeId",String.valueOf(listPublish.get(position).getId()));
                                        startActivityForResult(intentTracking,111);
                                    }else{
                                        String errorMsg = jsonObject.getString("errorMsg");
                                        AppToastMgr.shortToast(PersonPublishActivity.this,"修改失败！原因：" + errorMsg);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String str = response.networkResponse().toString();
                                Log.i("wangshu3", "network---" + str);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        //发货操作
        adapterExchange.setDeliverClick(new PersonExchangeAdapter.DeliverClick() {
            @Override
            public void deliverClick(View view, int position) {
                Intent intentTracking = new Intent(PersonPublishActivity.this,TrackingNoActivity.class);
                intentTracking.putExtra("exchangeId",String.valueOf(listPublish.get(position).getId()));
                startActivityForResult(intentTracking,111);
            }
        });
        //拒绝操作
        adapterExchange.setRefuseClick(new PersonExchangeAdapter.RefuseClick() {
            @Override
            public void refuseClick(View view, int position) {
                HttpRequest.refuseOrAgreeCoop(String.valueOf(listPublish.get(position).getId()), "拒绝", new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        AppToastMgr.shortToast(PersonPublishActivity.this,"请求失败！");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            if (null != response.cacheResponse()) {
                                String str = response.cacheResponse().toString();
                            } else {
                                try {
                                    String str1 = response.body().string();
                                    JSONObject jsonObject = new JSONObject(str1);
                                    Boolean success = jsonObject.getBoolean("success");
                                    if(success){
                                        initData(0);
//                                        Message message=new Message();
//                                        Bundle bundle = new Bundle();
//                                        bundle.putInt("position",position);
//                                        message.setData(bundle);
//                                        handler.sendMessage(message);//发送message信息
//                                        message.what=2;//标志是哪个线程传数据
                                    }else{
                                        String errorMsg = jsonObject.getString("errorMsg");
                                        AppToastMgr.shortToast(PersonPublishActivity.this,"修改失败！原因：" + errorMsg);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String str = response.networkResponse().toString();
                                Log.i("wangshu3", "network---" + str);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        //Item点击事件
        adapterExchange.setListItemClick(new PersonExchangeAdapter.ListItemClick() {
            @Override
            public void listItemClick(View view, int position) {
                Intent intentExchangeDetail = new Intent(PersonPublishActivity.this,ExchangeDetailActivity.class);
                intentExchangeDetail.putExtra("exchangeId",String.valueOf(listPublish.get(position).getId()));
                startActivity(intentExchangeDetail);
            }
        });
        adapterCoop.setListItemClick(new PersonCoopAdapter.ListItemClick() {
            @Override
            public void listItemClick(View view, int position) {
                Intent intentCoopDetail = new Intent(PersonPublishActivity.this,CooperateDetailActivity.class);
                intentCoopDetail.putExtra("coopId",String.valueOf(listCoop.get(position).getId()));
                startActivity(intentCoopDetail);
            }
        });
    }

    private void deletePublishExchange(final int position) {
        HttpRequest.deleteExchange(String.valueOf(listPublish.get(position).getId()), new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                AppToastMgr.shortToast(PersonPublishActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (null != response.cacheResponse()) {
                        String str = response.cacheResponse().toString();
                    } else {
                        try {
                            String str1 = response.body().string();
                            JSONObject jsonObject = new JSONObject(str1);
                            Boolean success = jsonObject.getBoolean("success");
                            if(success){
                                Message message=new Message();
                                Bundle bundle = new Bundle();
                                bundle.putInt("position",position);
                                message.setData(bundle);
                                handler.sendMessage(message);//发送message信息
                                message.what=1;//标志是哪个线程传数据
                            }else{
                                String errorMsg = jsonObject.getString("errorMsg");
                                AppToastMgr.shortToast(PersonPublishActivity.this,"修改失败！原因：" + errorMsg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String str = response.networkResponse().toString();
                        Log.i("wangshu3", "network---" + str);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(int type) {
        if(type == 0) {
            pageIndex = 1;
            HttpRequest.getPersonExchange(userId, String.valueOf(pageIndex), String.valueOf(pageSize), new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    AppToastMgr.shortToast(PersonPublishActivity.this, "请求失败！");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        String errorMsg = jsonObject.getString("errorMsg");
                        listPublish.removeAll(listPublish);
                        if (success) {
                            JSONObject obj = jsonObject.getJSONObject("data");
                            if(obj.getInt("totalRecord") != 0){
                                ll_person_publish.removeView(swipeRefreshLayout);
                                ll_person_publish.addView(swipeRefreshLayout);
                                JSONArray arr = obj.getJSONArray("data");
                                for (int i = 0; i < arr.length(); i++) {
                                    PersonPublish personPublish = JsonUtils.fromJson(arr.get(i).toString(), PersonPublish.class);
                                    listPublish.add(personPublish);
                                }
                                adapterExchange.notifyDataSetChanged();
                            }else{
                                setNoDataThis();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            AppToastMgr.shortToast(PersonPublishActivity.this, " 请求失败！原因：" + errorMsg);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            pageIndexCoop = 1;
            HttpRequest.getPersonCoop(userId, String.valueOf(pageIndexCoop), String.valueOf(pageSize), new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    AppToastMgr.shortToast(PersonPublishActivity.this, "请求失败！");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        String errorMsg = jsonObject.getString("errorMsg");
                        listCoop.removeAll(listCoop);
                        if (success) {
                            JSONObject obj = jsonObject.getJSONObject("data");
                            if(obj.getInt("totalRecord") != 0){
                                ll_person_publish.removeView(swipeRefreshLayout);
                                ll_person_publish.addView(swipeRefreshLayout);
                                JSONArray arr = obj.getJSONArray("data");
                                for (int i = 0; i < arr.length(); i++) {
                                    PersonCoop personCoop = JsonUtils.fromJson(arr.get(i).toString(), PersonCoop.class);
                                    listCoop.add(personCoop);
                                }
                                adapterCoop.notifyDataSetChanged();
                            }else{
                                setNoDataThis();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            AppToastMgr.shortToast(PersonPublishActivity.this, " 请求失败！原因：" + errorMsg);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == 222) {
            initData(0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_person_publish_back:
                appManager.finishActivity();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    class MyHandler extends Handler
    {
        //接受message的信息
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if(msg.what==1){
                int position = data.getInt("position");
                listPublish.remove(position);
                adapterExchange.notifyItemRemoved(position);
                if (position != listPublish.size()) {
                    adapterExchange.notifyItemRangeChanged(position, listPublish.size() - position);
                }
            }
        }
    }
    public void setNoDataThis(){
        View view = LayoutInflater.from(PersonPublishActivity.this).inflate(R.layout.activity_no_data,null);
        ll_person_publish.addView(view);
    }
}
