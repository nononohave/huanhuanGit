package com.cos.huanhuan.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.PersonCoopAdapter;
import com.cos.huanhuan.adapter.TrackLineAdapter;
import com.cos.huanhuan.model.TrackLine;
import com.cos.huanhuan.model.Tracking;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.MyListView;
import com.cos.huanhuan.utils.PicassoUtils;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.TimeLineView;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewTrackingActivity extends BaseActivity implements View.OnClickListener{
    private AppManager appManager;
    private TrackLineAdapter adapterTimeLine;
    private List<TrackLine> listTrackLine;
    private MyListView list_order_delivery;
    private ImageView iv_view_tracking;
    private TextView tv_tracking_status,tv_tracking_no,tv_tracking_from;
    private String trackingId;
    private String imgUrl;
    private TextView tv_tracking_service;
    private String phoneNum;
    private ScrollView sl_view_tracking;
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
        setTitle(this.getResources().getString(R.string.tracking_msg));
        setBaseContentView(R.layout.activity_view_tracking);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        trackingId = getIntent().getExtras().getString("trackingId");
        imgUrl = getIntent().getExtras().getString("imgUrl");
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
        sl_view_tracking = (ScrollView) findViewById(R.id.sl_view_tracking);
        list_order_delivery = (MyListView) findViewById(R.id.list_order_delivery);
        iv_view_tracking = (ImageView) findViewById(R.id.iv_view_head_tracking);
        tv_tracking_status = (TextView) findViewById(R.id.tv_tracking_status);
        tv_tracking_from = (TextView) findViewById(R.id.tv_tracking_from);
        tv_tracking_no = (TextView) findViewById(R.id.tv_tracking_no);
        tv_tracking_service = (TextView) findViewById(R.id.tv_tracking_service);
        list_order_delivery.setFocusable(false);


        listTrackLine = new ArrayList<>();
        adapterTimeLine = new TrackLineAdapter(ViewTrackingActivity.this,listTrackLine);
        list_order_delivery.setAdapter(adapterTimeLine);

        tv_tracking_service.setOnClickListener(this);
    }

    private void initData() {
        //trackingId
        //00000000201708131
        HttpRequest.getLogistics("00000000201708131", new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                toastErrorMsg(ViewTrackingActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success){
                        if(!(jsonObject.get("data").equals("null") || jsonObject.get("data").toString().length()==0 || jsonObject.get("data").equals(null))){
                            JSONObject obj =jsonObject.getJSONObject("data");
                            Tracking tracking = JsonUtils.fromJson(obj.toString(), Tracking.class);
                            phoneNum = tracking.getLogisticPhone();
                            PicassoUtils.getinstance().LoadImage(ViewTrackingActivity.this,imgUrl,iv_view_tracking,R.mipmap.comment_grey,R.mipmap.comment_grey,PicassoUtils.PICASSO_BITMAP_SHOW_ROUND_TYPE,0);
                            String staus = "<font color='#151515'>物流状态 </font><font color='#25AE5F'>" + tracking.getState() + "</font>";
                            tv_tracking_status.setText(Html.fromHtml(staus));
                            tv_tracking_from.setText("数据来源：" + tracking.getShipperCode());
                            tv_tracking_no.setText("运单编号：" + tracking.getLogisticCode());
                            listTrackLine.addAll(tracking.getAccept());
                            adapterTimeLine.notifyDataSetChanged();
                        }else{
                            setNoData();
                        }
                    }else{
                        toastErrorMsg(ViewTrackingActivity.this, " 接口调用失败！原因：" + errorMsg);
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
            case R.id.tv_tracking_service:
                // 检查是否获得了权限（Android6.0运行时权限）
                if (ContextCompat.checkSelfPermission(ViewTrackingActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    // 没有获得授权，申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ViewTrackingActivity.this,
                            Manifest.permission.CALL_PHONE)) {
                        // 帮跳转到该应用的设置界面，让用户手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }else{
                        // 不需要解释为何需要该权限，直接请求授权
                        ActivityCompat.requestPermissions(ViewTrackingActivity.this,new String[]{Manifest.permission.CALL_PHONE},1);
                    }
                }else {
                    // 已经获得授权，可以打电话
                    CallPhone();
                }
                break;
        }
    }

    private void CallPhone() {
        if (AppStringUtils.isEmpty(phoneNum)) {
            toastErrorMsg(ViewTrackingActivity.this,"未获取到客服号码");
        } else {
//            // 直接拨号
//            Intent intent = new Intent(); // 意图对象：动作 + 数据
//            intent.setAction(Intent.ACTION_CALL); // 设置动作
//            Uri data = Uri.parse("tel:" + phoneNum); // 设置数据
//            intent.setData(data);
//            startActivity(intent); // 激活Activity组件
            //弹出拨号界面
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            startActivity(intent);
        }
    }
}
