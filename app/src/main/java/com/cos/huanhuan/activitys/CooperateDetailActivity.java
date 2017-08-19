package com.cos.huanhuan.activitys;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
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
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.FastBlur;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.MyListView;
import com.cos.huanhuan.utils.ObservableScrollView;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.CircleImageView;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CooperateDetailActivity extends BaseActivity implements ObservableScrollView.ScrollViewListener,View.OnClickListener {

    private ObservableScrollView scrollView;
    private ImageView imageView_head_blur,img_coop_share,img_coop_back;
    private TextView nickName,personDesc,detailTitle,detailTime,detailRequest,detailAddress,tv_coop_detailDesc;
    private CircleImageView headCircleImg;
    private MyListView listView;
    private RelativeLayout ll_titleBar_coop;
    private View divideLine;
    private int imageHeight;
    private AppManager appManager;
    private String userId,coopId;

    //适配器
    private List<Image> imgList;
    private CoopDetailImageAdapter coopDetailImageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooperate_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        userId = getUserId();
        coopId = getIntent().getExtras().getString("coopId");
        initView();
        initListeners();
        initData();
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
        headCircleImg = (CircleImageView) findViewById(R.id.civ_coop_headImg);

        ll_titleBar_coop.setFocusable(true);
        ll_titleBar_coop.setFocusableInTouchMode(true);
        ll_titleBar_coop.requestFocus();
        ll_titleBar_coop.bringToFront();
        img_coop_share.setOnClickListener(this);
        img_coop_back.setOnClickListener(this);
        Bitmap scaledBitmap = FastBlur.doBlur(ViewUtils.convertViewToBitmap(imageView_head_blur), 15, true);
        imageView_head_blur.setImageBitmap(scaledBitmap);
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



    private void initData() {
        HttpRequest.getCoopDetail(coopId, userId, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(CooperateDetailActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success){
                        JSONObject obj =jsonObject.getJSONObject("data");
                        CoopDetail coopDetail = JsonUtils.fromJson(obj.toString(), CoopDetail.class);
                        setData(coopDetail);
                    }else{
                        AppToastMgr.shortToast(CooperateDetailActivity.this, " 接口调用失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setData(CoopDetail coopDetail) {
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
        imgList.addAll(coopDetail.getImgList());
        coopDetailImageAdapter.notifyDataSetChanged();
        Bitmap scaledBitmap = FastBlur.doBlur(ViewUtils.convertViewToBitmap(imageView_head_blur), 15, true);
        imageView_head_blur.setImageBitmap(scaledBitmap);
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
                UMImage image = new UMImage(CooperateDetailActivity.this, "http://www.cnblogs.com/skins/CodingLife/images/title-yellow.png");//网络图片
                new ShareAction(CooperateDetailActivity.this)
                        .withMedia(image)
                        .setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA)
                        .setCallback(umShareListener)
                        .open();
                break;
            case R.id.img_coop_back:
                //返回按钮
                appManager.finishActivity();
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
            Toast.makeText(CooperateDetailActivity.this,"成功了",Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(CooperateDetailActivity.this,"失败"+t.getMessage(),Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(CooperateDetailActivity.this,"取消了",Toast.LENGTH_LONG).show();
        }
    };


}
