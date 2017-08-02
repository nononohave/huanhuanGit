package com.cos.huanhuan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cos.huanhuan.adapter.ImageGridAdapter;
import com.cos.huanhuan.model.MultiPartStack;
import com.cos.huanhuan.model.MultipartRequest;
import com.cos.huanhuan.utils.SelectDialog;
import com.foamtrace.photopicker.ImageCaptureManager;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.PhotoPreviewActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView img;
    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private static final int RETURN_CAMERA_CODE = 123;
    private static final int RETURN_PHOTOS_CODE = 124;
    private ArrayList<String> imagePaths = null;
    private GridView gridView;
    private ImageGridAdapter imageGridAdapter;
    private ImageCaptureManager captureManager; // 相机拍照处理类
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.umeng_socialize_text_title));
        }
        System.out.print(1110);
        setContentView(R.layout.activity_main);
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.auth).setOnClickListener(this);
        findViewById(R.id.shareBoard).setOnClickListener(this);
        findViewById(R.id.qqAuth).setOnClickListener(this);
        findViewById(R.id.wbAuth).setOnClickListener(this);
        findViewById(R.id.wxAuth).setOnClickListener(this);
        findViewById(R.id.chosePhotos).setOnClickListener(this);
        findViewById(R.id.uploadImg).setOnClickListener(this);
        img = (ImageView)findViewById(R.id.img);
        gridView = (GridView) findViewById(R.id.gridView);
        //Picasso.with(this).load("http://cdn2.jianshu.io/assets/web/logo-58fd04f6f0de908401aa561cda6a0688.png").placeholder(R.drawable.fenxiang).into(img);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share:
//                Intent shareintent = new Intent(MainActivity.this, ShareActivity.class);
//                startActivity(shareintent);
                break;
            case R.id.auth:
                Intent authintent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(authintent);

               //UMShareAPI.get(MainActivity.this).doOauthVerify(MainActivity.this, SHARE_MEDIA.QQ, authListener);

                //UMShareAPI.get(MainActivity.this).getPlatformInfo(MainActivity.this, SHARE_MEDIA.QQ, authListener);

               //UMShareAPI.get(MainActivity.this).getPlatformInfo(MainActivity.this, SHARE_MEDIA.SINA, authListener);
                break;
            case R.id.shareBoard:
                UMImage image = new UMImage(MainActivity.this, "http://www.cnblogs.com/skins/CodingLife/images/title-yellow.png");//网络图片
                new ShareAction(MainActivity.this)
                        .withMedia(image)
                        .setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA)
                        .setCallback(umShareListener)
                        .open();
                break;
            case R.id.qqAuth:
                UMShareAPI.get(MainActivity.this).getPlatformInfo(MainActivity.this, SHARE_MEDIA.QQ, authListener);
                break;
            case R.id.wbAuth:
                UMShareAPI.get(MainActivity.this).getPlatformInfo(MainActivity.this, SHARE_MEDIA.SINA, authListener);
                break;
            case R.id.chosePhotos:
                List<String> names = new ArrayList<>();
                names.add("拍照");
                names.add("相册");
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0: // 直接调起相机
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //判断该应用是否有写SD卡权限，如果没有再去申请
                                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA}, RETURN_CAMERA_CODE);
                                    }else{
                                        takePhotos();
                                    }
                                }else{
                                    takePhotos();
                                }
                                break;
                            case 1:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //判断该应用是否有写SD卡权限，如果没有再去申请
                                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RETURN_PHOTOS_CODE);
                                    }else{
                                        choosePhotos();
                                    }
                                }else{
                                    choosePhotos();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }, names);
                break;
            case R.id.uploadImg:
                uploadImg();
                break;
        }
    }

    Response.Listener<String> mResonseListenerString = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {
            Log.i("2", response.toString());
        }
    };

    Response.ErrorListener mErrorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error != null) {
                if (error.networkResponse != null)
                    Log.i("3", " error "
                            + new String(error.networkResponse.data));
            }
        }
    };
    /**
     * 上传图片的方法
     */
    private void uploadImg() {
        List<File> listFile = new ArrayList<File>();
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < imagePaths.size(); i++) {
            File file = new File(imagePaths.get(i));
            listFile.add(file);
            params.put("file" + i,imagePaths.get(i));
        }
        MultipartRequest multipartRequest = new MultipartRequest("http://feifeijiuniu.s1.natapp.cc/appuser/uploadImg",mErrorListener,mResonseListenerString,"file",listFile,params);
        multipartRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue mSingleQueue = Volley.newRequestQueue(this,new MultiPartStack());
        mSingleQueue.add(multipartRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RETURN_CAMERA_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhotos();
                }else{
                    Toast.makeText(this, "获取拍照权限失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
        }
         if(requestCode == RETURN_PHOTOS_CODE){
             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 choosePhotos();
             }else{
                 Toast.makeText(this, "获取存储权限失败！", Toast.LENGTH_SHORT).show();
                 return;
             }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void choosePhotos() {
        PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
        intent.setSelectModel(SelectModel.MULTI);
        intent.setShowCarema(false); // 是否显示拍照， 默认false
        intent.setMaxTotal(9); // 最多选择照片数量，默认为9
        intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
        //intent.setImageConfig(config);
        MainActivity.this.startActivityForResult(intent, REQUEST_CAMERA_CODE);
    }

    private void takePhotos() {
        try {
            if (captureManager == null) {
                captureManager = new ImageCaptureManager(MainActivity.this);
            }
            Intent intentCapture = captureManager.dispatchTakePictureIntent();
            File newFile = createImageFile();
            Uri contentUri = FileProvider.getUriForFile(this, "com.cos.huanhuan.fileprovider", newFile);
            List<ResolveInfo> resInfoList= getPackageManager().queryIntentActivities(intentCapture, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, contentUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            startActivityForResult(intentCapture, ImageCaptureManager.REQUEST_TAKE_PHOTO);

        } catch (IOException e) {
            Toast.makeText(MainActivity.this, com.foamtrace.photopicker.R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            if (!storageDir.mkdir()) {
                throw new IOException();
            }
        }
        File image = new File(storageDir, imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
       // mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT));
                    break;
                // 调用相机拍照
                case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                    if(captureManager.getCurrentPhotoPath() != null) {
                        captureManager.galleryAddPic();

                        ArrayList<String> paths = new ArrayList<>();
                        paths.add(captureManager.getCurrentPhotoPath());
                        loadAdpater(paths);
                    }
                    break;

            }
        }
    }

    private void loadAdpater(ArrayList<String> paths){
        if(imagePaths == null){
            imagePaths = new ArrayList<>();
        }
        imagePaths.clear();
        imagePaths.addAll(paths);

        try{
            JSONArray obj = new JSONArray(imagePaths);
            Log.e("--", obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        if(imageGridAdapter == null){
            imageGridAdapter = new ImageGridAdapter(MainActivity.this,imagePaths);
            gridView.setAdapter(imageGridAdapter);
        }else {
            imageGridAdapter.notifyDataSetChanged();
        }
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    private UMShareListener umShareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this,"成功了",Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MainActivity.this,"失败"+t.getMessage(),Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this,"取消了",Toast.LENGTH_LONG).show();
        }
    };

    //注意先需要调用一下获取doOauthVerify进行授权然后再掉获取用户资料的方法
    private UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this, "test", Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_LONG).show();

        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

            Toast.makeText(MainActivity.this, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {

            Toast.makeText(MainActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };
}
