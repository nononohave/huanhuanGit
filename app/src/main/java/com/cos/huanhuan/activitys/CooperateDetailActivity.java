package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cos.huanhuan.MainActivity;
import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.CoopDetailImageAdapter;
import com.cos.huanhuan.model.CoopDetail;
import com.cos.huanhuan.model.Image;
import com.cos.huanhuan.model.JsonBean;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppACache;
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
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class CooperateDetailActivity extends BaseActivity implements ObservableScrollView.ScrollViewListener,View.OnClickListener,AdapterView.OnItemClickListener{

    private ObservableScrollView scrollView;
    private ImageView imageView_head_blur,img_coop_share,img_coop_back;
    private TextView nickName,personDesc,detailTitle,detailTime,detailRequest,detailAddress,tv_coop_detailDesc,tv_coop_evalu,tv_coop_join;
    private CircleImageView headCircleImg;
    private MyListView listView;
    private RelativeLayout ll_titleBar_coop;
    private View divideLine;
    private Button btn_coop_chat;
    private int imageHeight;
    private AppManager appManager;
    private String userId,coopId;
    private Handler handler;
    private List<String> listStrImg;
    private Boolean isLike = false;
    //适配器
    private List<Image> imgList;
    private CoopDetailImageAdapter coopDetailImageAdapter;
    private CoopDetail coopDetailItem;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooperate_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(CooperateDetailActivity.this);
        UserValueData userValueData = sharedPreferencesHelper.getObject("userData");
        coopId = getIntent().getExtras().getString("coopId");
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
        imageView_head_blur = (ImageView) findViewById(R.id.img_coopDetail);
        img_coop_share=(ImageView) findViewById(R.id.img_coop_share);
        img_coop_back=(ImageView) findViewById(R.id.img_coop_back);
        scrollView = (ObservableScrollView) findViewById(R.id.obscrollview_coopDetail);
        listView = (MyListView) findViewById(R.id.listView_coopDetail);
        ll_titleBar_coop = (RelativeLayout) findViewById(R.id.ll_titleBar_coop);
        divideLine = (View) findViewById(R.id.view_coop_divide);
        nickName = (TextView) findViewById(R.id.tv_nickName);
        personDesc = (TextView) findViewById(R.id.tv_personDesc);
        detailTitle = (TextView) findViewById(R.id.tv_coop_detailTitle);
        detailTime = (TextView) findViewById(R.id.tv_coop_detailTime);
        detailRequest = (TextView) findViewById(R.id.tv_coop_detailRequest);
        detailAddress = (TextView) findViewById(R.id.tv_coop_detailAddress);
        tv_coop_detailDesc = (TextView) findViewById(R.id.tv_coop_detailDesc);
        tv_coop_evalu = (TextView) findViewById(R.id.tv_coop_evalu);
        tv_coop_join = (TextView) findViewById(R.id.tv_coop_join);
        btn_coop_chat = (Button) findViewById(R.id.btn_coop_chat);
        headCircleImg = (CircleImageView) findViewById(R.id.civ_coop_headImg);

        ll_titleBar_coop.setFocusable(true);
        ll_titleBar_coop.setFocusableInTouchMode(true);
        ll_titleBar_coop.requestFocus();
        ll_titleBar_coop.bringToFront();
        img_coop_share.setOnClickListener(this);
        img_coop_back.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        tv_coop_evalu.setOnClickListener(this);
        tv_coop_join.setOnClickListener(this);
        btn_coop_chat.setOnClickListener(this);

        handler=new MyHandler();

        imgList = new ArrayList<Image>();
        coopDetailImageAdapter = new CoopDetailImageAdapter(CooperateDetailActivity.this,imgList);
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

                scrollView.setScrollViewListener(CooperateDetailActivity.this);
            }
        });
    }



    private void initData(int type) {
        if(type == 1) {
            userId = "";
        }
        HttpRequest.getCoopDetail(coopId, userId, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(CooperateDetailActivity.this, "请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if (success) {
                        JSONObject obj = jsonObject.getJSONObject("data");
                        CoopDetail coopDetail = JsonUtils.fromJson(obj.toString(), CoopDetail.class);
                        coopDetailItem = coopDetail;
                        setData(coopDetail);
                    } else {
                        AppToastMgr.shortToast(CooperateDetailActivity.this, " 接口调用失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setData(final CoopDetail coopDetail) {
        //配置数据
        Picasso.with(CooperateDetailActivity.this).load(coopDetail.getPortrait()).placeholder(R.mipmap.public_placehold).into(imageView_head_blur);
        //设置头像
        Picasso.with(CooperateDetailActivity.this).load(coopDetail.getPortrait()).placeholder(R.mipmap.public_placehold).into(headCircleImg);
        nickName.setText(coopDetail.getNickname());
        personDesc.setText(coopDetail.getDesc());
        detailTitle.setText(coopDetail.getTitle());
        detailTime.setText(coopDetail.getEnrollEnd());
        detailRequest.setText(coopDetail.getWill());
        detailAddress.setText(coopDetail.getAddress());
        tv_coop_detailDesc.setText(coopDetail.getDescribe());
        tv_coop_evalu.setText(String.valueOf(coopDetail.getCommentNum()));
        if(userId.equals(coopDetail.getUserId())){
            btn_coop_chat.setVisibility(View.GONE);
        }

        if(coopDetail.getHeed()){
            tv_coop_join.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.join_red),null,null);
            isLike = true;
        }else{
            tv_coop_join.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.join_grey),null,null);
            isLike = false;
        }

        imgList.addAll(coopDetail.getImgList());
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
                data.putString("imgUrl", coopDetail.getPortrait());
                message.setData(data);
                handler.sendMessage(message);//发送message信息
                message.what=1;//标志是哪个线程传数据
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ImagPagerUtil imagPagerUtil = new ImagPagerUtil(CooperateDetailActivity.this, listStrImg,position);
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
                Bitmap scaledBitmap = FastBlur.doBlur(ViewUtils.createViewBitmap(imageView_head_blur), 16, true);
                imageView_head_blur.setImageBitmap(scaledBitmap);
            }else if(msg.what == 3){
                if(isLike){
                    tv_coop_join.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.join_grey),null,null);
                    isLike = false;
                }else{
                    tv_coop_join.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.join_red),null,null);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_coop_share:
                //分享按钮
                if(coopDetailItem != null) {
                    UMWeb web = new UMWeb(coopDetailItem.getShareURL());
                    web.setTitle(coopDetailItem.getTitle());//标题
                    web.setThumb(new UMImage(CooperateDetailActivity.this,R.mipmap.ic_launcher));  //缩略图
                    web.setDescription(coopDetailItem.getDescribe());//描述
                    new ShareAction(CooperateDetailActivity.this)
                            .withMedia(web)
                            .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA)
                            .setCallback(umShareListener)
                            .open();
                }
                break;
            case R.id.img_coop_back:
                //返回按钮
                appManager.finishActivity();
                break;
            case R.id.tv_coop_evalu:
                if(AppStringUtils.isNotEmpty(userId)) {
                    Intent intentComment = new Intent(CooperateDetailActivity.this, CommentActivity.class);
                    intentComment.putExtra("coopId", coopId);
                    startActivity(intentComment);
                }else{
                    AppToastMgr.shortToast(CooperateDetailActivity.this,"请登录");
                    Intent intentLogin = new Intent(CooperateDetailActivity.this,LoginActivity.class);
                    startActivity(intentLogin);
                }
                break;
            case R.id.tv_coop_join:
                if(AppStringUtils.isNotEmpty(userId)) {
                    HttpRequest.joinCoop(coopId, userId, new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            AppToastMgr.shortToast(CooperateDetailActivity.this, "请求失败！");
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
                                            AppToastMgr.shortToast(CooperateDetailActivity.this, "修改失败！原因：" + errorMsg);
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
                    AppToastMgr.shortToast(CooperateDetailActivity.this,"请登录");
                    Intent intentLogin = new Intent(CooperateDetailActivity.this,LoginActivity.class);
                    startActivity(intentLogin);
                }
                break;
            case R.id.btn_coop_chat:
                if(AppStringUtils.isNotEmpty(userId)) {
                    if (RongIM.getInstance() != null) {
                        RongIM.getInstance().startPrivateChat(CooperateDetailActivity.this, coopDetailItem.getUserId(), coopDetailItem.getNickname());
                    } else {
                        AppToastMgr.shortToast(CooperateDetailActivity.this, "融云初始化为null");
                    }
                }else{
                    AppToastMgr.shortToast(CooperateDetailActivity.this,"请登录");
                    Intent intentLogin = new Intent(CooperateDetailActivity.this,LoginActivity.class);
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
            AppToastMgr.shortToast(CooperateDetailActivity.this,"分享成功");
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            AppToastMgr.shortToast(CooperateDetailActivity.this,"分享失败");
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            AppToastMgr.shortToast(CooperateDetailActivity.this,"取消分享");
        }
    };
}
