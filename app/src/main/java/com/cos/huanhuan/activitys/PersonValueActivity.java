package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.GridViewAdapter;
import com.cos.huanhuan.fragments.PayDetailFragment;
import com.cos.huanhuan.model.RechargeMoney;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.squareup.okhttp.Request;
import com.ta.utdid2.android.utils.StringUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersonValueActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private AppManager appManager;
    private ImageView img_back;
    private TextView tv_person_values,tv_person_value_pay;
    private GridView gv_person_value;
    private Button btn_recharge;
    private GridViewAdapter adapter;
    private List<RechargeMoney> listRecharge;
    private String userId;
    private Double chargeValue = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        userId = getUserId();
        initView();
        initData();
        initValueData();
    }

    private void initValueData() {
        HttpRequest.getMembers(userId, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                toastErrorMsg(PersonValueActivity.this,"请求失败！");
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
                        tv_person_values.setText(String.valueOf(userValueData.getShenJia()));
                    }else{
                        toastErrorMsg(PersonValueActivity.this, " 接口调用失败！原因：" + errorMsg);
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
        initValueData();
    }

    private void initView() {
        img_back = (ImageView) findViewById(R.id.img_person_value_back);
        tv_person_values = (TextView) findViewById(R.id.tv_person_values);
        tv_person_value_pay = (TextView) findViewById(R.id.tv_person_value_pay);
        gv_person_value = (GridView) findViewById(R.id.gv_person_value);
        btn_recharge = (Button) findViewById(R.id.btn_person_value_recharge);

        img_back.setOnClickListener(this);
        gv_person_value.setOnItemClickListener(this);
        btn_recharge.setOnClickListener(this);
        listRecharge = new ArrayList<>();
        adapter = new GridViewAdapter(PersonValueActivity.this,listRecharge);
        gv_person_value.setAdapter(adapter);
    }

    private void initData() {
        for (int i = 0; i < 5; i++) {
            RechargeMoney rechargeMoney = new RechargeMoney();
            if(i == 0){
                rechargeMoney.setMoney(10);
            }else if(i == 1){
                rechargeMoney.setMoney(30);
            }else if(i == 2){
                rechargeMoney.setMoney(50);
            }else if(i == 3){
                rechargeMoney.setMoney(100);
            }else if(i == 4){
                rechargeMoney.setMoney(200);
            }
            rechargeMoney.setClick(false);
            listRecharge.add(rechargeMoney);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_person_value_back:
                appManager.finishActivity();
                break;
            case R.id.btn_person_value_recharge:
                if(chargeValue > 0) {
                    PayDetailFragment payDetailFragment = new PayDetailFragment();
                    Bundle args = new Bundle();
                    args.putString("userId", userId);
                    args.putInt("type", 1);
                    args.putDouble("rechargeMoney", chargeValue);
                    payDetailFragment.setArguments(args);
                    payDetailFragment.show(getSupportFragmentManager(), "payDetailFragment");
                }else{
                    toastErrorMsg(PersonValueActivity.this,"请选择充值金额");
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        chargeValue = 0.0;//每次点击置为0
        for (int i = 0; i < listRecharge.size(); i++) {
            if(position == i){
                listRecharge.get(i).setClick(true);
                tv_person_value_pay.setText(String.valueOf(listRecharge.get(i).getMoney()) + "元");
                chargeValue = Double.valueOf(listRecharge.get(i).getMoney());
            }else{
                listRecharge.get(i).setClick(false);
            }
        }
        if(position == listRecharge.size()){
            tv_person_value_pay.setText("0元");
            Intent intentOtherMoney = new Intent(PersonValueActivity.this,OtherMoneyActivity.class);
            startActivity(intentOtherMoney);
        }
        adapter.notifyDataSetChanged();
    }
}
