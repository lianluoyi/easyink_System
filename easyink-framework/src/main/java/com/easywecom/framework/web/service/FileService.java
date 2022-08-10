package com.easywecom.framework.web.service;


import com.easywecom.common.config.RuoYiConfig;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.utils.file.FileUploadUtils;
import com.easywecom.common.utils.file.FileUtils;
import com.easywecom.framework.web.domain.server.SysFile;
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
     * 文件上传
     *
     * @param file 文件
     * @throws IOException IO异常
     */
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
                fileName = FileUploadUtils.upload(osFile.getPath(), file);
                imgUrlPrefix = RuoYiConfig.getProfile();
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
     * 文件上传
     *
     * @param file 文件
     * @throws IOException IO异常
     */
    public SysFile upload2Cos(MultipartFile file, String fileName) throws IOException {
        try {
            //只校验文件大小，不校验扩展名
            FileUploadUtils.assertFileSize(file);
            String urlfileName;
            String imgUrlPrefix;
            //开启云上传
            //开启云上传开关则云上传，不然上传本地
            urlfileName = FileUploadUtils.upload2Cos(file, ruoYiConfig.getFile().getCos(), fileName);
            imgUrlPrefix = ruoYiConfig.getFile().getCos().getCosImgUrlPrefix();
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


}
