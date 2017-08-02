package com.cos.huanhuan.model;

import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/2.
 */

public interface MultiPartRequestInterface {
    void addFileUpload(String param, File file);

            void addStringUpload(String param, String content);

            Map<String,File> getFileUploads();

            Map<String,String> getStringUploads();
}
