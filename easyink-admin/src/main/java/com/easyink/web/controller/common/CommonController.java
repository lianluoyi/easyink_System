package com.easyink.web.controller.common;

import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.config.ServerConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.FileVo;
import com.easyink.common.encrypt.StrategyCryptoUtil;
import com.easyink.common.exception.file.NoFileException;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.file.FileUploadUtils;
import com.easyink.common.utils.file.FileUtils;
import com.easyink.framework.web.domain.server.SysFile;
import com.easyink.framework.web.service.FileService;
import com.easyink.wecom.domain.dto.common.BatchDecryptDTO;
import com.easyink.wecom.domain.dto.common.BatchDecryptVO;
import com.easyink.wecom.domain.dto.common.SingleDecryptDTO;
import com.easyink.wecom.domain.dto.common.SingleDecryptVO;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.xmlly.util.FileUtil;
import com.xmlly.util.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private RuoYiConfig ruoYiConfig;

    @GetMapping("/getPublicKey")
    @ApiOperation(value = "获取登录公钥")
    public AjaxResult<String> getPublicKey() {
        if (ruoYiConfig.getLoginRsaPublicKey()==null||StringUtils.isEmpty(ruoYiConfig.getLoginRsaPublicKey())) return AjaxResult.error(500,"请联系技术人员配置登录密钥");
        return AjaxResult.success(null, ruoYiConfig.getLoginRsaPublicKey()) ;
    }

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
            // 验证文件名参数
            if (StringUtils.isEmpty(fileName)) {
                throw new NoFileException("文件名不能为空");
            }
            
            // 进行初步的文件名验证，防止路径遍历攻击
            if (fileName.contains("..") || fileName.contains("../") || fileName.contains("..\\")) {
                throw new NoFileException("文件名包含非法字符");
            }
            
            // 验证文件名长度
            if (fileName.length() > 500) {
                throw new NoFileException("文件名过长");
            }
            
            // 过滤文件名中的非法字符
            fileName = FileUtils.replaceFileNameUnValidChar(fileName);
            
            // 文件名分隔符 uuid_fileName.xlsx -> fileName.xlsx
            String splitSign = "_";
            String realFileName = fileName.substring(fileName.indexOf(splitSign) + 1);
            if(!Boolean.FALSE.equals(needTimeStamp)){
                realFileName = System.currentTimeMillis() + realFileName;
            }
            
            // 安全清理文件名，防止HTTP响应拆分攻击（这是主要的安全防护）
            realFileName = FileUtils.sanitizeFileName(realFileName);
            
            // 再次验证清理后的文件名
            if (StringUtils.isEmpty(realFileName)) {
                throw new NoFileException("文件名无效");
            }
            
            String filePath = RuoYiConfig.getDownloadPath() + fileName;

            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            HttpUtil.buildResponseHeader(response, "Content-Disposition",
                    "attachment;fileName=" + FileUtils.setFileDownloadHeader(request, realFileName));
            // 检查文件是否存在
            File file = FileUtil.getFile(filePath);
            if (!file.exists()) {
                log.error("要下载的文件不存在: {}", filePath);
                throw new  NoFileException("no file");
            }
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (Boolean.TRUE.equals(delete)) {
                FileUtils.deleteFile(filePath);
            }
        } catch (IOException e) {
            log.error("下载文件失败, e:{}", ExceptionUtils.getStackTrace(e));
            throw new NoFileException("error");
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
            String url = serverConfig.getUrl() + FileUploadUtils.getPathFileName(filePath) + fileName;
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
            String url = serverConfig.getUrl() + FileUploadUtils.getPathFileName(filePath) + fileName;
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
        // 验证资源名参数
        if (StringUtils.isEmpty(name)) {
            throw new NoFileException("资源名不能为空");
        }
        
        // 进行初步的资源名验证，防止路径遍历攻击
        if (name.contains("..") || name.contains("../") || name.contains("..\\")) {
            throw new NoFileException("资源名包含非法字符");
        }
        
        // 验证资源名长度
        if (name.length() > 1000) {
            throw new NoFileException("资源名过长");
        }
        
        // 本地资源路径
        String localPath = RuoYiConfig.getProfile();
        // 数据库资源地址
        String downloadPath = localPath + StringUtils.substringAfter(name, Constants.RESOURCE_PREFIX);
        // 下载名称
        String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
        
        // 验证提取的文件名
        if (StringUtils.isEmpty(downloadName)) {
            throw new NoFileException("无法提取有效的文件名");
        }
        
        // 安全清理文件名，防止HTTP响应拆分攻击（这是主要的安全防护）
        downloadName = FileUtils.sanitizeFileName(downloadName);
        
        // 再次验证清理后的文件名
        if (StringUtils.isEmpty(downloadName)) {
            throw new NoFileException("文件名无效");
        }
        
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        HttpUtil.buildResponseHeader(response, "Content-Disposition",
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
        FileUploadUtils.fileSuffixVerify(fileName, ruoYiConfig.getFile().getAllowUploadExtensionList());
        try {
            SysFile sysFile;
            sysFile = fileService.upload2CosV2(file, fileName);
            return AjaxResult.success(
                    FileVo.builder()
                            .fileName(sysFile.getFileName())
                            .url(sysFile.getImgUrlPrefix())
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


    @PostMapping("/common/batchDecrypt")
    @ApiOperation("批量解密")
    public AjaxResult<List<BatchDecryptVO>> batchDecrypt(@RequestBody BatchDecryptDTO batchDecryptDTO) {
        if (batchDecryptDTO == null || CollectionUtils.isEmpty(batchDecryptDTO.getEncryptValueList())) {
            return AjaxResult.error("参数错误");
        }
        List<BatchDecryptVO> decryptValueList = new ArrayList<>();
        for (String encryptValue : batchDecryptDTO.getEncryptValueList()) {
            decryptValueList.add(new BatchDecryptVO(encryptValue, StrategyCryptoUtil.decrypt(encryptValue)));
        }
        return AjaxResult.success(decryptValueList);
    }

    @PostMapping("/common/decrypt")
    @ApiOperation("单个解密")
    public AjaxResult<List<BatchDecryptVO>> batchDecrypt(@RequestBody SingleDecryptDTO decryptValue) {
        if (decryptValue == null || decryptValue.getEncryptValue() == null || decryptValue.getEncryptValue().isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        return AjaxResult.success(new SingleDecryptVO(StrategyCryptoUtil.decrypt(decryptValue.getEncryptValue())));
    }



}

