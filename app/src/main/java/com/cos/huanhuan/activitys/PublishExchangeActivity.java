package com.cos.huanhuan.activitys;

import android.Manifest;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.ImageGridAdapter;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.GetUriPath;
import com.cos.huanhuan.utils.SelectDialog;
import com.cos.huanhuan.utils.SoftHideKeyBoardUtil;
import com.cos.huanhuan.views.MyGridView;
import com.cos.huanhuan.views.TitleBar;
import com.foamtrace.photopicker.ImageCaptureManager;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PublishExchangeActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener {

    private AppManager appManager;
    private OptionsPickerView pvNoLinkOptions;

    private EditText et_publish_cosTitle, et_publish_cosAuthor, et_publish_cosRole, et_publish_cosContain, et_publish_cosFrom, et_publish_cosPrice, et_publish_cosDetailDesc;
    private LinearLayout ll_publishExchange_classify;
    private MyGridView gridView_publish;
    private ImageView select_img_page;

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

    private ArrayList<String> imagePaths = null;
    private ArrayList<String> double_imagePaths = null;

    private ImageGridAdapter imageGridAdapter;
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private List<String> pathsReturn = new ArrayList<String>();

    private Uri imageUri;

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

        select_img_page.setOnClickListener(this);
        gridView_publish.setOnItemClickListener(this);
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
                imageUri = FileProvider.getUriForFile(this, "com.cos.huanhuan.fileprovider", createImageFile());
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
                imageUri = FileProvider.getUriForFile(this, "com.cos.huanhuan.fileprovider", createImageFile());
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
                    startCrop(FileProvider.getUriForFile(this, "com.cos.huanhuan.fileprovider", new File(pathsReturn.get(0))),CROP_PHOTO_CODE);
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
                            //Picasso.with(PublishExchangeActivity.this).load(new File(pathsReturn.get(0))).placeholder(R.mipmap.default_error).into(select_img_page);
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
                        captureManager.galleryAddPic();
                        ArrayList<String> paths = new ArrayList<>();
                        paths.add(captureManager.getCurrentPhotoPath());
                        loadAdpater(paths);
                    }
                    break;
                case CROP_PHOTO_CODE:
                    Picasso.with(PublishExchangeActivity.this).load(UCrop.getOutput(data)).placeholder(R.mipmap.default_error).into(select_img_page);
                    break;
                case CROP_CAMERA_CODE:
                    Picasso.with(PublishExchangeActivity.this).load(UCrop.getOutput(data)).placeholder(R.mipmap.default_error).into(select_img_page);
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
}
