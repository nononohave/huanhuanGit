package com.cos.huanhuan.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.cos.huanhuan.R;
import com.cos.huanhuan.activitys.BaseActivity;
import com.cos.huanhuan.activitys.ConversationActivity;
import com.cos.huanhuan.activitys.IndexActivity;
import com.cos.huanhuan.activitys.LoginActivity;
import com.cos.huanhuan.activitys.MyExchangeActivity;
import com.cos.huanhuan.model.ExchangeAdd;
import com.cos.huanhuan.model.Image;
import com.cos.huanhuan.model.Recharge;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.utils.alipay.PayResult;
import com.squareup.okhttp.Request;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * 底部弹窗Fragment
 */
public class PayDetailFragment extends DialogFragment {
    private RelativeLayout rePayWay, rePayDetail, re_aliPay,re_wxPay;
    private LinearLayout LinPayWay;
    private Button btnPay,btn_choose_payWays;
    private ImageView imageCloseOne,imageCloseTwo,iv_choosed_aliPay,iv_choosed_wxPay;
    private TextView tv_payWays,priceMoneyToPay;
    private static final int SDK_PAY_FLAG = 1;
    private String userId;
    private IWXAPI api;
    private Context mContext;
    private Dialog dialogLoading;
    private int type;//判断从哪个页面传过来的支付选中页面，通过type调用不同的接口
    private double rechargeMoney;
    private int addressId,exId;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getString("userId");
        type = getArguments().getInt("type");
        sharedPreferencesHelper = new SharedPreferencesHelper(getActivity());
        if(type == 3 || type == 4 || type == 5){
            addressId = getArguments().getInt("AddressId");
            exId = getArguments().getInt("ExId");
        }
        rechargeMoney = getArguments().getDouble("rechargeMoney");
        api = WXAPIFactory.createWXAPI(getActivity(), "wx34470b0a77faa852");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        mContext = getActivity();
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.setContentView(R.layout.fragment_pay_detail);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
        // 设置宽度为屏宽, 靠近屏幕底部。
        final Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.AnimBottom);
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        lp.height = getActivity().getWindowManager().getDefaultDisplay().getHeight() * 3 / 6;
        window.setAttributes(lp);

        initView(dialog);
        return dialog;
    }

    private void initView(Dialog dialog) {
        rePayWay = (RelativeLayout) dialog.findViewById(R.id.re_pay_way);//支付方式选择
        rePayDetail = (RelativeLayout) dialog.findViewById(R.id.re_pay_detail);//付款详情页面
        LinPayWay = (LinearLayout) dialog.findViewById(R.id.lin_pay_way);//付款方式页面
        re_aliPay = (RelativeLayout) dialog.findViewById(R.id.re_aliPay);//付款方式（支付宝）
        re_wxPay = (RelativeLayout) dialog.findViewById(R.id.re_wxPay);// 付款方式（微信）
        btnPay = (Button) dialog.findViewById(R.id.btn_confirm_pay);//点击支付按钮
        btn_choose_payWays = (Button) dialog.findViewById(R.id.btn_choose_payWays);//确认选择支付方式
        imageCloseOne= (ImageView) dialog.findViewById(R.id.close_one);//第一个关掉支付界面
        imageCloseTwo= (ImageView) dialog.findViewById(R.id.close_two);//第二个返回付款界面
        iv_choosed_aliPay = (ImageView)dialog.findViewById(R.id.iv_choosed_aliPay);//选中支付宝
        iv_choosed_wxPay = (ImageView)dialog.findViewById(R.id.iv_choosed_wxPay);//选中WX
        tv_payWays = (TextView) dialog.findViewById(R.id.tv_payWays);// 选中的支付方式
        priceMoneyToPay = (TextView) dialog.findViewById(R.id.priceMoneyToPay);//支付的金额
        rePayWay.setOnClickListener(listener);
        re_aliPay.setOnClickListener(listener);
        re_wxPay.setOnClickListener(listener);
        btnPay.setOnClickListener(listener);
        imageCloseOne.setOnClickListener(listener);
        imageCloseTwo.setOnClickListener(listener);
        btn_choose_payWays.setOnClickListener(listener);
        iv_choosed_aliPay.setOnClickListener(listener);
        iv_choosed_wxPay.setOnClickListener(listener);
        priceMoneyToPay.setText(String.valueOf(rechargeMoney));
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Animation slide_left_to_left = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_to_left);
            Animation slide_right_to_left = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_to_left);
            Animation slide_left_to_right = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_to_right);
            Animation slide_left_to_left_in = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_to_left_in);
            switch (view.getId()) {
                case R.id.re_pay_way://选择方式
                    String payWays = tv_payWays.getText().toString();
                    if(payWays.equals("支付宝")){
                        iv_choosed_aliPay.setVisibility(View.VISIBLE);
                        iv_choosed_wxPay.setVisibility(View.GONE);
                    }else{
                        iv_choosed_aliPay.setVisibility(View.GONE);
                        iv_choosed_wxPay.setVisibility(View.VISIBLE);
                    }
                    rePayDetail.startAnimation(slide_left_to_left);
                    rePayDetail.setVisibility(View.GONE);
                    LinPayWay.startAnimation(slide_right_to_left);
                    LinPayWay.setVisibility(View.VISIBLE);
                    break;
                case R.id.re_aliPay:
                    iv_choosed_aliPay.setVisibility(View.VISIBLE);
                    iv_choosed_wxPay.setVisibility(View.GONE);
                    break;
                case R.id.re_wxPay:
                    iv_choosed_aliPay.setVisibility(View.GONE);
                    iv_choosed_wxPay.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_confirm_pay://确认付款
                    final Recharge recharge = new Recharge();
                    recharge.setUserId(Integer.valueOf(userId));
                    recharge.setMoney(rechargeMoney);
                    if(type == 1) {
                        recharge.setType("身家充值");
                    }else if(type == 2){
                        recharge.setType("会员充值");
                    }
                    final ExchangeAdd exchangeAdd = new ExchangeAdd();
                    exchangeAdd.setUserId(Integer.valueOf(userId));
                    if(type == 3){
                        exchangeAdd.setExamine("身家兑换");
                    }else if(type == 4){
                        exchangeAdd.setExamine("会员租赁");
                    }else if(type == 5){
                        exchangeAdd.setExamine("单次租赁");
                    }
                    final String payType = tv_payWays.getText().toString().trim();
                    if(payType.equals("支付宝")){
                        recharge.setPayType("Ali");
                        exchangeAdd.setPayType("Ali");
                    }else{
                        recharge.setPayType("Wx");
                        exchangeAdd.setPayType("Wx");
                    }
                    exchangeAdd.setAddressId(addressId);
                    exchangeAdd.setExId(exId);
                    if(type == 1 || type == 2) {
                        sharedPreferencesHelper.put("isRecharge",true);
                        HttpRequest.rechargePersonValue(recharge, new StringCallback() {
                            @Override
                            public void onError(Request request, Exception e) {
                                AppToastMgr.shortToast(getActivity(), "请求失败！");
                            }

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Boolean success = jsonObject.getBoolean("success");
                                    String errorMsg = jsonObject.getString("errorMsg");
                                    if (success) {
                                        getDialog().dismiss();
                                        if (payType.equals("支付宝")) {
                                            AppToastMgr.shortToast(getActivity(), "正常调起支付宝支付");
                                            final String orderInfo = jsonObject.getString("data");
                                            Runnable payRunnable = new Runnable() {

                                                @Override
                                                public void run() {
                                                    PayTask alipay = new PayTask(getActivity());
                                                    Map<String, String> result = alipay.payV2(orderInfo, true);
                                                    Log.i("msp", result.toString());

                                                    Message msg = new Message();
                                                    msg.what = SDK_PAY_FLAG;
                                                    msg.obj = result;
                                                    mHandler.sendMessage(msg);
                                                }
                                            };

                                            Thread payThread = new Thread(payRunnable);
                                            payThread.start();
                                        } else {
                                            JSONObject data = jsonObject.getJSONObject("data");
                                            PayReq req = new PayReq();
                                            req.appId = data.getString("appid");
                                            req.partnerId = data.getString("partnerid");
                                            req.prepayId = data.getString("prepayid");
                                            req.nonceStr = data.getString("noncestr");
                                            req.timeStamp = data.getString("timestamp");
                                            req.packageValue = data.getString("package");
                                            req.sign = data.getString("sign");
                                            req.extData = "app data"; // optional
                                            AppToastMgr.shortToast(getActivity(), "正常调起支付");
                                            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                            api.sendReq(req);
                                        }
                                    } else {
                                        AppToastMgr.shortToast(getActivity(), " 获取充值信息失败！原因：" + errorMsg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else{
                        sharedPreferencesHelper.put("isRecharge",false);
                        HttpRequest.ComfirmExchange(exchangeAdd, new StringCallback() {
                            @Override
                            public void onError(Request request, Exception e) {
                                AppToastMgr.shortToast(getActivity(), "请求失败！");
                            }

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Boolean success = jsonObject.getBoolean("success");
                                    String errorMsg = jsonObject.getString("errorMsg");
                                    if (success) {
                                        getDialog().dismiss();
                                        if (payType.equals("支付宝")) {
                                            AppToastMgr.shortToast(getActivity(), "正常调起支付宝支付");
                                            final String orderInfo = jsonObject.getString("data");
                                            Runnable payRunnable = new Runnable() {

                                                @Override
                                                public void run() {
                                                    PayTask alipay = new PayTask(getActivity());
                                                    Map<String, String> result = alipay.payV2(orderInfo, true);
                                                    Log.i("msp", result.toString());

                                                    Message msg = new Message();
                                                    msg.what = SDK_PAY_FLAG;
                                                    msg.obj = result;
                                                    mHandler.sendMessage(msg);
                                                }
                                            };

                                            Thread payThread = new Thread(payRunnable);
                                            payThread.start();
                                        } else {
                                            JSONObject data = jsonObject.getJSONObject("data");
                                            PayReq req = new PayReq();
                                            req.appId = data.getString("appid");
                                            req.partnerId = data.getString("partnerid");
                                            req.prepayId = data.getString("prepayid");
                                            req.nonceStr = data.getString("noncestr");
                                            req.timeStamp = data.getString("timestamp");
                                            req.packageValue = data.getString("package");
                                            req.sign = data.getString("sign");
                                            req.extData = "app data"; // optional
                                            AppToastMgr.shortToast(getActivity(), "正常调起支付");
                                            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                            api.sendReq(req);
                                        }
                                    } else {
                                        AppToastMgr.shortToast(getActivity(), " 获取充值信息失败！原因：" + errorMsg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    break;
                case R.id.close_one:
                    getDialog().dismiss();
                    break;
                case R.id.close_two:
                    rePayDetail.startAnimation(slide_left_to_left_in);
                    rePayDetail.setVisibility(View.VISIBLE);
                    LinPayWay.startAnimation(slide_left_to_right);
                    LinPayWay.setVisibility(View.GONE);
                    break;
                case R.id.btn_choose_payWays:
                    rePayDetail.startAnimation(slide_left_to_left_in);
                    rePayDetail.setVisibility(View.VISIBLE);
                    LinPayWay.startAnimation(slide_left_to_right);
                    LinPayWay.setVisibility(View.GONE);
                    if(iv_choosed_aliPay.getVisibility() == View.VISIBLE){
                        tv_payWays.setText("支付宝");
                    }
                    if(iv_choosed_wxPay.getVisibility() == View.VISIBLE){
                        tv_payWays.setText("微信");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        AppToastMgr.shortToast(mContext,"支付成功");
                        if(type == 3 || type == 4 || type ==5){
                            Intent intentMyExhchange = new Intent(getActivity(), MyExchangeActivity.class);
                            startActivity(intentMyExhchange);
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        AppToastMgr.shortToast(mContext,"支付失败");
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };
}
