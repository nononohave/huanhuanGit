package com.cos.huanhuan.activitys;

import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.cos.huanhuan.R;
import com.cos.huanhuan.model.AddressDTO;
import com.cos.huanhuan.model.AddressVO;
import com.cos.huanhuan.model.JsonBean;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.GetJsonDataUtil;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.views.TitleBar;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class EditAddrssActivity extends BaseActivity implements View.OnClickListener{
    private AppManager appManager;

    private EditText et_consignee,et_phone,et_detail_address;
    private RelativeLayout choose_local;
    private TextView address_city;

    private Boolean isDefault = false;
    private String userId;
    private OptionsPickerView addressOptionsPick;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private int selectOptions1 = 0, selectOptions2 = 0, selectOptions3 = 0;
    private String provice,city,district;
    private AddressVO addressVO;
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
        setTitle(this.getResources().getString(R.string.editAddress));
        setBaseContentView(R.layout.activity_edit_addrss);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        userId = getUserId();
        addressVO = (AddressVO) getIntent().getSerializableExtra("data");
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        setRightButton(new TitleBar.TextAction(this.getResources().getString(R.string.save)) {
            @Override
            public void performAction(View view) {
                saveAddress();
            }
        });
        initView();
        initJsonData();
    }

    private void initView() {
        et_consignee = (EditText) findViewById(R.id.et_addAddress_consignee);
        et_phone = (EditText) findViewById(R.id.et_addAddress_phone);
        et_detail_address = (EditText) findViewById(R.id.et_address_detail);
        choose_local = (RelativeLayout) findViewById(R.id.rl_choose_local);
        address_city = (TextView) findViewById(R.id.tv_address_city);

        choose_local.setOnClickListener(this);

        if(addressVO != null){
            et_consignee.setText(addressVO.getName());
            et_consignee.setSelection(addressVO.getName().length());
            et_phone.setText(addressVO.getPhone());
            et_detail_address.setText(addressVO.getAddress());
            provice = addressVO.getProvince();
            city = addressVO.getCity();
            district = addressVO.getCounty();
            address_city.setText(provice + city + district);
        }
    }
    @Override
    public void onClick(View view) {
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                    0);
        }
        switch (view.getId()){
            case R.id.rl_choose_local:
                if(options1Items != null && options1Items.size() > 0
                        && options2Items != null && options2Items.size() > 0
                        && options3Items != null && options3Items.size() > 0) {
                    addressOptionsPick = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            //返回的分别是三个级别的选中位置
                            selectOptions1 = options1;
                            selectOptions2 = options2;
                            selectOptions3 = options3;
                            String tx = options1Items.get(options1).getPickerViewText()
                                    + options2Items.get(options1).get(options2)
                                    + options3Items.get(options1).get(options2).get(options3);
                            address_city.setText(tx);
                            provice = options1Items.get(options1).getPickerViewText();
                            city = options2Items.get(options1).get(options2);
                            district = options3Items.get(options1).get(options2).get(options3);
                        }
                    }).setContentTextSize(20)
                            .setSelectOptions(selectOptions1, selectOptions2, selectOptions3)
                            .setTitleText("选择地址")//标题文字
                            .setCancelColor(getResources().getColor(R.color.titleBarTextColor))
                            .setSubmitColor(getResources().getColor(R.color.titleBarTextColor))
                            .build();
                    addressOptionsPick.setPicker(options1Items, options2Items, options3Items);//添加数据源
                    addressOptionsPick.show();
                }else{
                    toastErrorMsg(EditAddrssActivity.this,"未获取到地址信息");
                }
                break;
        }
    }
    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this,"province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i=0;i<jsonBean.size();i++){//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c=0; c<jsonBean.get(i).getCityList().size(); c++){//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        ||jsonBean.get(i).getCityList().get(c).getArea().size()==0) {
                    City_AreaList.add("");
                }else {

                    for (int d=0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    /**
     * 保存地址信息
     */
    private void saveAddress() {
        String consignee = et_consignee.getText().toString();
        String phone = et_phone.getText().toString();
        String address = et_detail_address.getText().toString();

        if(AppStringUtils.isEmpty(consignee)){
            toastErrorMsg(EditAddrssActivity.this,"请输入收件人姓名");
            return;
        }
        if(AppStringUtils.isEmpty(phone)){
            toastErrorMsg(EditAddrssActivity.this,"请输入收件人手机号");
            return;
        }
        if(AppStringUtils.isEmpty(provice) && AppStringUtils.isEmpty(city) && AppStringUtils.isEmpty(district)){
            toastErrorMsg(EditAddrssActivity.this,"请选择省市区");
            return;
        }
        if(AppStringUtils.isEmpty(address)){
            toastErrorMsg(EditAddrssActivity.this,"请输入详细地址");
            return;
        }
        AddressVO addressDTO = new AddressVO();
        addressDTO.setId(addressVO.getId());
        addressDTO.setUserId(Integer.valueOf(userId));
        addressDTO.setProvince(provice);
        addressDTO.setCity(city);
        addressDTO.setCounty(district);
        addressDTO.setAddress(address);
        addressDTO.setZipCode("000000");
        addressDTO.setName(consignee);
        addressDTO.setPhone(phone);
        HttpRequest.editAddress(addressDTO, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                toastErrorMsg(EditAddrssActivity.this,"请求失败！");
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
                                toastErrorMsg(EditAddrssActivity.this,"修改成功");
                                appManager.finishActivity(EditAddrssActivity.this);
                            } else {
                                String errorMsg = jsonObject.getString("errorMsg");
                                toastErrorMsg(EditAddrssActivity.this,errorMsg);
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
}
