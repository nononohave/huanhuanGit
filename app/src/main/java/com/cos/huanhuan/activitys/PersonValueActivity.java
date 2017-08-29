package com.cos.huanhuan.activitys;

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
import com.cos.huanhuan.model.RechargeMoney;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        initView();
        initData();
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
                rechargeMoney.setMoney(10.0);
            }else if(i == 1){
                rechargeMoney.setMoney(30.0);
            }else if(i == 2){
                rechargeMoney.setMoney(50.0);
            }else if(i == 3){
                rechargeMoney.setMoney(100.0);
            }else if(i == 4){
                rechargeMoney.setMoney(200.0);
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
                AppToastMgr.shortToast(PersonValueActivity.this,"立即充值");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        for (int i = 0; i < listRecharge.size(); i++) {
            if(position == i){
                listRecharge.get(i).setClick(true);
            }else{
                listRecharge.get(i).setClick(false);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
