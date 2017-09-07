package com.cos.huanhuan.activitys;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bigkoo.pickerview.OptionsPickerView;
import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.ImageGridAdapter;
import com.cos.huanhuan.model.Classify;
import com.cos.huanhuan.model.MultiPartStack;
import com.cos.huanhuan.model.MultipartRequest;
import com.cos.huanhuan.model.PublishExchanges;
import com.cos.huanhuan.utils.AppACache;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.FileUtils;
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

public class PublishExchangeActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener {

    private AppManager appManager;
    private OptionsPickerView pvNoLinkOptions;

    private EditText et_publish_cosTitle, et_publish_cosAuthor, et_publish_cosRole, et_publish_cosContain, et_publish_cosFrom, et_publish_cosPrice, et_publish_cosDetailDesc;
    private LinearLayout ll_publishExchange_classify;
    private MyGridView gridView_publish;
    private ImageView select_img_page;
    private TextView tv_publish_cosClassify;

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
    private static final int DOUBLE_CROP_PHOTO_CODE = 444;
    private static final int DOUBLE_CROP_PHOTO_CODE_END = 445;

    private ArrayList<String> imagePaths = null;
    private ArrayList<String> double_imagePaths = null;

    private ImageGridAdapter imageGridAdapter;
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private List<String> pathsReturn = new ArrayList<String>();

    private Uri imageUri;

    private List<Classify> listClassify;
    private List<String> listClassifyString;

    private PopViewListAdapter adapter;

    //用于上传的封面图片地址
    private ArrayList<String> imagePathsSelectdPage = null;

    //用于上传的图片列表地址
    private ArrayList<String> imagePathsSelectdList = null;

    private int coverId = -1;
    private String mutiImgId = "";

    private String userId = "";

    private Dialog dialog;

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
        setTitle(this.getResources().getString(R.string.publishExchange));
        setBaseContentView(R.layout.activity_publish_exchange);
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
        et_publish_cosTitle = (EditText) findViewById(R.id.et_publish_cosTitle);
        et_publish_cosAuthor = (EditText) findViewById(R.id.et_publish_cosAuthor);
        et_publish_cosRole = (EditText) findViewById(R.id.et_publish_cosRole);
        et_publish_cosContain = (EditText) findViewById(R.id.et_publish_cosContain);
        et_publish_cosFrom = (EditText) findViewById(R.id.et_publish_cosFrom);
        et_publish_cosPrice = (EditText) findViewById(R.id.et_publish_cosPrice);
        et_publish_cosDetailDesc = (EditText) findViewById(R.id.et_publish_cosDetailDesc);

        ll_publishExchange_classify = (LinearLayout) findViewById(R.id.ll_publishExchange_classify);

        gridView_publish = (MyGridView) findViewById(R.id.gridView_publish);

        select_img_page = (ImageView) findViewById(R.id.select_img_page);

        tv_publish_cosClassify = (TextView) findViewById(R.id.tv_publish_cosClassify);

        select_img_page.setOnClickListener(this);
        gridView_publish.setOnItemClickListener(this);
        ll_publishExchange_classify.setOnClickListener(this);

        listClassify = new ArrayList<Classify>();
        listClassifyString = new ArrayList<>();

        HttpRequest.getExchangeClass(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(PublishExchangeActivity.this,"请求分类接口失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success) {
                        JSONArray arr = jsonObject.getJSONArray("list");
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
                        AppToastMgr.shortToast(PublishExchangeActivity.this, " 请求分类接口失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        userId = getUserId();
        ArrayList<String> listItem = new ArrayList<String>();
        loadAdpater(listItem);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_img_page:
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
                                    if (ContextCompat.checkSelfPermission(PublishExchangeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(PublishExchangeActivity.this, new String[]{Manifest.permission.CAMERA}, RETURN_CAMERA_CODE);
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
                                    if (ContextCompat.checkSelfPermission(PublishExchangeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(PublishExchangeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RETURN_PHOTOS_CODE);
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
            case R.id.ll_publishExchange_classify:
//                final View popupView = LayoutInflater.from(this).inflate(R.layout.popwindow_noimg, null);
//                ListView listView = (ListView)popupView.findViewById(R.id.popListView);
//                listClassify = new ArrayList<String>();
//                listClassify.add("11111");
//                adapter = new PopViewListAdapter(PublishExchangeActivity.this,listClassify);
//                listView.setAdapter(adapter);
//                ViewUtils.showPopupWindow(PublishExchangeActivity.this,ll_publishExchange_classify,6,popupView);
                int position = 0;
                String selectItem = tv_publish_cosClassify.getText().toString();
                if(AppStringUtils.isNotEmpty(selectItem) && listClassifyString != null && listClassifyString.size() > 0){
                    position = listClassifyString.indexOf(selectItem);
                }
                //条件选择器
                OptionsPickerView pvOptions = new  OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                        //返回的分别是三个级别的选中位置
                        String className = listClassifyString.get(options1);
                        tv_publish_cosClassify.setText(className);
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
            PhotoPickerIntent intent = new PhotoPickerIntent(PublishExchangeActivity.this);
            intent.setSelectModel(SelectModel.SINGLE);
            intent.setShowCarema(false); // 是否显示拍照， 默认false
            intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            //intent.setImageConfig(config);
            PublishExchangeActivity.this.startActivityForResult(intent, REQUEST_CAMERA_CODE);
        } else {
            PhotoPickerIntent intent = new PhotoPickerIntent(PublishExchangeActivity.this);
            intent.setSelectModel(SelectModel.MULTI);
            intent.setShowCarema(false); // 是否显示拍照， 默认false
            intent.setMaxTotal(9); // 最多选择照片数量，默认为9
            intent.setSelectedPaths(double_imagePaths); // 已选中的照片地址， 用于回显选中状态
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            //intent.setImageConfig(config);
            PublishExchangeActivity.this.startActivityForResult(intent, DOUBLE_REQUEST_CAMERA_CODE);
        }
    }

    private void takePhotos(int type) {
        if (type == 1) {
            try {
                if (captureManager == null) {
                    captureManager = new ImageCaptureManager(PublishExchangeActivity.this);
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
                Toast.makeText(PublishExchangeActivity.this, com.foamtrace.photopicker.R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            try {
                if (captureManager == null) {
                    captureManager = new ImageCaptureManager(PublishExchangeActivity.this);
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
                Toast.makeText(PublishExchangeActivity.this, com.foamtrace.photopicker.R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
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
                        Picasso.with(PublishExchangeActivity.this).load(new File(pathsReturn.get(0))).placeholder(R.mipmap.default_error).into(select_img_page);
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
                    imagePaths.add(GetUriPath.getPath(PublishExchangeActivity.this,UCrop.getOutput(data)));
                    Picasso.with(PublishExchangeActivity.this).load(UCrop.getOutput(data)).placeholder(R.mipmap.default_error).into(select_img_page);
                    break;
                case CROP_CAMERA_CODE:
                    imagePaths = new ArrayList<>();
                    imagePaths.add(GetUriPath.getPath(PublishExchangeActivity.this,UCrop.getOutput(data)));
                    Picasso.with(PublishExchangeActivity.this).load(UCrop.getOutput(data)).placeholder(R.mipmap.default_error).into(select_img_page);
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
            imageGridAdapter = new ImageGridAdapter(PublishExchangeActivity.this, double_imagePaths);
            gridView_publish.setAdapter(imageGridAdapter);
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
                                if (ContextCompat.checkSelfPermission(PublishExchangeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(PublishExchangeActivity.this, new String[]{Manifest.permission.CAMERA}, DOUBLE_RETURN_CAMERA_CODE);
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
                                if (ContextCompat.checkSelfPermission(PublishExchangeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(PublishExchangeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, DOUBLE_RETURN_PHOTOS_CODE);
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
        //Uri sourceUri = Uri.parse(GetUriPath.getPath(PublishExchangeActivity.this, uri));
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

    private void upLoadCoverImag() {
        String cosTitle = et_publish_cosTitle.getText().toString();
        String cosAuthor = et_publish_cosAuthor.getText().toString();
        String cosRole = et_publish_cosRole.getText().toString();
        String cosContain = et_publish_cosContain.getText().toString();
        String cosFrom = et_publish_cosFrom.getText().toString();
        String cosPriceText = et_publish_cosPrice.getText().toString();
        Double cosPrice = 0.0;
        String cosDetailDesc = et_publish_cosDetailDesc.getText().toString();
        String selectCosClassText = tv_publish_cosClassify.getText().toString();
        String selectClassifyId = "";
        if(AppStringUtils.isNotEmpty(selectCosClassText)){
            int position = listClassifyString.indexOf(tv_publish_cosClassify.getText().toString());
            selectClassifyId = listClassify.get(position).getClassifyId();
        }
        if(AppStringUtils.isEmpty(cosTitle)){
            AppToastMgr.shortToast(PublishExchangeActivity.this, "请输入标题");
            return;
        }
        if(AppStringUtils.isEmpty(cosAuthor)){
            AppToastMgr.shortToast(PublishExchangeActivity.this, "请输入物品原作");
            return;
        }
        if(AppStringUtils.isEmpty(cosRole)){
            AppToastMgr.shortToast(PublishExchangeActivity.this, "请输入物品角色");
            return;
        }
        if(AppStringUtils.isEmpty(cosContain)){
            AppToastMgr.shortToast(PublishExchangeActivity.this, "请输入物品包含");
            return;
        }
        if(AppStringUtils.isEmpty(cosFrom)){
            AppToastMgr.shortToast(PublishExchangeActivity.this, "请输入物品来源");
            return;
        }
        if(AppStringUtils.isEmpty(cosPriceText)){
            AppToastMgr.shortToast(PublishExchangeActivity.this, "请输入物品价格");
            return;
        }
        if(AppStringUtils.isEmpty(selectCosClassText)){
            AppToastMgr.shortToast(PublishExchangeActivity.this, "请选择物品类别");
            return;
        }
        if(AppStringUtils.isEmpty(cosDetailDesc)){
            AppToastMgr.shortToast(PublishExchangeActivity.this, "请填写描述信息");
            return;
        }
        cosPrice = Double.parseDouble(cosPriceText);
        if(cosPrice <= 0){
            AppToastMgr.shortToast(PublishExchangeActivity.this, "物品价格不能小于0");
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
                                    500000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                            )
                    );
                    RequestQueue mSingleQueue = Volley.newRequestQueue(this, new MultiPartStack());
                    mSingleQueue.add(multipartRequest);
                }else{
                    AppToastMgr.shortToast(PublishExchangeActivity.this, "请至少选择三张图片");
                }
            }else{
                AppToastMgr.shortToast(PublishExchangeActivity.this, "请选择图片");
            }
        }else{
            AppToastMgr.shortToast(PublishExchangeActivity.this, "请选择封面图片");
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
                    AppToastMgr.shortToast(PublishExchangeActivity.this, " 封面图片上传失败！原因：" + errorMsg);
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
            AppToastMgr.shortToast(PublishExchangeActivity.this, " 封面图片上传接口调用失败！");
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
                        500000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
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
                    String cosTitle = et_publish_cosTitle.getText().toString();
                    String cosAuthor = et_publish_cosAuthor.getText().toString();
                    String cosRole = et_publish_cosRole.getText().toString();
                    String cosContain = et_publish_cosContain.getText().toString();
                    String cosFrom = et_publish_cosFrom.getText().toString();
                    String cosPriceText = et_publish_cosPrice.getText().toString();
                    String cosDetailDesc = et_publish_cosDetailDesc.getText().toString();
                    String selectClassifyId = "";
                    int position = listClassifyString.indexOf(tv_publish_cosClassify.getText().toString());
                    selectClassifyId = listClassify.get(position).getClassifyId();
                    PublishExchanges publishExchanges = new PublishExchanges();
                    publishExchanges.setTitle(cosTitle);
                    publishExchanges.setItemName(cosAuthor);
                    publishExchanges.setItemCharacter(cosRole);
                    publishExchanges.setConstitute(cosContain);
                    publishExchanges.setSource(cosFrom);
                    publishExchanges.setPrice(Double.parseDouble(cosPriceText));
                    publishExchanges.setClassId(Integer.valueOf(selectClassifyId));
                    publishExchanges.setDescribe(cosDetailDesc);
                    publishExchanges.setCover(coverId);
                    publishExchanges.setImgList(mutiImgId);
                    publishExchanges.setUserId(Integer.valueOf(userId));
                    PublishAllMessage(publishExchanges);
                }else{
                    dialog.dismiss();
                    AppToastMgr.shortToast(PublishExchangeActivity.this, " 多张图片上传失败！原因：" + errorMsg);
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
            AppToastMgr.shortToast(PublishExchangeActivity.this, " 多张图片上传接口调用失败！");
            dialog.dismiss();
            if (error != null) {
                if (error.networkResponse != null)
                    Log.i("3", " error "
                            + new String(error.networkResponse.data));
            }
        }
    };

    /**
     * 上传发布兑换信息
     * @param publishExchanges
     */
    private void PublishAllMessage(PublishExchanges publishExchanges) {
        HttpRequest.publishExchanges(publishExchanges, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                dialog.dismiss();
                AppToastMgr.shortToast(PublishExchangeActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success) {
                        dialog.dismiss();
                        AppToastMgr.shortToast(PublishExchangeActivity.this, " 发布成功");
                        appManager.finishActivity();
                    }else{
                        dialog.dismiss();
                        AppToastMgr.shortToast(PublishExchangeActivity.this, " 发布失败！原因：" + errorMsg);
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        });
    }
}
