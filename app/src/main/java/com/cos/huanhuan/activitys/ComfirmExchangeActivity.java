package com.cos.huanhuan.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.huanhuan.MainActivity;
import com.cos.huanhuan.R;
import com.cos.huanhuan.fragments.PayDetailFragment;
import com.cos.huanhuan.model.AddressVO;
import com.cos.huanhuan.model.Comment;
import com.cos.huanhuan.model.ConfirmDetail;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.SelectDialog;
import com.cos.huanhuan.views.TitleBar;
import com.squareup.okhttp.Request;
import com.umeng.socialize.UMShareAPI;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComfirmExchangeActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;

    private RelativeLayout rl_address,rl_exchange_ways,rl_address_item,rl_exchange_singleBorrow;
    private Button btn_create_address,btn_confirm;
    private TextView tv_address_person,tv_address_phone,tv_address_detail,tv_exchange_ways,tv_exchange_expenses,tv_exchange_prices,tv_total_prices,tv_exchange_prices_text,tv_exchange__singleBorrow_prices;
    private String exchangeId;
    private UserValueData userValueData;
    private String userId;
    private String examine;
    public static int CHOOSE_ADDRESS = 333;
    private ConfirmDetail confirmDetailItem;
    private int isBorrow;
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
        isBorrow = getIntent().getExtras().getInt("isBorrow");
        if(isBorrow == 0){
            setTitle(this.getResources().getString(R.string.confirm_borrow));
        }else{
            setTitle(this.getResources().getString(R.string.confirmExchange));
        }
        setBaseContentView(R.layout.activity_comfirm_exchange);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        exchangeId = getIntent().getExtras().getString("exchangeId");
        userValueData = (UserValueData) getIntent().getExtras().getSerializable("userValueData");
        userId = getUserId();
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        initView();
        initData(isBorrow);
    }
    private void initView() {
        rl_exchange_singleBorrow = (RelativeLayout) findViewById(R.id.rl_exchange_singleBorrow);
        rl_address = (RelativeLayout) findViewById(R.id.rl_address_comfirmExchange);
        rl_exchange_ways = (RelativeLayout) findViewById(R.id.rl_exchange_ways);
        rl_address_item = (RelativeLayout) findViewById(R.id.rl_address_item);
        btn_create_address = (Button) findViewById(R.id.btn_comfirm_exchange_createAddress);
        btn_confirm = (Button) findViewById(R.id.btn_confirm_exchange);
        tv_address_person = (TextView) findViewById(R.id.tv_address_person_name);
        tv_address_phone = (TextView) findViewById(R.id.tv_address_person_phone);
        tv_address_detail = (TextView) findViewById(R.id.tv_address_detail);
        tv_exchange_ways = (TextView) findViewById(R.id.tv_exchange_ways);
        tv_exchange_expenses = (TextView) findViewById(R.id.tv_exchange_expenses);
        tv_exchange_prices = (TextView) findViewById(R.id.tv_exchange_prices);
        tv_total_prices = (TextView) findViewById(R.id.tv_total_prices);
        tv_exchange_prices_text = (TextView) findViewById(R.id.tv_exchange_prices_text);
        tv_exchange__singleBorrow_prices = (TextView) findViewById(R.id.tv_exchange__singleBorrow_prices);

        rl_address.setOnClickListener(this);
        rl_exchange_ways.setOnClickListener(this);
        btn_create_address.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
    }

    private void initData(int type) {
        if(type == 0){
            //默认单词租赁
            examine = "单次租赁";
            HttpRequest.getConfirmDetail(exchangeId,userId, examine,new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    AppToastMgr.shortToast(ComfirmExchangeActivity.this,"请求失败！");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        String errorMsg = jsonObject.getString("errorMsg");
                        if(success) {
                            JSONObject obj =jsonObject.getJSONObject("data");
                            ConfirmDetail confirmDetail = JsonUtils.fromJson(obj.toString(),ConfirmDetail.class);
                            confirmDetailItem = confirmDetail;
                            setData(confirmDetail);
                        }else{
                            AppToastMgr.shortToast(ComfirmExchangeActivity.this, " 请求失败！原因：" + errorMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else if(type == 1){
            examine = "身家兑换";
            HttpRequest.getConfirmDetail(exchangeId,userId, examine,new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    AppToastMgr.shortToast(ComfirmExchangeActivity.this,"请求失败！");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        String errorMsg = jsonObject.getString("errorMsg");
                        if(success) {
                            JSONObject obj =jsonObject.getJSONObject("data");
                            ConfirmDetail confirmDetail = JsonUtils.fromJson(obj.toString(),ConfirmDetail.class);
                            confirmDetailItem = confirmDetail;
                            setData(confirmDetail);
                        }else{
                            AppToastMgr.shortToast(ComfirmExchangeActivity.this, " 请求失败！原因：" + errorMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
                examine = "会员租赁";
                HttpRequest.getConfirmDetail(exchangeId,userId, examine,new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        AppToastMgr.shortToast(ComfirmExchangeActivity.this,"请求失败！");
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean success = jsonObject.getBoolean("success");
                            String errorMsg = jsonObject.getString("errorMsg");
                            if(success) {
                                JSONObject obj =jsonObject.getJSONObject("data");
                                ConfirmDetail confirmDetail = JsonUtils.fromJson(obj.toString(),ConfirmDetail.class);
                                confirmDetailItem = confirmDetail;
                                setData(confirmDetail);
                            }else{
                                AppToastMgr.shortToast(ComfirmExchangeActivity.this, " 请求失败！原因：" + errorMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_address_comfirmExchange:
                Intent intentChooseAddress = new Intent(ComfirmExchangeActivity.this, AddressManagerActivity.class);
                startActivityForResult(intentChooseAddress,CHOOSE_ADDRESS);
                break;
            case R.id.rl_exchange_ways:
                if(isBorrow == 0) {
                    List<String> names = new ArrayList<>();
                    names.add("单次租赁");
                    names.add("会员租赁（剩余" + userValueData.getSurplus() + "次）");
                    showDialog(new SelectDialog.SelectDialogListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0:
                                    initData(0);
                                    break;
                                case 1:
                                    initData(2);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }, names);
                }else{
                    List<String> names = new ArrayList<>();
                    names.add("身家兑换（剩余" + userValueData.getShenJia() + ")身家");
                    showDialog(new SelectDialog.SelectDialogListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0:
                                    initData(1);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }, names);
                }
                break;
            case R.id.btn_comfirm_exchange_createAddress:
                Intent intentCreateAddress = new Intent(ComfirmExchangeActivity.this,AddNewAddressActivity.class);
                startActivity(intentCreateAddress);
                break;
            case R.id.btn_confirm_exchange:
                String exchangeWays = tv_exchange_ways.getText().toString();
                PayDetailFragment payDetailFragment=new PayDetailFragment();
                Bundle args = new Bundle();
                args.putString("userId", userId);
                if(exchangeWays.equals("身家兑换")){
                    args.putInt("type",3);
                }else if(exchangeWays.equals("会员租赁")){
                    args.putInt("type",4);
                }else{
                    args.putInt("type",5);
                }
                args.putDouble("rechargeMoney",confirmDetailItem.getPriceSum());
                // args.putDouble("rechargeMoney",0.01);
                args.putInt("AddressId",confirmDetailItem.getAddressId());
                args.putInt("ExId",Integer.valueOf(exchangeId));
                payDetailFragment.setArguments(args);
                payDetailFragment.show(getSupportFragmentManager(),"payDetailFragment");
                //appManager.finishActivity();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_ADDRESS && resultCode == AddressManagerActivity.SELECTED_ADDRESS) {
            AddressVO selectAddress = (AddressVO) data.getSerializableExtra("selectItem");
            if(confirmDetailItem != null){
                confirmDetailItem.setAddressId(selectAddress.getId());
                confirmDetailItem.setConsignee(selectAddress.getName());
                confirmDetailItem.setPhoneMob(selectAddress.getPhone());
                confirmDetailItem.setAddress(selectAddress.getProvince() + selectAddress.getCity() + selectAddress.getCounty() + selectAddress.getAddress());
                setData(confirmDetailItem);
            }
        }
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }
    public void setData(ConfirmDetail data) {
        if(data.getAddressId() == 0){
            btn_create_address.setEnabled(true);
            rl_address_item.setOnClickListener(null);
            rl_address_item.setEnabled(false);
            rl_address_item.setClickable(false);
            rl_address_item.setVisibility(View.GONE);
            btn_create_address.setVisibility(View.VISIBLE);
        }else{
            btn_create_address.setEnabled(false);
            rl_address_item.setEnabled(true);
            rl_address_item.setVisibility(View.VISIBLE);
            btn_create_address.setVisibility(View.GONE);
            tv_address_person.setText(data.getConsignee());
            tv_address_phone.setText(data.getPhoneMob());
            tv_address_detail.setText("收货地址：" + data.getAddress());
        }

        tv_exchange_ways.setText(data.getExamine());
        tv_exchange_expenses.setText("全国统一邮费" + String.valueOf(data.getFare()) + "元");
        tv_total_prices.setText("￥" + String.valueOf(data.getPriceSum()));
        if(data.getExamine().equals("身家兑换")){
            tv_exchange_prices_text.setText("最终值");
            tv_exchange_prices.setText(String.valueOf(data.getPrice()) + "身家");
            rl_exchange_singleBorrow.setVisibility(View.GONE);
            btn_confirm.setText("确认兑换");
        }else if(data.getExamine().equals("会员租赁")){
            tv_exchange_prices_text.setText("押金");
            tv_exchange_prices.setText(String.valueOf(data.getDeposit()) + "元");
            rl_exchange_singleBorrow.setVisibility(View.GONE);
            btn_confirm.setText("确认租借");
        }else{
            tv_exchange_prices_text.setText("押金");
            tv_exchange_prices.setText(String.valueOf(data.getDeposit())  + "元");
            rl_exchange_singleBorrow.setVisibility(View.VISIBLE);
            tv_exchange__singleBorrow_prices.setText(String.valueOf(data.getPrice())  + "元");
            btn_confirm.setText("确认租借");
        }
    }
}
