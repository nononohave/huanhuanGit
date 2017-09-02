package com.cos.huanhuan.activitys;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cos.huanhuan.R;
import com.cos.huanhuan.views.TitleBar;

/**
 * Created by Administrator on 2017/9/2.
 */

public class ConversationActivity extends FragmentActivity {
    private TitleBar titleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        initView();
    }

    private void initView() {
    }
}
