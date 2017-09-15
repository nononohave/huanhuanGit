package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.CoopDetailImageAdapter;
import com.cos.huanhuan.model.CoopDetail;
import com.cos.huanhuan.model.ExchangeDetail;
import com.cos.huanhuan.model.Image;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.FastBlur;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.MyListView;
import com.cos.huanhuan.utils.ObservableScrollView;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.CircleImageView;
import com.cos.huanhuan.views.ImagPagerUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeDetailActivity extends BaseActivity implements ObservableScrollView.ScrollViewListener,View.OnClickListener,AdapterView.OnItemClickListener {

    private ObservableScrollView scrollView;
    private ImageView imageView_head_blur,img_exchange_share,img_exchange_back;
    private TextView nickName,personDesc,detailTitle,tv_exchange_status,tv_exchange_classify,tv_exchange_finalValue,tv_exchange_detailDesc,tv_exchange_evalu,tv_exchange_join;
    private CircleImageView headCircleImg;
    private MyListView listView;
    private RelativeLayout ll_titleBar_exchange;
    private View divideLine;
    private Button btn_exchange_borrow,btn_exchange_now;
    private int imageHeight;
    private AppManager appManager;
    private String userId,exchangeId;
    private Handler handler;
    private List<String> listStrImg;
    private Boolean isLike = false;
    private SharedPreferencesHelper sharedPreferencesHelper;
    //适配器
    private List<Image> imgList;
    private CoopDetailImageAdapter coopDetailImageAdapter;

    private UserValueData userValueData;//用于传入下一个页面的用户身家信息
    private String shareUrl;
    private ExchangeDetail exchangeDetailItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(ExchangeDetailActivity.this);
        UserValueData userValueData = sharedPreferencesHelper.getObject("userData");
        exchangeId = getIntent().getExtras().getString("exchangeId");
        if(userValueData != null){
            userId = String.valueOf(userValueData.getId());
            initData(0);
        }else{
            initData(1);
        }
        initView();
        initListeners();
    }

    private void initView() {
        imageView_head_blur = (ImageView) findViewById(R.id.img_exchangeDetail);
        img_exchange_share=(ImageView) findViewById(R.id.img_exchange_share);
        img_exchange_back=(ImageView) findViewById(R.id.img_exchange_back);
        scrollView = (ObservableScrollView) findViewById(R.id.obscrollview_exchangeDetail);
        listView = (MyListView) findViewById(R.id.listView_exchangeDetail);
        ll_titleBar_exchange = (RelativeLayout) findViewById(R.id.ell_titleBar_xchange);
        divideLine = (View) findViewById(R.id.view_exchange_divide);
        nickName = (TextView) findViewById(R.id.tv_nickName);
        personDesc = (TextView) findViewById(R.id.tv_personDesc);
        detailTitle = (TextView) findViewById(R.id.tv_exchange_detailTitle);
        tv_exchange_status = (TextView) findViewById(R.id.tv_exchange_status);
        tv_exchange_classify = (TextView) findViewById(R.id.tv_exchange_classify);
        tv_exchange_finalValue = (TextView) findViewById(R.id.tv_exchange_finalValue);
        tv_exchange_detailDesc = (TextView) findViewById(R.id.tv_exchange_detailDesc);
        tv_exchange_evalu = (TextView) findViewById(R.id.tv_exchange_evalu);
        tv_exchange_join = (TextView) findViewById(R.id.tv_exchange_join);
        btn_exchange_borrow = (Button) findViewById(R.id.btn_exchange_borrow);
        btn_exchange_now = (Button) findViewById(R.id.btn_exchange_now);
        headCircleImg = (CircleImageView) findViewById(R.id.civ_exchange_headImg);

        ll_titleBar_exchange.setFocusable(true);
        ll_titleBar_exchange.setFocusableInTouchMode(true);
        ll_titleBar_exchange.requestFocus();
        ll_titleBar_exchange.bringToFront();
        img_exchange_share.setOnClickListener(this);
        img_exchange_back.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        tv_exchange_evalu.setOnClickListener(this);
        tv_exchange_join.setOnClickListener(this);
        btn_exchange_borrow.setOnClickListener(this);
        btn_exchange_now.setOnClickListener(this);

        handler=new MyHandler();

        imgList = new ArrayList<Image>();
        coopDetailImageAdapter = new CoopDetailImageAdapter(ExchangeDetailActivity.this,imgList);
        listView.setAdapter(coopDetailImageAdapter);
    }

    private void initListeners() {
        // 获取顶部图片高度后，设置滚动监听
        ViewTreeObserver vto = imageView_head_blur.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView_head_blur.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
                imageHeight = imageView_head_blur.getHeight();

                scrollView.setScrollViewListener(ExchangeDetailActivity.this);
            }
        });
    }



    private void initData(int type) {
        if(type == 0) {
            HttpRequest.getExchangeDetail(exchangeId, userId, new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    toastErrorMsg(ExchangeDetailActivity.this, "请求失败！");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        String errorMsg = jsonObject.getString("errorMsg");
                        if (success) {
                            JSONObject obj = jsonObject.getJSONObject("data");
                            ExchangeDetail exchangeDetail = JsonUtils.fromJson(obj.toString(), ExchangeDetail.class);
                            setData(exchangeDetail);
                        } else {
                            toastErrorMsg(ExchangeDetailActivity.this, " 接口调用失败！原因：" + errorMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            HttpRequest.getMembers(userId, new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    toastErrorMsg(ExchangeDetailActivity.this,"请求失败！");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        String errorMsg = jsonObject.getString("errorMsg");
                        if(success){
                            JSONObject obj =jsonObject.getJSONObject("data");
                            userValueData = JsonUtils.fromJson(obj.toString(), UserValueData.class);
                        }else{
                            toastErrorMsg(ExchangeDetailActivity.this, " 接口调用失败！原因：" + errorMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            HttpRequest.getExchangeDetail(exchangeId, "", new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    toastErrorMsg(ExchangeDetailActivity.this, "请求失败！");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        String errorMsg = jsonObject.getString("errorMsg");
                        if (success) {
                            JSONObject obj = jsonObject.getJSONObject("data");
                            ExchangeDetail exchangeDetail = JsonUtils.fromJson(obj.toString(), ExchangeDetail.class);
                            setData(exchangeDetail);
                        } else {
                            toastErrorMsg(ExchangeDetailActivity.this, " 接口调用失败！原因：" + errorMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }

    private void setData(final ExchangeDetail exchangeDetail) {
        exchangeDetailItem = exchangeDetail;
        //配置数据
        Picasso.with(ExchangeDetailActivity.this).load(exchangeDetail.getPortrait()).placeholder(R.mipmap.public_placehold).into(imageView_head_blur);
        //设置头像
        Picasso.with(ExchangeDetailActivity.this).load(exchangeDetail.getPortrait()).placeholder(R.mipmap.public_placehold).into(headCircleImg);
        nickName.setText(exchangeDetail.getNickname());
        personDesc.setText(exchangeDetail.getDesc());
        detailTitle.setText(exchangeDetail.getTitle());
        tv_exchange_status.setText(exchangeDetail.getExamineName());
        tv_exchange_classify.setText(exchangeDetail.getClassName());
        tv_exchange_finalValue.setText(exchangeDetail.getOfficial());
        tv_exchange_detailDesc.setText(exchangeDetail.getDescribe());
        tv_exchange_evalu.setText(String.valueOf(exchangeDetail.getCommentNum()));

        if(exchangeDetail.getHeed()){
            tv_exchange_join.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.join_red),null,null);
            isLike = true;
        }else{
            tv_exchange_join.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.join_grey),null,null);
            isLike = false;
        }

        imgList.addAll(exchangeDetail.getImgList());
        coopDetailImageAdapter.notifyDataSetChanged();
        listStrImg = new ArrayList<>();

        for (int i = 0; i < imgList.size(); i++) {
            listStrImg.add(imgList.get(i).getImgPath());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=new Message();
                Bundle data = new Bundle();
                data.putString("imgUrl", exchangeDetail.getPortrait());
                message.setData(data);
                handler.sendMessage(message);//发送message信息
                message.what=1;//标志是哪个线程传数据
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ImagPagerUtil imagPagerUtil = new ImagPagerUtil(ExchangeDetailActivity.this, listStrImg,position);
        imagPagerUtil.setContentText("");
        imagPagerUtil.show();
    }

    class MyHandler extends Handler
    {
        //接受message的信息
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what==1)
            {
//                Bitmap scaledBitmap = FastBlur.doBlur(ViewUtils.createViewBitmap(imageView_head_blur), 16, true);
//                imageView_head_blur.setImageBitmap(scaledBitmap);
            }else if(msg.what == 3){
                if(isLike){
                    tv_exchange_join.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.join_grey),null,null);
                    isLike = false;
                }else{
                    tv_exchange_join.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.join_red),null,null);
                    isLike = true;
                }
            }

        }
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
            img_exchange_back.setImageResource(R.mipmap.back_green);
            img_exchange_share.setImageResource(R.mipmap.share);
            ll_titleBar_exchange.setBackgroundColor(Color.argb( 0, 255, 255, 255));//AGB由相关工具获得，或者美工提供
        } else if (y > 0 && y <= imageHeight - DensityUtils.dip2px(ExchangeDetailActivity.this,64)) {
            divideLine.setVisibility(View.GONE);
            img_exchange_back.setImageResource(R.mipmap.back_green);
            img_exchange_share.setImageResource(R.mipmap.share);
            float scale = (float) y / imageHeight;
            float alpha = (255 * scale);
            // 只是layout背景透明(仿知乎滑动效果)
            ll_titleBar_exchange.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
        } else {
            divideLine.setVisibility(View.VISIBLE);
            img_exchange_back.setImageResource(R.mipmap.nav_back);
            img_exchange_share.setImageResource(R.mipmap.share_red);
            ll_titleBar_exchange.setBackgroundColor(Color.argb( 255, 255, 255, 255));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_exchange_share:
                //分享按钮
//                UMImage image = new UMImage(ExchangeDetailActivity.this, shareUrl);//网络图片
//                new ShareAction(ExchangeDetailActivity.this)
//                        .withMedia(image)
//                        .setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA)
//                        .setCallback(umShareListener)
//                        .open();
                if(exchangeDetailItem != null) {
                    UMWeb web = new UMWeb(exchangeDetailItem.getShareURL());
                    web.setTitle(exchangeDetailItem.getTitle());//标题
                    web.setThumb(new UMImage(ExchangeDetailActivity.this,R.mipmap.ic_launcher));  //缩略图
                    web.setDescription(exchangeDetailItem.getDescribe());//描述
                    new ShareAction(ExchangeDetailActivity.this)
                            .withMedia(web)
                            .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA)
                            .setCallback(umShareListener)
                            .open();
                }
                break;
            case R.id.img_exchange_back:
                //返回按钮
                appManager.finishActivity();
                break;
            case R.id.tv_exchange_evalu:
                if(AppStringUtils.isNotEmpty(userId)) {
                    Intent intentComment = new Intent(ExchangeDetailActivity.this, ExchangeCommentActivity.class);
                    intentComment.putExtra("exchangeId", exchangeId);
                    startActivity(intentComment);
                }else{
                    toastErrorMsg(ExchangeDetailActivity.this,"未登录");
                    Intent intentLogin = new Intent(ExchangeDetailActivity.this,LoginActivity.class);
                    startActivity(intentLogin);
                }
                break;
            case R.id.tv_exchange_join:
                if(AppStringUtils.isNotEmpty(userId)) {
                    HttpRequest.attentionExchange(exchangeId, userId, new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            toastErrorMsg(ExchangeDetailActivity.this, "请求失败！");
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
                                        if (success) {
                                            Message message = new Message();
                                            handler.sendMessage(message);//发送message信息
                                            message.what = 3;//标志是哪个线程传数据
                                        } else {
                                            String errorMsg = jsonObject.getString("errorMsg");
                                            toastErrorMsg(ExchangeDetailActivity.this,errorMsg);
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
                }else{
                    toastErrorMsg(ExchangeDetailActivity.this,"未登录");
                    Intent intentLogin = new Intent(ExchangeDetailActivity.this,LoginActivity.class);
                    startActivity(intentLogin);
                }
                break;
            case R.id.btn_exchange_borrow:
                if(AppStringUtils.isNotEmpty(userId)) {
                    if (exchangeDetailItem != null) {
                        if(exchangeDetailItem.getExamineName().equals("可兑换")) {
                            if (!userId.equals(String.valueOf(exchangeDetailItem.getUserId()))) {
                                Intent intentConfirmBorrow = new Intent(ExchangeDetailActivity.this, ComfirmExchangeActivity.class);
                                intentConfirmBorrow.putExtra("exchangeId", exchangeId);
                                if (userValueData != null) {
                                    intentConfirmBorrow.putExtra("userValueData", userValueData);
                                }
                                intentConfirmBorrow.putExtra("isBorrow", 0);
                                startActivity(intentConfirmBorrow);
                            } else {
                                toastErrorMsg(ExchangeDetailActivity.this, "无法兑换自己发布的兑换！");
                            }
                        }else{
                            toastErrorMsg(ExchangeDetailActivity.this, "该产品暂不能租借");
                        }
                    }
                }else{
                    toastErrorMsg(ExchangeDetailActivity.this,"未登录");
                    Intent intentLogin = new Intent(ExchangeDetailActivity.this,LoginActivity.class);
                    startActivity(intentLogin);
                }
                break;
            case R.id.btn_exchange_now:
                if(AppStringUtils.isNotEmpty(userId)){
                    if(exchangeDetailItem != null){
                        if(exchangeDetailItem.getExamineName().equals("可兑换")) {
                            if (!userId.equals(String.valueOf(exchangeDetailItem.getUserId()))) {
                                Intent intentConfirmExchange = new Intent(ExchangeDetailActivity.this, ComfirmExchangeActivity.class);
                                intentConfirmExchange.putExtra("exchangeId", exchangeId);
                                if (userValueData != null) {
                                    intentConfirmExchange.putExtra("userValueData", userValueData);
                                }
                                intentConfirmExchange.putExtra("isBorrow", 1);
                                startActivity(intentConfirmExchange);
                            } else {
                                toastErrorMsg(ExchangeDetailActivity.this, "无法兑换自己发布的兑换！");
                            }
                        }else{
                            toastErrorMsg(ExchangeDetailActivity.this, "该产品暂不能兑换");
                        }
                    }
                }else{
                    toastErrorMsg(ExchangeDetailActivity.this,"未登录");
                    Intent intentLogin = new Intent(ExchangeDetailActivity.this,LoginActivity.class);
                    startActivity(intentLogin);
                }
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            toastErrorMsg(ExchangeDetailActivity.this,"分享成功");
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            toastErrorMsg(ExchangeDetailActivity.this,"分享失败");
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            toastErrorMsg(ExchangeDetailActivity.this,"取消分享");
        }
    };


}
