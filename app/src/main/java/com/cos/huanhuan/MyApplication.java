package com.cos.huanhuan;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.common.QueuedWork;

/**
 * @author wangfei
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        Config.DEBUG = true;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        PlatformConfig.setWeixin("wx34470b0a77faa852", "6cb85789a5e4f20bf69b268492a2aea8");
        //豆瓣RENREN平台目前只能在服务器端配置
        PlatformConfig.setSinaWeibo("1693410036", "ceca1abae1dd223adcf0f439a2c9d6f5","http://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }
}
