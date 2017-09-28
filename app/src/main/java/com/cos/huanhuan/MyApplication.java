package com.cos.huanhuan;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cos.huanhuan.fragments.MessageFragment;
import com.cos.huanhuan.utils.CrashHandlerUtil;
import com.cos.huanhuan.utils.FileUtils;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.RCrashHandler;
import com.squareup.okhttp.Request;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.common.QueuedWork;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.RealTimeLocationMessageProvider;
import io.rong.push.RongPushClient;
import io.rong.push.common.RongException;

/**
 * @author wangfei
 */
public class MyApplication extends Application {

    private RCrashHandler.CrashUploader mCrashUploader;
    private Context mAppContext;
    @Override
    public void onCreate() {

        super.onCreate();
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        Config.DEBUG = true;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            RongIM.init(this);
        }
        mAppContext = getApplicationContext();
        initCrashHandler();

        //控制字体不系统变化
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        PlatformConfig.setWeixin("wx34470b0a77faa852", "8c280585efe26ea15a0cf75cb3f2bf06");
        //PlatformConfig.setWeixin("wx34470b0a77faa852", "6cb85789a5e4f20bf69b268492a2aea8");
        //豆瓣RENREN平台目前只能在服务器端配置
        PlatformConfig.setSinaWeibo("1693410036", "ceca1abae1dd223adcf0f439a2c9d6f5","http://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("1105784182", "PY8bHkSlUjZAyOLJ");
    }
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 初始化崩溃处理器
     */
    private void initCrashHandler() {
//        mCrashUploader = new CrashHandlerUtil.CrashUploader(){
//
//            @Override
//            public void uploadCrashMessage(ConcurrentHashMap<String, Object> infos) {
//                ConcurrentHashMap<String, String> packageInfos = (ConcurrentHashMap<String, String>) infos.get(RCrashHandler.PACKAGE_INFOS_MAP);
//                HashMap<String, String> params = new HashMap<>();
//                params.put("version_name", packageInfos.get(RCrashHandler.VERSION_NAME));
//                params.put("version_code", packageInfos.get(RCrashHandler.VERSION_CODE));
//                params.put("exception_info", (String) infos.get(RCrashHandler.EXCEPETION_INFOS_STRING));
//                params.put("device_info",
//                        RCrashHandler.getInfosStr((ConcurrentHashMap<String, String>) infos.get(RCrashHandler.BUILD_INFOS_MAP)).toString());
//                Log.e("呵呵哈哈哈都是减肥都按地方建瓯盘",params.toString());
//            }
//        };
//        CrashHandlerUtil.getInstance().init(mAppContext,mCrashUploader);
        mCrashUploader = new RCrashHandler.CrashUploader() {
            @Override
            public void uploadCrashMessage(ConcurrentHashMap<String, Object> infos) {
                ConcurrentHashMap<String, String> packageInfos = (ConcurrentHashMap<String, String>) infos.get(RCrashHandler.PACKAGE_INFOS_MAP);

                HashMap<String, String> params = new HashMap<>();
                params.put("version_name", packageInfos.get(RCrashHandler.VERSION_NAME));
                params.put("version_code", packageInfos.get(RCrashHandler.VERSION_CODE));
                params.put("exception_info", (String) infos.get(RCrashHandler.EXCEPETION_INFOS_STRING));
                params.put("device_info",
                        RCrashHandler.getInfosStr((ConcurrentHashMap<String, String>) infos.get(RCrashHandler.BUILD_INFOS_MAP)).toString());
                params.put("system_infos_map",RCrashHandler.getInfosStr((ConcurrentHashMap<String, String>) infos.get(RCrashHandler.SYSTEM_INFOS_MAP)).toString());
                Toast.makeText(mAppContext, "上传", Toast.LENGTH_LONG).show();
                HttpRequest.Log(params, new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                    }
                });
            }
        };
        String CRASH = FileUtils.getRootFilePath() + "huanhuan/crashLog";
        RCrashHandler.getInstance(CRASH).init(mAppContext, mCrashUploader);
    }
}
