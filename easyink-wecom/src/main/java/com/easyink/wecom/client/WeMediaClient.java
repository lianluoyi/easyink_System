package com.easyink.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easyink.common.exception.RetryException;
import com.easyink.wecom.client.retry.EnableRetry;
import com.easyink.wecom.domain.dto.WeMediaDTO;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 类名: 素材管理
 *
 * @author: 1*+
 * @date: 2021-08-18 17:08
 */
@Component
@EnableRetry(retryExceptionClass = RetryException.class)
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeMediaClient {


    /**
     * 上传图片
     *
     * @param multipartFile
     * @return
     */
    @Post(url = "/media/uploadimg")
    WeMediaDTO uploadimg(@DataFile(value = "fieldNameHere") MultipartFile multipartFile, @Header("corpid") String corpId);


    /**
     * 上传临时素材
     * Inputstream 对象
     * 使用byte数组和Inputstream对象时一定要定义fileName属性
     */
    @Post(url = "/media/upload")
    WeMediaDTO upload(@DataFile(value = "media", fileName = "${1}") InputStream file, @DataParam("filename") String filename, @Query("type") String type, @Header("corpid") String corpId, @DataParam("filelength") int filelength, @DataParam("content-type") String contentType);

    /**
     * 上传附件资源
     * <p>
     * 素材上传得到media_id，该media_id仅三天内有效
     * media_id在同一企业内应用之间可以共享
     */
    @Post(url = "/media/upload_attachment")
    WeMediaDTO uploadAttachment(@DataFile(value = "media", fileName = "${1}") InputStream file, String filename, @Query("media_type") String mediaType, @Query("attachment_type") Integer attachmentType, @Header("corpid") String corpId);



}
