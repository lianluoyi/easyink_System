package com.easyink.common.config;

import lombok.Data;

@Data
public class FileConfig {

    /**
     * 是否开启云存储
     */
    private boolean startCosUpload = false;
    /**
     * 腾讯云存储相关配置
     */
    private CosConfig cos;
    /**
     * 文件前缀
     */
    private String imgUrlPrefix;

    /**
     * 允许上传文件的后缀
     */
    private String[] allowUploadExtensionList;

}
