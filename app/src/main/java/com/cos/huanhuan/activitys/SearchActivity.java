package com.cos.huanhuan.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.AppACache;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.views.FlowLayout;
import com.cos.huanhuan.views.TitleSearchBar;
import com.cos.huanhuan.views.TitleSearchBarSecond;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class SearchActivity extends AppCompatActivity {

    private TitleSearchBarSecond titleSearchBar;
    private String[] hotStringArr;
    private FlowLayout gv_search_history,gv_search_hot_history;
    private String[] historStringArr;
    private AppManager appManager;
    private static final String KEY_SEARCH_HISTORY_KEYWORD = "key_search_history_keyword";
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private ImageView clearHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        boolean isImmersive = false;
        if (hasKitKat() && !hasLollipop()) {
            isImmersive = true;
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isImmersive = true;
        }
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        mPref =  getSharedPreferences("history_search", Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initView();
        initData();
    }

    private void initView() {
        titleSearchBar = (TitleSearchBarSecond) findViewById(R.id.title_search_bar);
        gv_search_history = (FlowLayout) findViewById(R.id.gv_search_history);
        gv_search_hot_history = (FlowLayout) findViewById(R.id.gv_search_hot_history);
        clearHistory = (ImageView) findViewById(R.id.clearHistory);
        titleSearchBar.setLeftButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        titleSearchBar.setRightButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentHistory = new Intent(SearchActivity.this, SearchResultActivity.class);
                intentHistory.putExtra("sea",titleSearchBar.getEtText());
                startActivity(intentHistory);
                save();
            }
        });
        gv_search_history.setOnTagClickListener(new FlowLayout.OnTagClickListener() {
            @Override
            public void TagClick(String text) {
                Intent intentHistory = new Intent(SearchActivity.this, SearchResultActivity.class);
                intentHistory.putExtra("sea",text);
                startActivity(intentHistory);
            }
        });
        gv_search_hot_history.setOnTagClickListener(new FlowLayout.OnTagClickListener() {
            @Override
            public void TagClick(String text) {
                Intent intentHot = new Intent(SearchActivity.this, SearchResultActivity.class);
                intentHot.putExtra("sea",text);
                startActivity(intentHot);
            }
        });
        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanHistory();
            }
        });
    }

    private void initData(){
        gv_search_history.cleanTag();
        String history = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD,"");
        if (!AppStringUtils.isEmpty(history)){
            List<String> list = new ArrayList<String>();
            for(Object o : history.split(",")) {
                list.add((String)o);
            }
            historStringArr =  new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                historStringArr[i] = list.get(i);
            }
        }else{
            historStringArr = new String[]{};
        }
        gv_search_history.setData(historStringArr);
        HttpRequest.getHotSearch(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(SearchActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success){
                        gv_search_hot_history.cleanTag();
                        JSONArray arr = jsonObject.getJSONArray("list");
                        hotStringArr = new String[arr.length()];
                        for (int i = 0; i < arr.length(); i++) {
                            hotStringArr[i] = arr.getString(i);
                        }
                        gv_search_hot_history.setData(hotStringArr);
                    }else{
                        AppToastMgr.shortToast(SearchActivity.this, " 获取历史记录失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void save() {
        String text = titleSearchBar.getEtText();
        String oldText = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD,"");
        System.out.println("zlw======="+oldText);
        if (!AppStringUtils.isEmpty(text) && !oldText.contains(text)) {
            mEditor.putString(KEY_SEARCH_HISTORY_KEYWORD, text + "," + oldText);
            mEditor.commit();
        }
    }

    public void cleanHistory() {
        mEditor.putString(KEY_SEARCH_HISTORY_KEYWORD, "");
        mEditor.commit();
        mEditor.clear();
        historStringArr = new String[]{};
        gv_search_history.setData(new String[]{});
        gv_search_history.cleanTag();
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
