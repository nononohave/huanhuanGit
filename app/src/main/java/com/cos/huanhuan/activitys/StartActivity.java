package com.cos.huanhuan.activitys;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.cos.huanhuan.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 800);
    }

    class splashhandler implements Runnable {

        public void run() {
            Intent intent = new Intent(getApplication(),LoginActivity.class);
            startActivity(intent);
            StartActivity.this.finish();
        }

    }
}
