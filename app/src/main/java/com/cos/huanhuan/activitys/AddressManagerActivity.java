package com.cos.huanhuan.activitys;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.AddressAdapter;
import com.cos.huanhuan.model.AddressVO;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.views.TitleBar;

import java.util.ArrayList;
import java.util.List;

public class AddressManagerActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private AppManager appManager;
    private String userId;
    private ListView listView;
    private AddressAdapter addressAdapter;
    private List<AddressVO> listAddress;
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
        setTitle(this.getResources().getString(R.string.chooseAddress));
        setBaseContentView(R.layout.activity_address_manager);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        userId = getUserId();
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        setRightButton(new TitleBar.TextAction(this.getResources().getString(R.string.manage)) {
            @Override
            public void performAction(View view) {
                
            }
        });
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_address_manager);
        listView.setOnItemClickListener(this);

        listAddress = new ArrayList<AddressVO>();
        addressAdapter = new AddressAdapter(AddressManagerActivity.this,listAddress);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

    }
}
