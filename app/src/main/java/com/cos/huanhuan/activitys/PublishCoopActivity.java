package com.cos.huanhuan.activitys;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.ImageGridAdapter;
import com.cos.huanhuan.model.Classify;
import com.cos.huanhuan.model.JsonBean;
import com.cos.huanhuan.model.MultiPartStack;
import com.cos.huanhuan.model.MultipartRequest;
import com.cos.huanhuan.model.PublishCoop;
import com.cos.huanhuan.model.PublishExchanges;
import com.cos.huanhuan.utils.AppACache;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.DateUtil;
import com.cos.huanhuan.utils.GetJsonDataUtil;
import com.cos.huanhuan.utils.GetUriPath;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.SelectDialog;
import com.cos.huanhuan.utils.SoftHideKeyBoardUtil;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.MyGridView;
import com.cos.huanhuan.views.TitleBar;
import com.foamtrace.photopicker.ImageCaptureManager;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
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

public class PublishCoopActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private EditText et_coop_cosTitle,et_publishCoop_cosAddressDetail,et_publish_cosRequest,et_publishCoop_cosPersonNums,et_publishCoop_cosDetailDesc;
    private LinearLayout ll_publishCoop_Address,ll_publishCoop_time,ll_publishCoop_classify;
    private TextView tv_publishCoop_cosCoopAddress,tv_publishCoop_cosTime,tv_publish_coop_cosClassify;
    private ImageView coop_select_img_page;
    private MyGridView gridView_coop;

    private AppManager appManager;

    //单张图片返回码
    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private static final int RETURN_CAMERA_CODE = 123;
    private static final int RETURN_PHOTOS_CODE = 124;

    //新增的多张图片选择返回码
    private static final int DOUBLE_REQUEST_CAMERA_CODE = 31;
    private static final int DOUBLE_REQUEST_PREVIEW_CODE = 32;
    private static final int DOUBLE_RETURN_CAMERA_CODE = 325;
    private static final int DOUBLE_RETURN_PHOTOS_CODE = 326;
    private static final int DOUBLE_REQUEST_TAKE_PHOTO = 327;

    private static final int CROP_CAMERA_CODE = 441;
    private static final int CROP_PHOTO_CODE = 442;
    private static final int DOUBLE_CROP_CAMERA_CODE = 443;

    private ArrayList<String> imagePaths = null;
    private ArrayList<String> double_imagePaths = null;

    private ImageGridAdapter imageGridAdapter;
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private List<String> pathsReturn = new ArrayList<String>();

    private Uri imageUri;

    private List<Classify> listClassify;
    private List<String> listClassifyString;

    private int coverId = -1;
    private String mutiImgId = "";

    private String userId = "";

    private Dialog dialog;

    private String provice,city,district;
    private TimePickerView timePickerView;
    private OptionsPickerView addressOptionsPick;
    private Date selectDate = new Date();
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private int selectOptions1 = 0, selectOptions2 = 0, selectOptions3 = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        setTitleBarColor(R.color.white);
        setLeftText(getString(R.string.cancel));
        setLeftTextColor(R.color.titleBarTextColor);
        setDividerColor(R.color.dividLineColor);
        setRightTextColor(R.color.titleBarTextColor);
        setTitleTextColor(R.color.titleBarTextColor);
        setTitle(this.getResources().getString(R.string.publishCoop));
        setBaseContentView(R.layout.activity_publish_coop);
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        SoftHideKeyBoardUtil.assistActivity(this);
        initView();

        leftButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
        setRightButton(new TitleBar.TextAction(this.getResources().getString(R.string.confirmPublish)) {
            @Override
            public void performAction(View view) {
                upLoadCoverImag();
            }
        });
    }

    private void initView() {
        et_coop_cosTitle = (EditText) findViewById(R.id.et_coop_cosTitle);
        et_publishCoop_cosAddressDetail = (EditText) findViewById(R.id.et_publishCoop_cosAddressDetail);
        et_publish_cosRequest = (EditText) findViewById(R.id.et_publish_cosRequest);
        et_publishCoop_cosPersonNums= (EditText) findViewById(R.id.et_publishCoop_cosPersonNums);
        et_publishCoop_cosDetailDesc = (EditText) findViewById(R.id.et_publishCoop_cosDetailDesc);
        ll_publishCoop_Address = (LinearLayout) findViewById(R.id.ll_publishCoop_Address);
        ll_publishCoop_time = (LinearLayout) findViewById(R.id.ll_publishCoop_time);
        ll_publishCoop_classify = (LinearLayout) findViewById(R.id.ll_publishCoop_classify);
        tv_publishCoop_cosCoopAddress = (TextView) findViewById(R.id.tv_publishCoop_cosCoop);
        tv_publishCoop_cosTime = (TextView) findViewById(R.id.tv_publishCoop_cosTime);
        tv_publish_coop_cosClassify = (TextView) findViewById(R.id.tv_publish_coop_cosClassify);
        coop_select_img_page = (ImageView) findViewById(R.id.coop_select_img_page);
        gridView_coop = (MyGridView) findViewById(R.id.gridView_coop);

        ll_publishCoop_Address.setOnClickListener(this);
        ll_publishCoop_time.setOnClickListener(this);
        coop_select_img_page.setOnClickListener(this);
        gridView_coop.setOnItemClickListener(this);
        ll_publishCoop_classify.setOnClickListener(this);
        userId = getUserId();
        ArrayList<String> listItem = new ArrayList<String>();
        loadAdpater(listItem);
        initJsonData();
        initData();
    }

    private void initData() {
        listClassify = new ArrayList<Classify>();
        listClassifyString = new ArrayList<>();
        HttpRequest.getCoopClass(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                toastErrorMsg(PublishCoopActivity.this,"请求分类接口失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success) {
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
                        toastErrorMsg(PublishCoopActivity.this, " 请求分类接口失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                    0);
        }
        switch (view.getId()){
            case R.id.ll_publishCoop_Address:
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
                            tv_publishCoop_cosCoopAddress.setText(tx);
                            provice = options1Items.get(options1).getPickerViewText();
                            city = options2Items.get(options1).get(options2);
                            district = options1Items.get(options1).getPickerViewText();;
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
                    toastErrorMsg(PublishCoopActivity.this,"未获取到地址信息");
                }
                break;
            case R.id.ll_publishCoop_time:
                timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date,View v) {//选中事件回调
                        selectDate = date;
                        tv_publishCoop_cosTime.setText(DateUtil.formatDate(date,DateUtil.formatStr7));
                    }
                }).setType(new boolean[]{true, true, true, true, true, false})
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleText("选择时间")//标题文字
                .setOutSideCancelable(true)
                .setTitleColor(getResources().getColor(R.color.black))//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.titleBarTextColor))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.titleBarTextColor))//取消按钮文字颜色
                .setBgColor(getResources().getColor(R.color.white))
                .setDate(DateUtil.getCalendar(selectDate))
                .build();
                timePickerView.show();
                break;
            case R.id.coop_select_img_page:
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
                                    if (ContextCompat.checkSelfPermission(PublishCoopActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(PublishCoopActivity.this, new String[]{Manifest.permission.CAMERA}, RETURN_CAMERA_CODE);
                                    } else {
                                        takePhotos(1);
                                    }
                                } else {
                                    takePhotos(1);
                                }
                                break;
                            case 1:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //判断该应用是否有写SD卡权限，如果没有再去申请
                                    if (ContextCompat.checkSelfPermission(PublishCoopActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(PublishCoopActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RETURN_PHOTOS_CODE);
                                    } else {
                                        choosePhotos(1);
                                    }
                                } else {
                                    choosePhotos(1);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }, names);
                break;
            case R.id.ll_publishCoop_classify:
                int position = 0;
                String selectItem = tv_publish_coop_cosClassify.getText().toString();
                if(AppStringUtils.isNotEmpty(selectItem) && listClassifyString != null && listClassifyString.size() > 0){
                    position = listClassifyString.indexOf(selectItem);
                }
                //条件选择器
                OptionsPickerView pvOptions = new  OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                        //返回的分别是三个级别的选中位置
                        String className = listClassifyString.get(options1);
                        tv_publish_coop_cosClassify.setText(className);
                    }
                }).setContentTextSize(20).isDialog(true)
                        .setSelectOptions(position)
                        .setCancelColor(getResources().getColor(R.color.titleBarTextColor))
                        .setSubmitColor(getResources().getColor(R.color.titleBarTextColor))
                        .build();
                pvOptions.setPicker(listClassifyString, null, null);
                pvOptions.show();
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RETURN_CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotos(1);
            } else {
                Toast.makeText(this, "获取拍照权限失败！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (requestCode == RETURN_PHOTOS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhotos(1);
            } else {
                Toast.makeText(this, "获取存储权限失败！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (requestCode == DOUBLE_RETURN_CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotos(2);
            } else {
                Toast.makeText(this, "获取拍照权限失败！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (requestCode == DOUBLE_RETURN_PHOTOS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhotos(2);
            } else {
                Toast.makeText(this, "获取存储权限失败！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void choosePhotos(int type){
        if (type == 1) {
            PhotoPickerIntent intent = new PhotoPickerIntent(PublishCoopActivity.this);
            intent.setSelectModel(SelectModel.SINGLE);
            intent.setShowCarema(false); // 是否显示拍照， 默认false
            intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            //intent.setImageConfig(config);
            PublishCoopActivity.this.startActivityForResult(intent, REQUEST_CAMERA_CODE);
        } else {
            PhotoPickerIntent intent = new PhotoPickerIntent(PublishCoopActivity.this);
            intent.setSelectModel(SelectModel.MULTI);
            intent.setShowCarema(false); // 是否显示拍照， 默认false
            intent.setMaxTotal(9); // 最多选择照片数量，默认为9
            intent.setSelectedPaths(double_imagePaths); // 已选中的照片地址， 用于回显选中状态
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            //intent.setImageConfig(config);
            PublishCoopActivity.this.startActivityForResult(intent, DOUBLE_REQUEST_CAMERA_CODE);
        }
    }

    private void takePhotos(int type) {
        if (type == 1) {
            try {
                if (captureManager == null) {
                    captureManager = new ImageCaptureManager(PublishCoopActivity.this);
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
                Toast.makeText(PublishCoopActivity.this, com.foamtrace.photopicker.R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            try {
                if (captureManager == null) {
                    captureManager = new ImageCaptureManager(PublishCoopActivity.this);
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
                startActivityForResult(intentCapture, DOUBLE_REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                Toast.makeText(PublishCoopActivity.this, com.foamtrace.photopicker.R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
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
                        Picasso.with(PublishCoopActivity.this).load(new File(pathsReturn.get(0))).placeholder(R.mipmap.default_error).into(coop_select_img_page);
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

                case DOUBLE_REQUEST_CAMERA_CODE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                    break;
                // 预览
                case DOUBLE_REQUEST_PREVIEW_CODE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                    break;
                // 调用相机拍照
                case DOUBLE_REQUEST_TAKE_PHOTO:
                    if (captureManager.getCurrentPhotoPath() != null) {
                        startCrop(FileProvider.getUriForFile(this, "com.cos.huanhuan.photos.fileprovider", new File(captureManager.getCurrentPhotoPath())),DOUBLE_CROP_CAMERA_CODE);
                    }
                    break;
                case CROP_PHOTO_CODE:
                    imagePaths = new ArrayList<>();
                    imagePaths.add(GetUriPath.getPath(PublishCoopActivity.this, UCrop.getOutput(data)));
                    Picasso.with(PublishCoopActivity.this).load(UCrop.getOutput(data)).placeholder(R.mipmap.default_error).into(coop_select_img_page);
                    break;
                case CROP_CAMERA_CODE:
                    imagePaths = new ArrayList<>();
                    imagePaths.add(GetUriPath.getPath(PublishCoopActivity.this,UCrop.getOutput(data)));
                    Picasso.with(PublishCoopActivity.this).load(UCrop.getOutput(data)).placeholder(R.mipmap.default_error).into(coop_select_img_page);
                    break;
                case DOUBLE_CROP_CAMERA_CODE:
                    if (captureManager.getCurrentPhotoPath() != null) {
                        ArrayList<String> paths = new ArrayList<>();
                        captureManager.galleryAddPic();
                        paths.addAll(double_imagePaths);
                        paths.add(captureManager.getCurrentPhotoPath());
                        loadAdpater(paths);
                    }
                    break;
            }
        }
    }

    private void loadAdpater(ArrayList<String> paths) {
        if (double_imagePaths == null) {
            double_imagePaths = new ArrayList<>();
        }
        double_imagePaths.clear();
        double_imagePaths.addAll(paths);
        try {
            JSONArray obj = new JSONArray(double_imagePaths);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (imageGridAdapter == null) {
            imageGridAdapter = new ImageGridAdapter(PublishCoopActivity.this, double_imagePaths);
            gridView_coop.setAdapter(imageGridAdapter);
        } else {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        if (position == parent.getChildCount() - 1) {
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
                                if (ContextCompat.checkSelfPermission(PublishCoopActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(PublishCoopActivity.this, new String[]{Manifest.permission.CAMERA}, DOUBLE_RETURN_CAMERA_CODE);
                                } else {
                                    takePhotos(2);
                                }
                            } else {
                                takePhotos(2);
                            }
                            break;
                        case 1:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                //判断该应用是否有写SD卡权限，如果没有再去申请
                                if (ContextCompat.checkSelfPermission(PublishCoopActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(PublishCoopActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, DOUBLE_RETURN_PHOTOS_CODE);
                                } else {
                                    choosePhotos(2);
                                }
                            } else {
                                choosePhotos(2);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }, names);
        }
    }

    private void startCrop(Uri sourceUri, int requestCode) {
        //Uri sourceUri = Uri.parse(GetUriPath.getPath(PublishCoopActivity.this, uri));
        //裁剪后保存到文件中
        try {
            Uri destinationUri = Uri.fromFile(createImageFile());
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(getResources().getColor(R.color.titleBarTextColor));
            options.setStatusBarColor(getResources().getColor(R.color.titleBarTextColor));
            options.setFreeStyleCropEnabled(false);
            options.setHideBottomControls(true);
            UCrop.of(sourceUri, destinationUri)
                    .withOptions(options)
                    .withAspectRatio(16, 16)
                    .withMaxResultSize(300, 300)
                    .start(this,requestCode);
        } catch (IOException e) {
            e.printStackTrace();
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

    private void upLoadCoverImag() {

        String cosTitle = et_coop_cosTitle.getText().toString();
        String cosAddress = tv_publishCoop_cosCoopAddress.getText().toString();
        String cosAddressDetail = et_publishCoop_cosAddressDetail.getText().toString();
        String cosRequest = et_publish_cosRequest.getText().toString();
        String cosTime = tv_publishCoop_cosTime.getText().toString();
        String cosPersonNums = et_publishCoop_cosPersonNums.getText().toString();
        String cosDetailDesc = et_publishCoop_cosDetailDesc.getText().toString();
        String cosClassify = tv_publish_coop_cosClassify.getText().toString();

        if(AppStringUtils.isEmpty(cosTitle)){
            toastErrorMsg(PublishCoopActivity.this, "请输入合作主题名称");
            return;
        }
        if(AppStringUtils.isEmpty(cosClassify)){
            toastErrorMsg(PublishCoopActivity.this, "请选择物品类别");
            return;
        }
        if(AppStringUtils.isEmpty(cosAddress)){
            toastErrorMsg(PublishCoopActivity.this, "请选择地址");
            return;
        }
        if(AppStringUtils.isEmpty(cosAddressDetail)){
            toastErrorMsg(PublishCoopActivity.this, "请输入详细地址");
            return;
        }
        if(AppStringUtils.isEmpty(cosRequest)){
            toastErrorMsg(PublishCoopActivity.this, "请输入需求");
            return;
        }
        if(AppStringUtils.isEmpty(cosTime)){
            toastErrorMsg(PublishCoopActivity.this, "请选择活动时间");
            return;
        }
        if(AppStringUtils.isEmpty(cosPersonNums)){
            toastErrorMsg(PublishCoopActivity.this, "请设置人数");
            return;
        }
        if(AppStringUtils.isEmpty(cosDetailDesc)){
            toastErrorMsg(PublishCoopActivity.this, "请填写描述信息");
            return;
        }
        int cosPersons = Integer.valueOf(cosPersonNums);
        if(cosPersons <= 0){
            toastErrorMsg(PublishCoopActivity.this, "人数不能小于0");
            return;
        }
        if(imagePaths != null && imagePaths.size() > 0) {
            if(double_imagePaths != null && double_imagePaths.size() > 0) {
                if(double_imagePaths.size() >= 3) {
                    List<File> listFile = new ArrayList<File>();
                    Map<String, String> params = new HashMap<String, String>();
                    for (int i = 0; i < imagePaths.size(); i++) {
                        File file = new File(imagePaths.get(i));
                        listFile.add(file);
                        params.put("file" + i, imagePaths.get(i));
                    }
                    dialog = ViewUtils.createLoadingDialog(this);
                    dialog.show();
                    MultipartRequest multipartRequest = new MultipartRequest(HttpRequest.TEXT_HUANHUAN_HOST + "Imgs", mErrorListener, mResonseListenerString, "files", listFile, params);
                    multipartRequest.setRetryPolicy(
                            new DefaultRetryPolicy(
                                    3000000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                            )
                    );
                    RequestQueue mSingleQueue = Volley.newRequestQueue(this, new MultiPartStack());
                    mSingleQueue.add(multipartRequest);
                }else{
                    toastErrorMsg(PublishCoopActivity.this, "请至少选择三张图片");
                }
            }else{
                toastErrorMsg(PublishCoopActivity.this, "请选择图片");
            }
        }else{
            toastErrorMsg(PublishCoopActivity.this, "请选择封面图片");
        }
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
                    coverId = jsonObject.getInt("data");
                    Log.i("单张图片",String.valueOf(coverId));
                    upLoadMutiImag();
                }else{
                    toastErrorMsg(PublishCoopActivity.this, " 封面图片上传失败！原因：" + errorMsg);
                    dialog.dismiss();
                }
            } catch (JSONException e) {
                dialog.dismiss();
                e.printStackTrace();
            }
        }
    };
    //单张上传图片失败监听器
    Response.ErrorListener mErrorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            toastErrorMsg(PublishCoopActivity.this, " 封面图片上传接口调用失败！");
            dialog.dismiss();
            if (error != null) {
                if (error.networkResponse != null)
                    Log.i("3", " error "
                            + new String(error.networkResponse.data));
            }
        }
    };

    private void upLoadMutiImag() {
        List<File> listFile = new ArrayList<File>();
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < double_imagePaths.size(); i++) {
            File file = new File(double_imagePaths.get(i));
            listFile.add(file);
            params.put("file" + i, double_imagePaths.get(i));
        }
        MultipartRequest multipartRequest = new MultipartRequest(HttpRequest.TEXT_HUANHUAN_HOST + "Imgs", mErrorMutiListener, mResonseMutiListenerString, "files", listFile, params);
        multipartRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        3000000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue mSingleQueue = Volley.newRequestQueue(this, new MultiPartStack());
        mSingleQueue.add(multipartRequest);
    }

    //单张图片上传图片成功监听器
    Response.Listener<String> mResonseMutiListenerString = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {
            Log.i("2", response.toString());
            try {
                JSONObject jsonObject = new JSONObject(response);
                Boolean success = jsonObject.getBoolean("success");
                String errorMsg = jsonObject.getString("errorMsg");
                if(success){
                    mutiImgId = jsonObject.getString("data");
                    String cosTitle = et_coop_cosTitle.getText().toString();
                    String cosAddressDetail = et_publishCoop_cosAddressDetail.getText().toString();
                    String cosRequest = et_publish_cosRequest.getText().toString();
                    String cosPersonNums = et_publishCoop_cosPersonNums.getText().toString();
                    String cosDetailDesc = et_publishCoop_cosDetailDesc.getText().toString();
                    String selectClassifyId = "";
                    int position = listClassifyString.indexOf(tv_publish_coop_cosClassify.getText().toString());
                    selectClassifyId = listClassify.get(position).getClassifyId();
                    PublishCoop publishCoop = new PublishCoop();
                    publishCoop.setTitle(cosTitle);
                    publishCoop.setProv(provice);
                    publishCoop.setCity(city);
                    publishCoop.setDist(district);
                    publishCoop.setClassId(Integer.valueOf(selectClassifyId));
                    publishCoop.setAddress(cosAddressDetail);
                    publishCoop.setWill(cosRequest);
                    publishCoop.setEnrollEnd(selectDate);
                    publishCoop.setLimitPerson(Integer.valueOf(cosPersonNums));
                    publishCoop.setDescribe(cosDetailDesc);
                    publishCoop.setCover(coverId);
                    publishCoop.setImgList(mutiImgId);
                    publishCoop.setUserId(Integer.valueOf(userId));
                    PublishAllMessage(publishCoop);
                }else{
                    dialog.dismiss();
                    toastErrorMsg(PublishCoopActivity.this, " 多张图片上传失败！原因：" + errorMsg);
                }
            } catch (JSONException e) {
                dialog.dismiss();
                e.printStackTrace();
            }
        }
    };

    //单张上传图片失败监听器
    Response.ErrorListener mErrorMutiListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            toastErrorMsg(PublishCoopActivity.this, " 多张图片上传接口调用失败！");
            dialog.dismiss();
            if (error != null) {
                if (error.networkResponse != null)
                    Log.i("3", " error "
                            + new String(error.networkResponse.data));
            }
        }
    };

    /**
     * 上传发布合作信息
     * @param publishCoop
     */
    private void PublishAllMessage(PublishCoop publishCoop) {
        HttpRequest.publishExchanges(publishCoop, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                dialog.dismiss();
                toastErrorMsg(PublishCoopActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success) {
                        dialog.dismiss();
                        toastErrorMsg(PublishCoopActivity.this, " 发布成功");
                        appManager.finishActivity();
                    }else{
                        dialog.dismiss();
                        toastErrorMsg(PublishCoopActivity.this, " 发布失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        });
    }
}
