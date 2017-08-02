package com.cos.huanhuan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cos.huanhuan.model.ShareAdapter;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;

import java.util.ArrayList;

/**
 * Created by wangfei on 16/11/9.
 */
public class ShareActivity extends Activity{
    private ListView listView;
    private ShareAdapter shareAdapter;
    public ArrayList<SnsPlatform> platforms = new ArrayList<SnsPlatform>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.umeng_blue));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //判断该应用是否有写SD卡权限，如果没有再去申请
                if (ContextCompat.checkSelfPermission(ShareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShareActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                }
            }
        }
        setContentView(R.layout.umeng_share);
        listView = (ListView) findViewById(R.id.list);
        initPlatforms();
        shareAdapter  = new ShareAdapter(this,platforms);
        listView.setAdapter(shareAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShareActivity.this,ShareDetailActivity.class);
                intent.putExtra("platform",platforms.get(position).mPlatform);
                ShareActivity.this.startActivity(intent);
            }
        });
        ((TextView)findViewById(R.id.umeng_title)).setText(R.string.umeng_share_title);
        findViewById(R.id.umeng_back).setVisibility(View.VISIBLE);
        findViewById(R.id.umeng_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

    }

    // 调用requestPermissions会弹出对话框，用户做出选择之后的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //requestCode 是调用requestPermissions传入的123，当然你可以设置成其他值或者某个静态变量
        if (requestCode == 123) {
            if (grantResults.length >= 1) {
                //因为我们只申请了一个权限，所以这个数组只有一个
                int writeResult = grantResults[0];
                //判断是否授权，也就是用户点击的是拒绝还是接受
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;
                if (writeGranted) {
                    //用户点击了接受，可以进行相应处理
                } else {
                    //用户点击了拒绝，可以进行相应处理
                }
            }
        }
    }

    private void initPlatforms(){
        platforms.clear();
        platforms.add(SHARE_MEDIA.WEIXIN.toSnsPlatform());
        platforms.add(SHARE_MEDIA.WEIXIN_CIRCLE.toSnsPlatform());
        platforms.add(SHARE_MEDIA.WEIXIN_FAVORITE.toSnsPlatform());
        platforms.add(SHARE_MEDIA.SINA.toSnsPlatform());
        platforms.add(SHARE_MEDIA.QQ.toSnsPlatform());
        platforms.add(SHARE_MEDIA.QZONE.toSnsPlatform());
    }
}
