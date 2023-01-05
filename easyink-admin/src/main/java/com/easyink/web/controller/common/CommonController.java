package com.easyink.web.controller.common;

import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.config.ServerConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.FileVo;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.file.FileUploadUtils;
import com.easyink.common.utils.file.FileUtils;
import com.easyink.framework.web.domain.server.SysFile;
import com.easyink.framework.web.service.FileService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 类名: CommonController
 *
 * @author: 1*+
 * @date: 2021-08-27 16:09
 */
@Slf4j
@RestController
@ApiSupport(order = 2, author = "1*+")
@Api(value = "CommonController", tags = "通用接口")
public class CommonController {

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private FileService fileService;

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete   是否删除
     */
    @ApiOperation(value = "通用下载")
    @GetMapping("common/download")
    public void fileDownload(@ApiParam("文件名") String fileName, @ApiParam("是否删除本地文件") Boolean delete, @ApiParam("是否带时间戳") Boolean needTimeStamp, HttpServletResponse response, HttpServletRequest request) {
        try {
            if (!FileUtils.isValidFilename(fileName)) {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            // 文件名分隔符 uuid_fileName.xlsx -> fileName.xlsx
            String splitSign = "_";
            String realFileName = fileName.substring(fileName.indexOf(splitSign) + 1);
            if(!Boolean.FALSE.equals(needTimeStamp)){
                realFileName = System.currentTimeMillis() + realFileName;
            }
            String filePath = RuoYiConfig.getDownloadPath() + fileName;

            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + FileUtils.setFileDownloadHeader(request, realFileName));
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (Boolean.TRUE.equals(delete)) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求
     */
    @ApiOperation(value = "通用上传")
    @PostMapping(value = "/common/upload", headers = "content-type=multipart/form-data")
    public AjaxResult uploadFile(@ApiParam(value = "上传文件", required = true) MultipartFile file) {
        try {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("fileName", fileName);
            ajax.put("url", url);
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求
     */
    @ApiOperation(value = "上传域名校验证书")
    @PostMapping("/common/uploadCert")
    public AjaxResult uploadCert(@ApiParam("上传域名文件") MultipartFile file) {
        try {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.uploadCert(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("fileName", fileName);
            ajax.put("url", url);
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 本地资源通用下载
     */
    @ApiOperation(value = "本地资源下载")
    @GetMapping("/common/download/resource")
    public void resourceDownload(@ApiParam("资源名") String name, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 本地资源路径
        String localPath = RuoYiConfig.getProfile();
        // 数据库资源地址
        String downloadPath = localPath + StringUtils.substringAfter(name, Constants.RESOURCE_PREFIX);
        // 下载名称
        String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition",
                "attachment;fileName=" + FileUtils.setFileDownloadHeader(request, downloadName));
        FileUtils.writeBytes(downloadPath, response.getOutputStream());
    }


    /**
     * 网络资源通用下载
     */
    @ApiOperation(value = "网络资源通用下载")
    @GetMapping("/common/download/url")
    public void webResourceDownload(@ApiParam("资源链接") String url, HttpServletRequest request, HttpServletResponse response) throws IOException {
        FileUtils.downloadFile(url, response.getOutputStream());
    }


    /**
     * 通用上传请求
     */
    @ApiOperation(value = "上传到云存储")
    @PostMapping("/common/uploadFile2Cos")
    public AjaxResult uploadFile2Cos(@ApiParam("资源文件") MultipartFile file,String fileName) {
        try {
            SysFile sysFile;
            if (StringUtils.isNotBlank(fileName)) {
                sysFile = fileService.upload2Cos(file, fileName);
            } else {
                sysFile = fileService.upload(file);
            }
            return AjaxResult.success(
                    FileVo.builder()
                            .fileName(sysFile.getFileName())
                            .url(sysFile.getImgUrlPrefix() + sysFile.getFileName())
                            .build()
            );
        } catch (Exception e) {
            return AjaxResult.error("不支持当前文件上传或文件过大建议传20MB以内的文件");
        }
    }


    /**
     * 获取图片
     */
    @ApiOperation("获取图片")
    @GetMapping("/common/findImage")
    public void findImage(HttpServletResponse response, @ApiParam("文件名") String fileName) {
        fileService.findImage(fileName, response);
    }





}
