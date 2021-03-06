package com.cos.huanhuan.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.cos.huanhuan.activitys.ComfirmExchangeActivity;
import com.cos.huanhuan.activitys.MyExchangeActivity;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.Constants;
import com.cos.huanhuan.utils.SharedPreferencesHelper;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	private SharedPreferencesHelper sharedPreferencesHelper;
    private IWXAPI api;
	private AppManager appManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//setContentView(R.layout.pay_result);
		sharedPreferencesHelper = new SharedPreferencesHelper(WXPayEntryActivity.this);
		appManager = AppManager.getAppManager();
		appManager.addActivity(this);
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {int code = resp.errCode;
			if (code == 0){
				//显示充值成功的页面和需要的操作
				AppToastMgr.shortToast(WXPayEntryActivity.this,"充值成功");
				appManager.finishActivity(ComfirmExchangeActivity.class);
				Boolean isRecharge = (Boolean) sharedPreferencesHelper.get("isRecharge",false);
				if(!isRecharge){
					Intent intentMyExchange = new Intent(WXPayEntryActivity.this, MyExchangeActivity.class);
					startActivity(intentMyExchange);
				}
			}

			if (code == -1){
				//错误
			}
			if (code == -2){
				AppToastMgr.shortToast(WXPayEntryActivity.this,"您取消了付款");
			}
			WXPayEntryActivity.this.finish();
		}
	}
}