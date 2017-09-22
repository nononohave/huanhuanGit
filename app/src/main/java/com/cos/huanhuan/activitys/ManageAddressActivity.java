package com.cos.huanhuan.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.AddressAdapter;
import com.cos.huanhuan.model.AddressVO;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.views.TitleBar;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 地址管理
 */
public class ManageAddressActivity extends BaseActivity{

    private AppManager appManager;
    private String userId;
    private ListView listView;
    private AddressAdapter addressAdapter;
    private List<AddressVO> listAddress;
    private ManageAddressActivity.MyHandler handler;
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
        handler = new ManageAddressActivity.MyHandler();
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
//        setRightButton(new TitleBar.TextAction(this.getResources().getString(R.string.manage)) {
//            @Override
//            public void performAction(View view) {
//                //编辑栏展示隐藏
//                if(listAddress != null && listAddress.size() > 0){
//                    if(listAddress.get(0).getManage()){
//                        for (int i = 0; i < listAddress.size(); i++) {
//                            listAddress.get(i).setManage(false);
//                        }
//                    }else{
//                        for (int i = 0; i < listAddress.size(); i++) {
//                            listAddress.get(i).setManage(true);
//                        }
//                    }
//                }
//                addressAdapter.notifyDataSetChanged();
//            }
//        });
        setRightButton(new TitleBar.TextAction(this.getResources().getString(R.string.add)) {
            @Override
            public void performAction(View view) {
                Intent intentAdd = new Intent(ManageAddressActivity.this,AddNewAddressActivity.class);
                intentAdd.putExtra("isConfirm",false);
                startActivity(intentAdd);
            }
        });
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_address_manager);

        listAddress = new ArrayList<AddressVO>();
        addressAdapter = new AddressAdapter(ManageAddressActivity.this,listAddress);
        listView.setAdapter(addressAdapter);
        //默认地址选择
        addressAdapter.setImageButtonClick(new AddressAdapter.ImageButtonClick() {
            @Override
            public void imageButtonClick(View view, final int position) {
                ImageButton imgBtn = (ImageButton)view;
                HttpRequest.setDefaultAddress(String.valueOf(listAddress.get(position).getId()), new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        toastErrorMsg(ManageAddressActivity.this,"请求失败！");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            if (null != response.cacheResponse()) {
                            } else {
                                try {
                                    String str1 = response.body().string();
                                    JSONObject jsonObject = new JSONObject(str1);
                                    Boolean success = jsonObject.getBoolean("success");
                                    if(success){
                                        Message message=new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("position", String.valueOf(position));
                                        message.setData(bundle);
                                        handler.sendMessage(message);//发送message信息
                                        message.what=1;//标志是哪个线程传数据
                                    }else{
                                        String errorMsg = jsonObject.getString("errorMsg");
                                        toastErrorMsg(ManageAddressActivity.this, errorMsg);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        //编辑点击
        addressAdapter.setEditClick(new AddressAdapter.EditClick() {
            @Override
            public void editClick(View view, int position) {
                Intent editIntent = new Intent(ManageAddressActivity.this, EditAddrssActivity.class);
                editIntent.putExtra("addressId",listAddress.get(position).getId());
                editIntent.putExtra("data",listAddress.get(position));
                startActivity(editIntent);
            }
        });

        //删除选择
        addressAdapter.setDeleteClick(new AddressAdapter.DeleteClick() {
            @Override
            public void deleteClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageAddressActivity.this);
                builder.setTitle("确认删除地址吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAddress(position);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
    private void deleteAddress(final int position) {
        HttpRequest.deleteAddress(String.valueOf(listAddress.get(position).getId()), new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                toastErrorMsg(ManageAddressActivity.this, "请求失败！");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (null != response.cacheResponse()) {
                    } else {
                        try {
                            String str1 = response.body().string();
                            JSONObject jsonObject = new JSONObject(str1);
                            Boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("position", String.valueOf(position));
                                message.setData(bundle);
                                handler.sendMessage(message);//发送message信息
                                message.what = 2;//标志是哪个线程传数据
                            } else {
                                String errorMsg = jsonObject.getString("errorMsg");
                                toastErrorMsg(ManageAddressActivity.this,errorMsg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initData() {
        HttpRequest.getMembersAddress(userId, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                toastErrorMsg(ManageAddressActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success){
                        listAddress.removeAll(listAddress);
                        JSONArray arr = jsonObject.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            AddressVO addressVO = JsonUtils.fromJson(arr.getJSONObject(i).toString(),AddressVO.class);
                            addressVO.setManage(true);
                            listAddress.add(addressVO);
                        }
                        addressAdapter.notifyDataSetChanged();
                    }else{
                        toastErrorMsg(ManageAddressActivity.this, " 登录失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                Bundle bundle = msg.getData();
                int position = Integer.valueOf(bundle.getString("position"));
                if(listAddress.get(position).getDefault()){
                    for (int i = 0; i <listAddress.size(); i++) {
                        listAddress.get(i).setDefault(false);
                    }
                }else{
                    for (int i = 0; i <listAddress.size(); i++) {
                        if(i == position){
                            listAddress.get(i).setDefault(true);
                        }else{
                            listAddress.get(i).setDefault(false);
                        }
                    }
                }
                addressAdapter.notifyDataSetChanged();
            }else if(msg.what == 2){
                Bundle bundle = msg.getData();
                int position = Integer.valueOf(bundle.getString("position"));
                listAddress.remove(position);
                addressAdapter.notifyDataSetChanged();
            }

        }
    }
}
