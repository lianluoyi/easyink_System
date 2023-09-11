package com.easyink.framework.web.service;


import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.exception.file.InvalidExtensionException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.file.FileUploadUtils;
import com.easyink.common.utils.file.FileUtils;
import com.easyink.common.utils.file.MimeTypeUtils;
import com.easyink.framework.web.domain.server.SysFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;


/**
 * 类名: 文件服务
 * @author: 1*+
 * @date: 2021-08-05 10:38
 */
@Component
@Slf4j
public class FileService {


    @Autowired
    private RuoYiConfig ruoYiConfig;

    /**
     * 文件上传-方法已在EasyInk V1.32.0版本弃用，请使用upload2CosV2()方法
     *
     * @param file 文件
     * @throws IOException IO异常
     */
    @Deprecated
    public SysFile upload(MultipartFile file) throws IOException {
        try {
            //只校验文件大小，不校验扩展名
            FileUploadUtils.assertFileSize(file);
            String fileName;
            String imgUrlPrefix;
            //开启云上传
            if (ruoYiConfig.getFile().isStartCosUpload()) {
                //开启云上传开关则云上传，不然上传本地
                fileName = FileUploadUtils.upload2Cos(file, ruoYiConfig.getFile().getCos());
                imgUrlPrefix = ruoYiConfig.getFile().getCos().getCosImgUrlPrefix();
            } else {//本地上传
                File osFile = new File(RuoYiConfig.getProfile());
                if (!osFile.exists()) {
                    osFile.mkdirs();
                }
                // 上传至本地编码日期路径下
                FileUploadUtils.upload(osFile.getPath(), file);
                // 使用原始文件名
                fileName = file.getOriginalFilename();
                // 获取文件资源映射前缀
                imgUrlPrefix = FileUploadUtils.getPathFileName(osFile.getPath()) + DateUtils.datePath() + WeConstans.SLASH;
            }
            return SysFile.builder()
                    .fileName(fileName)
                    .imgUrlPrefix(imgUrlPrefix)
                    .build();

        } catch (Exception e) {
            log.error("文件上传异常：ex{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }

    }

    /**
     * 文件上传-方法已在EasyInk V1.32.0版本弃用，请使用upload2CosV2()方法
     *
     * @param file 文件
     * @throws IOException IO异常
     */
    @Deprecated
    public SysFile upload2Cos(MultipartFile file, String fileName) throws IOException {
        try {
            //只校验文件大小，不校验扩展名
            FileUploadUtils.assertFileSize(file);
            String urlfileName;
            String imgUrlPrefix;
            //开启云上传
            if (ruoYiConfig.getFile().isStartCosUpload()) {
                //开启云上传开关则云上传，不然上传本地
                urlfileName = FileUploadUtils.upload2Cos(file, ruoYiConfig.getFile().getCos(), fileName);
                imgUrlPrefix = ruoYiConfig.getFile().getCos().getCosImgUrlPrefix();
            } else {
                //本地上传
                File osFile = new File(RuoYiConfig.getProfile());
                if (!osFile.exists()) {
                    osFile.mkdirs();
                }
                // 上传至本地编码日期路径下
                FileUploadUtils.upload(osFile.getPath(), file);
                // 使用原始文件名
                urlfileName = fileName;
                imgUrlPrefix = FileUploadUtils.getPathFileName(osFile.getPath()) + DateUtils.datePath() + WeConstans.SLASH;
            }
            return SysFile.builder()
                    .fileName(urlfileName)
                    .imgUrlPrefix(imgUrlPrefix)
                    .build();
        } catch (Exception e) {
            log.error("文件上传异常：ex{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }

    }
    /**
     * 获取图片(本地,或者网络图片)
     *
     * @param fileName
     * @param rp
     */
    public void findImage(String fileName, HttpServletResponse rp) {
        try {
            String fileDownUrl;
            rp.setContentType("image/png");
            //开启云上传
            if (ruoYiConfig.getFile().isStartCosUpload()) {
                fileDownUrl = ruoYiConfig.getFile().getCos().getCosImgUrlPrefix();
                FileUtils.downloadFile(fileDownUrl + fileName, rp.getOutputStream());
            } else {
                fileDownUrl = RuoYiConfig.getProfile() + WeConstans.SLASH + fileName;
                FileUtils.writeBytes(fileDownUrl, rp.getOutputStream());
            }
        } catch (Exception e) {
            log.error("获取图片异常：ex{}", ExceptionUtils.getStackTrace(e));

        }
    }

    /**
     * 《文件上传第二版》
     * 说明：因上传文件涉及到的功能繁多，原有的逻辑无法满足，所以重构文件上传方法，将文件内容转MD5作为文件名上传，防止因文件名相同覆盖文件。
     * 该方法根据传来的MultipartFile格式的文件和fileName参数，进行云存储/本地上传。
     *
     * @param file     {@link MultipartFile} 文件内容
     * @param fileName 文件名称
     * @return {@link SysFile} filename-返回给前端的文件名；imgUrlPrefix-上传后的文件访问路径【配置的云存储地址 / 本地的资源映射地址 “/profile/YYYY/MM/DD/”】
     * @throws IOException IO异常
     * @since 2023-08-07 EasyInk V1.32.0
     */
    public SysFile upload2CosV2(MultipartFile file, String fileName) throws IOException, InvalidExtensionException {
        try {
            // 只校验文件大小，不校验扩展名
            FileUploadUtils.assertFileSize(file);
            // 返回的上传文件前缀地址
            String filePrefix;
            if (ruoYiConfig.getFile().isStartCosUpload()) {
                // 上传至云存储
                String returnFilename = FileUploadUtils.upload2Cos(file, ruoYiConfig.getFile().getCos(), fileName);
                // 获取云存储Url前缀地址
                filePrefix = ruoYiConfig.getFile().getCos().getCosImgUrlPrefix() + returnFilename;
            } else {
                // 上传文件至本地，并获取本地上传资源映射地址
                filePrefix = FileUploadUtils.uploadV2(fileName, file, MimeTypeUtils.getDefaultAllowedExtension());
            }
            return SysFile.builder()
                    .fileName(fileName)
                    .imgUrlPrefix(filePrefix)
                    .build();
        } catch (Exception e) {
            log.error("文件上传异常：ex{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

}
