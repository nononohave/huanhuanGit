package com.cos.huanhuan.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.cos.huanhuan.R;
import com.cos.huanhuan.activitys.AllExchangeActivity;
import com.cos.huanhuan.activitys.CooperateDetailActivity;
import com.cos.huanhuan.activitys.LoginActivity;
import com.cos.huanhuan.activitys.PublishCoopActivity;
import com.cos.huanhuan.activitys.PublishExchangeActivity;
import com.cos.huanhuan.adapter.CardGridAdapter;
import com.cos.huanhuan.adapter.CoopCardGridAdapter;
import com.cos.huanhuan.model.CardCoop;
import com.cos.huanhuan.model.CardExchange;
import com.cos.huanhuan.model.Classify;
import com.cos.huanhuan.model.CoopList;
import com.cos.huanhuan.model.ExchangeList;
import com.cos.huanhuan.model.JsonBean;
import com.cos.huanhuan.model.PublishCoop;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DensityUtils;
import com.cos.huanhuan.utils.FastBlur;
import com.cos.huanhuan.utils.GetJsonDataUtil;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.SpacesItemDecoration;
import com.cos.huanhuan.views.TitleBar;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CooperateFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{

    private TitleBar titleBar;
    private ViewGroup contentView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager mLayoutManager;
    private LinearLayout ll_request_classify,ll_address_choose,ll_fragment_coop;
    private TextView tv_request_classify,tv_address_choose;

    private CoopCardGridAdapter coopCardGridAdapter;
    private List<CardCoop> listCardCoop;
    private int pageIndex = 0;
    private int pageNum = 6;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private OptionsPickerView addressOptionsPick;
    private int selectOptions1 = 0, selectOptions2 = 0;
    private String provice,cityStr;
    private List<Classify> listClassify;
    private List<String> listClassifyString;
    private PopViewListAdapter adapter;
    private View popupView;
    private String cid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cooperate, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean isImmersive = false;
        if (hasKitKat() && !hasLollipop()) {
            isImmersive = true;
            //透明状态栏
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isImmersive = true;
        }
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        titleBar = (TitleBar) getActivity().findViewById(R.id.coop_title_bar);

        //初始化控件
        contentView = (ViewGroup) getActivity().findViewById(R.id.base_coop_contentView);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.coop_grid_recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.grid_swipe_coop_refresh);
        ll_request_classify = (LinearLayout) getActivity().findViewById(R.id.ll_request_classify);
        ll_fragment_coop = (LinearLayout) getActivity().findViewById(R.id.ll_fragment_coop);
        ll_address_choose = (LinearLayout)getActivity().findViewById(R.id.ll_address_choose);
        tv_request_classify = (TextView) getActivity().findViewById(R.id.tv_request_classify);
        tv_address_choose = (TextView) getActivity().findViewById(R.id.tv_address_choose);

        mLayoutManager=new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        recyclerView.setLayoutManager(mLayoutManager);
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        int leftRight = DensityUtils.dip2px(getActivity(),5);
        int topBottom = DensityUtils.dip2px(getActivity(),0);
        recyclerView.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom));

        //设置标题
        titleBar.setBackgroundColor(getResources().getColor(R.color.white));
        titleBar.setDividerColor(R.color.dividLineColor);
        titleBar.setActionTextColor(getResources().getColor(R.color.titleBarTextColor));
        titleBar.setTitleColor(getResources().getColor(R.color.titleBarTextColor));
        titleBar.setTitle(this.getResources().getString(R.string.cooperate));
        final View popPulishView = LayoutInflater.from(getActivity()).inflate(R.layout.popwindow_publish, null);
        final ImageView backBlurImg = (ImageView)popPulishView.findViewById(R.id.back_blur_pop);
        final RelativeLayout rl = (RelativeLayout) popPulishView.findViewById(R.id.back_rl_blur);
        ImageView imageClose = (ImageView) popPulishView.findViewById(R.id.close_publish_popWindow);
        LinearLayout publishExchange = (LinearLayout) popPulishView.findViewById(R.id.ll_popWindow_publishExchange);
        LinearLayout publishCoo = (LinearLayout) popPulishView.findViewById(R.id.ll_popWindow_publishCoo);
        titleBar.addAction(new TitleBar.TextAction(this.getResources().getString(R.string.publish)) {
            @Override
            public void performAction(View view) {
                ViewUtils.showPopupWindow(getActivity(),titleBar,5,popPulishView);
                Bitmap scaledBitmap = FastBlur.doBlur(ViewUtils.takeScreenShot(getActivity()), 15, true);
                backBlurImg.setVisibility(View.VISIBLE);
                rl.bringToFront();
                backBlurImg.setImageBitmap(scaledBitmap);
            }
        });
        imageClose.setOnClickListener(this);
        publishExchange.setOnClickListener(this);
        publishCoo.setOnClickListener(this);
        ll_request_classify.setOnClickListener(this);
        ll_address_choose.setOnClickListener(this);

        listCardCoop = new ArrayList<CardCoop>();
        coopCardGridAdapter = new CoopCardGridAdapter(getActivity(),listCardCoop);
        recyclerView.setAdapter(coopCardGridAdapter);

        coopCardGridAdapter.setOnImageClick(new CoopCardGridAdapter.OnImageClick() {
            @Override
            public void OnImageClick(View view, int position) {
                AppToastMgr.shortToast(getActivity(),"选中"+position);
                Intent intent = new Intent(getActivity(), CooperateDetailActivity.class);
                intent.putExtra("coopId",listCardCoop.get(position).getCardId());//传入合作id
                startActivity(intent);
            }
        });
        coopCardGridAdapter.setOnUserClick(new CoopCardGridAdapter.OnUserClick() {
            @Override
            public void OnUserClick(View view, int position) {
                AppToastMgr.shortToast(getActivity(),"选中"+position);
            }
        });


        listClassify = new ArrayList<Classify>();
        listClassifyString = new ArrayList<>();
        popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popwindow_coop, null);
        ListView listView = (ListView)popupView.findViewById(R.id.popListView);
        adapter = new PopViewListAdapter(getActivity(),listClassifyString);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        initData();
        initJsonData();
        CoopList coopList = new CoopList();
        getData(coopList);
        cid = "";
        cityStr = "";
    }

    private void initData() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CoopList coopList = new CoopList();
                if(AppStringUtils.isNotEmpty(cid)){
                    coopList.setCid(cid);
                }
                if(AppStringUtils.isNotEmpty(cityStr)){
                    coopList.setCity(cityStr);
                }
                getData(coopList);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==coopCardGridAdapter.getItemCount()) {
                    CoopList coopList = new CoopList();
                    if(AppStringUtils.isNotEmpty(cid)){
                        coopList.setCid(cid);
                    }
                    if(AppStringUtils.isNotEmpty(cityStr)){
                        coopList.setCity(cityStr);
                    }
                    getDataAdd(coopList);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem=layoutManager.findLastVisibleItemPosition();
            }
        });
        HttpRequest.getCoopClass(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(getActivity(),"请求分类接口失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success) {
                        listClassify.removeAll(listClassify);
                        listClassifyString.removeAll(listClassifyString);
                        JSONArray arr = jsonObject.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            Classify classify = new Classify();
                            String id = arr.getJSONObject(i).getString("id");
                            String className = arr.getJSONObject(i).getString("className");
                            String classUsName = arr.getJSONObject(i).getString("classUsName");
                            classify.setClassifyId(id);
                            classify.setClassName(className);
                            classify.setClassUsName(classUsName);
                            listClassify.add(classify);
                            listClassifyString.add(className);
                        }
                    }else{
                        AppToastMgr.shortToast(getActivity(), " 请求分类接口失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getData(CoopList coopList) {
        pageIndex = 1;
        coopList.setPageIndex(pageIndex);
        coopList.setPageSize(pageNum);
        HttpRequest.getCooperateList(coopList, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(getActivity(),"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success) {
                        JSONObject obj = jsonObject.getJSONObject("data");
                        JSONArray arr = obj.getJSONArray("data");
                        listCardCoop.removeAll(listCardCoop);
                        for (int i = 0; i < arr.length(); i++) {
                            CardCoop cardCoop = new CardCoop();
                            String id = arr.getJSONObject(i).getString("id");
                            String title = arr.getJSONObject(i).getString("title");
                            String nickname = arr.getJSONObject(i).getString("nickname");
                            String cover = arr.getJSONObject(i).getString("cover");
                            int userId = arr.getJSONObject(i).getInt("userId");
                            String city = arr.getJSONObject(i).getString("city");
                            int personNum = arr.getJSONObject(i).getInt("personNum");
                            String portrait = arr.getJSONObject(i).getString("portrait");
                            cardCoop.setPortrait(portrait);
                            cardCoop.setCardId(id);
                            cardCoop.setCardTitle(title);
                            cardCoop.setCreateName(nickname);
                            cardCoop.setCardImgUrl(HttpRequest.IMG_HUANHUAN_HOST + cover);
                            cardCoop.setUserId(userId);
                            cardCoop.setAddress(city);
                            cardCoop.setPersonNum(personNum);
                            listCardCoop.add(cardCoop);
                        }
                        coopCardGridAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        AppToastMgr.shortToast(getActivity(), " 请求失败！原因：" + errorMsg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getDataAdd(CoopList coopList) {
        pageIndex = pageIndex + 1;
        coopList.setPageIndex(pageIndex);
        coopList.setPageSize(pageNum);
        HttpRequest.getCooperateList(coopList, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(getActivity(),"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success) {
                        JSONObject obj = jsonObject.getJSONObject("data");
                        JSONArray arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            CardCoop cardCoop = new CardCoop();
                            String id = arr.getJSONObject(i).getString("id");
                            String title = arr.getJSONObject(i).getString("title");
                            String nickname = arr.getJSONObject(i).getString("nickname");
                            String cover = arr.getJSONObject(i).getString("cover");
                            int userId = arr.getJSONObject(i).getInt("userId");
                            String city = arr.getJSONObject(i).getString("city");
                            int personNum = arr.getJSONObject(i).getInt("personNum");
                            String portrait = arr.getJSONObject(i).getString("portrait");
                            cardCoop.setPortrait(portrait);
                            cardCoop.setCardId(id);
                            cardCoop.setCardTitle(title);
                            cardCoop.setCreateName(nickname);
                            cardCoop.setCardImgUrl(HttpRequest.IMG_HUANHUAN_HOST + cover);
                            cardCoop.setUserId(userId);
                            cardCoop.setAddress(city);
                            cardCoop.setPersonNum(personNum);
                            listCardCoop.add(cardCoop);
                        }
                        coopCardGridAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        AppToastMgr.shortToast(getActivity(), " 请求失败！原因：" + errorMsg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_publish_popWindow:
                ViewUtils.dismissPopup();
                break;
            case R.id.ll_popWindow_publishExchange:
                ViewUtils.dismissPopup();
                Intent intent = new Intent(getActivity(), PublishExchangeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_popWindow_publishCoo:
                ViewUtils.dismissPopup();
                Intent intentCoop = new Intent(getActivity(), PublishCoopActivity.class);
                startActivity(intentCoop);
                break;
            case R.id.ll_request_classify:
                ViewUtils.showPopupWindow(getActivity(),ll_request_classify,7,popupView);
                break;
            case R.id.ll_address_choose:
                if(options1Items != null && options1Items.size() > 0
                        && options2Items != null && options2Items.size() > 0) {
                    addressOptionsPick = new OptionsPickerView.Builder(getActivity(), new OptionsPickerView.OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            //返回的分别是三个级别的选中位置
                            selectOptions1 = options1;
                            selectOptions2 = options2;
                            String tx = options1Items.get(options1).getPickerViewText()
                                    + options2Items.get(options1).get(options2);
                            tv_address_choose.setText(tx);
                            //tv_request_classify,tv_address_choose
                            provice = options1Items.get(options1).getPickerViewText();
                            cityStr = options2Items.get(options1).get(options2);
                            CoopList coopList = new CoopList();
                            coopList.setCity(options2Items.get(options1).get(options2));
                            if(AppStringUtils.isNotEmpty(cid)){
                                coopList.setCid(cid);
                            }
                            getData(coopList);
                        }
                    }).setContentTextSize(20)
                            .setSelectOptions(selectOptions1, selectOptions2, 0)
                            .setTitleText("选择市")//标题文字
                            .setCancelColor(getResources().getColor(R.color.titleBarTextColor))
                            .setSubmitColor(getResources().getColor(R.color.titleBarTextColor))
                            .build();
                    addressOptionsPick.setPicker(options1Items, options2Items, null);//添加数据源
                    addressOptionsPick.show();
                }else{
                    AppToastMgr.shortToast(getActivity(),"未获取到地址信息");
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
        String JsonData = new GetJsonDataUtil().getJson(getActivity(),"province.json");//获取assets目录下的json文件数据

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ViewUtils.dismissPopup();
        CoopList coopList = new CoopList();
        cid = listClassify.get(i).getClassifyId();
        tv_request_classify.setText(listClassify.get(i).getClassName());
        coopList.setCid(cid);
        if(AppStringUtils.isNotEmpty(cityStr)){
            coopList.setCity(cityStr);
        }
        getData(coopList);
    }

    class PopViewListAdapter extends BaseAdapter {

        private Context context;
        private List<String> list;

        public PopViewListAdapter(Context context,List<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.pop_list_item, null);
            }
            TextView tv = (TextView)convertView.findViewById(R.id.popWindowText);
            tv.setText(list.get(position));
            return convertView;
        }
    }
}
