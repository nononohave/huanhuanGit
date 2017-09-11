package com.cos.huanhuan.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.cos.huanhuan.R;
import com.cos.huanhuan.model.MultiPartStack;
import com.cos.huanhuan.model.MultipartRequest;
import com.cos.huanhuan.model.PersonData;
import com.cos.huanhuan.model.UserValueData;
import com.cos.huanhuan.utils.AppACache;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.GetUriPath;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.PicassoUtils;
import com.cos.huanhuan.utils.SelectDialog;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.CircleImageView;
import com.foamtrace.photopicker.ImageCaptureManager;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalDataActivity extends BaseActivity implements View.OnClickListener{

    private AppManager appManager;
    private RelativeLayout rl_person_data_headImage,rl_person_data_nickName,rl_person_data_sex,rl_person_data_desc;
    private CircleImageView person_data_headImage;
    private TextView tv_person_data_nickName,tv_person_data_sex,tv_person_data__desc;
    private static final int RETURN_CAMERA_CODE = 123;
    private static final int RETURN_PHOTOS_CODE = 124;
    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private static final int CROP_CAMERA_CODE = 441;
    private static final int CROP_PHOTO_CODE = 442;
    private Uri imageUri;
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private List<String> pathsReturn = new ArrayList<String>();
    private ArrayList<String> imagePaths = null;
    private String userId;
    private UserValueData userValueData;
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
        setTitle(this.getResources().getString(R.string.personal_data));
        setBaseContentView(R.layout.activity_personal_data);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        userId = getUserId();
        initView();
        initData();
    }
    private void initView() {

        rl_person_data_headImage = (RelativeLayout) findViewById(R.id.rl_person_data_headImage);
        rl_person_data_nickName = (RelativeLayout) findViewById(R.id.rl_person_data_nickName);
        rl_person_data_sex = (RelativeLayout) findViewById(R.id.rl_person_data_sex);
        rl_person_data_desc = (RelativeLayout) findViewById(R.id.rl_person_data_desc);
        person_data_headImage = (CircleImageView) findViewById(R.id.civ_person_data_headImage);
        tv_person_data_nickName = (TextView) findViewById(R.id.tv_person_data_nickName);
        tv_person_data_sex = (TextView) findViewById(R.id.tv_person_data_sex);
        tv_person_data__desc = (TextView) findViewById(R.id.tv_person_data__desc);

        rl_person_data_headImage.setOnClickListener(this);
        rl_person_data_nickName.setOnClickListener(this);
        rl_person_data_sex.setOnClickListener(this);
        rl_person_data_desc.setOnClickListener(this);
    }

    private void initData() {
        HttpRequest.getMembers(userId, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(PersonalDataActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success){
                        JSONObject obj =jsonObject.getJSONObject("data");
                        UserValueData userValueDataItem = JsonUtils.fromJson(obj.toString(), UserValueData.class);
                        PicassoUtils.getinstance().LoadImage(PersonalDataActivity.this,userValueDataItem.getPortrait(),person_data_headImage,R.mipmap.comment_grey,R.mipmap.comment_grey,PicassoUtils.PICASSO_BITMAP_SHOW_ROUND_TYPE,0);
                        if(AppStringUtils.isNotEmpty(userValueDataItem.getNickname())){
                            tv_person_data_nickName.setText(userValueDataItem.getNickname());
                        }else{
                            tv_person_data_nickName.setText("暂无昵称");
                        }
                        if(AppStringUtils.isNotEmpty(userValueDataItem.getDescribe())) {
                            if(userValueDataItem.getDescribe().length() >= 14){
                                tv_person_data__desc.setText(userValueDataItem.getDescribe().substring(0,13) + "...");
                            }else{
                                tv_person_data__desc.setText(userValueDataItem.getDescribe());
                            }
                        }else{
                            tv_person_data__desc.setText("暂无个性签名");
                        }
                        tv_person_data_sex.setText(userValueDataItem.getGender());
                        userValueData = userValueDataItem;
                    }else{
                        AppToastMgr.shortToast(PersonalDataActivity.this, " 接口调用失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_person_data_headImage:
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
                                    if (ContextCompat.checkSelfPermission(PersonalDataActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(PersonalDataActivity.this, new String[]{Manifest.permission.CAMERA}, RETURN_CAMERA_CODE);
                                    } else {
                                        takePhotos();
                                    }
                                } else {
                                    takePhotos();
                                }
                                break;
                            case 1:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //判断该应用是否有写SD卡权限，如果没有再去申请
                                    if (ContextCompat.checkSelfPermission(PersonalDataActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(PersonalDataActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RETURN_PHOTOS_CODE);
                                    } else {
                                        choosePhotos();
                                    }
                                } else {
                                    choosePhotos();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }, names);
                break;
            case R.id.rl_person_data_nickName:
                String nickNameStr = "";
                if(userValueData != null) {
                    if (AppStringUtils.isNotEmpty(userValueData.getNickname())) {
                        nickNameStr = userValueData.getNickname();
                    }
                }
                Intent intentNickName = new Intent(PersonalDataActivity.this,EditNickNameActivity.class);
                intentNickName.putExtra("nickName",nickNameStr);
                startActivity(intentNickName);
                break;
            case R.id.rl_person_data_sex:
                final PersonData personData = new PersonData();
                personData.setId(Integer.valueOf(userId));
                final List<String> namesSex = new ArrayList<>();
                namesSex.add("男");
                namesSex.add("女");
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0: // 直接调起相机
                                tv_person_data_sex.setText("男");
                                 personData.setGender("1");
                                changePersonData(personData);
                                break;
                            case 1:
                                tv_person_data_sex.setText("女");
                                personData.setGender("0");
                                changePersonData(personData);
                                break;
                            default:
                                break;
                        }
                    }
                }, namesSex);
                break;
            case R.id.rl_person_data_desc:
                String personDesc = "";
                if(userValueData != null) {
                    if (AppStringUtils.isNotEmpty(userValueData.getDescribe())) {
                        personDesc = userValueData.getDescribe();
                    }
                }
                Intent intentDesc = new Intent(PersonalDataActivity.this,EditPersonDataActivity.class);
                intentDesc.putExtra("personDesc",personDesc);
                startActivity(intentDesc);
                break;
        }
    }

    private void changePersonData(PersonData personData) {
        HttpRequest.changePersonData(personData, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                AppToastMgr.shortToast(PersonalDataActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                try {
                    if (null != response.cacheResponse()) {
                        String str = response.cacheResponse().toString();
                        Log.i("wangshu1", "cache---" + str);
                    } else {
                        try {
                            String str1 = response.body().string();
                            Log.i("地方撒阿达啥打法是否", "1111111111111111111111111---" + str1);
                            JSONObject jsonObject = new JSONObject(str1);
                            Boolean success = jsonObject.getBoolean("success");
                            if(success){
                                //AppToastMgr.shortToast(PersonalDataActivity.this,"修改成功！");
                            }else{
                                String errorMsg = jsonObject.getString("errorMsg");
                                //AppToastMgr.shortToast(PersonalDataActivity.this,"修改失败！原因：" + errorMsg);
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

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    private void takePhotos() {
        try {
            if (captureManager == null) {
                captureManager = new ImageCaptureManager(PersonalDataActivity.this);
            }
            Intent intentCapture = captureManager.dispatchTakePictureIntent();
            imageUri = FileProvider.getUriForFile(this, "com.cos.huanhuan.photos.fileprovider", createImageFile());
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intentCapture, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentCapture, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            Toast.makeText(PersonalDataActivity.this, com.foamtrace.photopicker.R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void choosePhotos(){
        PhotoPickerIntent intent = new PhotoPickerIntent(PersonalDataActivity.this);
        intent.setSelectModel(SelectModel.SINGLE);
        intent.setShowCarema(false); // 是否显示拍照， 默认false
        intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        //intent.setImageConfig(config);
        PersonalDataActivity.this.startActivityForResult(intent, REQUEST_CAMERA_CODE);
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
        //CROP_CAMERA_CODE CROP_PHOTO_CODE DOUBLE_CROP_CAMERA_CODE DOUBLE_CROP_PHOTO_CODE
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    pathsReturn = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    startCrop(FileProvider.getUriForFile(this, "com.cos.huanhuan.photos.fileprovider", new File(pathsReturn.get(0))),CROP_PHOTO_CODE);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    pathsReturn = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    if (pathsReturn != null && pathsReturn.size() > 0) {
                        Picasso.with(PersonalDataActivity.this).load(new File(pathsReturn.get(0))).placeholder(R.mipmap.default_error).into(person_data_headImage);
                    }
                    break;
                // 调用相机拍照
                case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                    if (captureManager.getCurrentPhotoPath() != null) {
                        captureManager.galleryAddPic();
                        pathsReturn = new ArrayList<>();
                        pathsReturn.add(captureManager.getCurrentPhotoPath());
                        if (pathsReturn != null && pathsReturn.size() > 0) {
                            startCrop(imageUri,CROP_CAMERA_CODE);
                        }
                    }
                    break;
                // 选择照片
                case CROP_PHOTO_CODE:
                    imagePaths = new ArrayList<>();
                    imagePaths.add(GetUriPath.getPath(PersonalDataActivity.this,UCrop.getOutput(data)));
                    Picasso.with(PersonalDataActivity.this).load(UCrop.getOutput(data)).placeholder(R.mipmap.default_error).into(person_data_headImage);
                    uploadHeadImg();
                    break;
                case CROP_CAMERA_CODE:
                    imagePaths = new ArrayList<>();
                    imagePaths.add(GetUriPath.getPath(PersonalDataActivity.this,UCrop.getOutput(data)));
                    Picasso.with(PersonalDataActivity.this).load(UCrop.getOutput(data)).placeholder(R.mipmap.default_error).into(person_data_headImage);
                    uploadHeadImg();
                    break;

            }
        }
    }
    private void startCrop(Uri sourceUri, int requestCode) {
        //Uri sourceUri = Uri.parse(GetUriPath.getPath(PublishExchangeActivity.this, uri));
        //裁剪后保存到文件中
        try {
            Uri destinationUri = Uri.fromFile(createImageFile());
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(getResources().getColor(R.color.titleBarTextColor));
            options.setStatusBarColor(getResources().getColor(R.color.titleBarTextColor));
            options.setFreeStyleCropEnabled(false);
            options.setHideBottomControls(true);
            options.setCircleDimmedLayer(true);
            UCrop.of(sourceUri, destinationUri)
                    .withOptions(options)
                    .withAspectRatio(16, 16)
                    .withMaxResultSize(300, 300)
                    .start(this,requestCode);
//            config.isOval = true;
//            config.aspectRatioX = 1;
//            config.aspectRatioY = 1;
//            config.hideBottomControls = true;
//            config.showGridLine = false;
//            config.showOutLine = false;
//            config.maxHeight = 400;
//            config.maxWidth = 400;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void uploadHeadImg(){
        List<File> listFile = new ArrayList<File>();
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < imagePaths.size(); i++) {
            File file = new File(imagePaths.get(i));
            listFile.add(file);
            params.put("file" + i, imagePaths.get(i));
        }
        //params.put("id",userId);
        MultipartRequest multipartRequest = new MultipartRequest(HttpRequest.TEXT_HUANHUAN_HOST + "Members/Portrait/" + userId, mErrorListener, mResonseListenerString, "portrait", listFile, params);
        multipartRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue mSingleQueue = Volley.newRequestQueue(this, new MultiPartStack());
        mSingleQueue.add(multipartRequest);
    }

    //单张图片上传图片成功监听器
    Response.Listener<String> mResonseListenerString = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {
            Log.i("2", response.toString());
            try {
                JSONObject jsonObject = new JSONObject(response);
                Boolean success = jsonObject.getBoolean("success");
                String errorMsg = jsonObject.getString("errorMsg");
                if(success){
                    Picasso.with(PersonalDataActivity.this).load(new File(imagePaths.get(0))).placeholder(R.mipmap.comment_grey).into(person_data_headImage);
                }else{
                    AppToastMgr.shortToast(PersonalDataActivity.this, " 头像上传失败！原因：" + errorMsg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    //单张上传图片失败监听器
    Response.ErrorListener mErrorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            AppToastMgr.shortToast(PersonalDataActivity.this, " 头像上传接口调用失败！");
            if (error != null) {
                if (error.networkResponse != null)
                    Log.i("3", " error "
                            + new String(error.networkResponse.data));
            }
        }
    };
}
