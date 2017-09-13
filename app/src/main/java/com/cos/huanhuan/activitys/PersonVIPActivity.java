package com.cos.huanhuan.activitys;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.fragments.PayDetailFragment;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.PicassoUtils;
import com.cos.huanhuan.views.CircleImageView;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonVIPActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private String userId;
    private CircleImageView vip_headImage;
    private TextView vip_nickName,vip_rechargeNums,vip_time;
    private ImageButton vip_rechargeChoose;
    private Button vip_recharge;
    private Boolean isImgClick = true;//默认选中1年有效期
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
        setTitle(this.getResources().getString(R.string.moneyRecharge));
        setBaseContentView(R.layout.activity_person_vip);
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
    /**
     * 初始化控件
     */
    private void initView() {

        vip_headImage = (CircleImageView) findViewById(R.id.civ_person_vip_headImage);
        vip_nickName = (TextView) findViewById(R.id.tv_person_vip_nickName);
        vip_rechargeNums = (TextView) findViewById(R.id.tv_person_vip_rechargeNums);
        vip_time = (TextView) findViewById(R.id.tv_person_vip_time);
        vip_rechargeChoose = (ImageButton) findViewById(R.id.ib_vip_rechargeChoose);
        vip_recharge = (Button) findViewById(R.id.btn_person_vip_recharge);

        vip_rechargeChoose.setOnClickListener(this);
        vip_recharge.setOnClickListener(this);
    }

    private void initData() {
        HttpRequest.getMembers(userId, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                toastErrorMsg(PersonVIPActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success){
                        JSONObject obj =jsonObject.getJSONObject("data");
                        UserValueData userValueData = JsonUtils.fromJson(obj.toString(), UserValueData.class);
                        PicassoUtils.getinstance().LoadImage(PersonVIPActivity.this,userValueData.getPortrait(),vip_headImage,R.mipmap.comment_grey,R.mipmap.comment_grey,PicassoUtils.PICASSO_BITMAP_SHOW_ROUND_TYPE,0);
                        vip_nickName.setText(userValueData.getNickname());
                        if(userValueData.getVip()){
                            vip_rechargeNums.setText("可兑换次数" + userValueData.getSurplus() + "次");
                            vip_time.setText(userValueData.getEndTime());
                        }else{
                            vip_rechargeNums.setText("可兑换次数0次");
                            vip_time.setText("已过期或未充值");
                        }
                    }else{
                        toastErrorMsg(PersonVIPActivity.this, " 接口调用失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_vip_rechargeChoose:
                if(isImgClick){
                    vip_rechargeChoose.setImageResource(R.mipmap.circle);
                    isImgClick = false;
                }else{
                    vip_rechargeChoose.setImageResource(R.mipmap.circle_ok);
                    isImgClick = true;
                }
                break;
            case R.id.btn_person_vip_recharge:
                if(isImgClick){
                    PayDetailFragment payDetailFragment=new PayDetailFragment();
                    Bundle args = new Bundle();
                    args.putString("userId", userId);
                    args.putInt("type",2);
                    args.putDouble("rechargeMoney",99.0);//这里默认设置为99后续应该会改动
                    payDetailFragment.setArguments(args);
                    payDetailFragment.show(getSupportFragmentManager(),"payDetailFragment");
                }else{
                    toastErrorMsg(PersonVIPActivity.this,"请选择充值金额");
                }
                break;
        }
    }
}
