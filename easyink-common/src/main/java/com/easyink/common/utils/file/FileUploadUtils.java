package com.easyink.common.utils.file;

import com.easyink.common.config.CosConfig;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.material.UrlFileTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.file.FileNameLengthLimitExceededException;
import com.easyink.common.exception.file.FileSizeLimitExceededException;
import com.easyink.common.exception.file.InvalidExtensionException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

/**
 * 文件上传工具类
 *
 * @author admin
 */
@Slf4j
public class FileUploadUtils {
    private FileUploadUtils() {
    }

    /**
     * 默认大小 50M
     */
    private static final long DEFAULT_MAX_SIZE = (long) 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    private static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 默认上传的地址
     */
    private static String defaultBaseDir = RuoYiConfig.getProfile();


    private static String getDefaultBaseDir() {
        return defaultBaseDir;
    }


    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     */
    public static String upload(MultipartFile file) throws IOException {
        try {
            return upload(getDefaultBaseDir(), file, MimeTypeUtils.getDefaultAllowedExtension());
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }


    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 文件名称
     */
    public static String upload(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, MimeTypeUtils.getDefaultAllowedExtension());
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 文件名称
     * @throws IOException
     */
    public static String uploadCert(String baseDir, MultipartFile file) throws IOException, InvalidExtensionException {
        int fileNamelength = 0;
        String fileName = StringUtils.EMPTY;
        File desc;
        if (file != null) {
            if (StringUtils.isNotEmpty(file.getOriginalFilename())) {
                fileNamelength = file.getOriginalFilename().length();
            }
            fileName = file.getOriginalFilename();
            desc = getAbsoluteFile(baseDir, fileName);
            file.transferTo(desc);
        }
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }
        assertAllowed(file, MimeTypeUtils.getDefaultAllowedExtension());
        return fileName;
    }

    /**
     * 文件上传
     *
     * @param baseDir          相对应用的基目录
     * @param file             上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws FileSizeLimitExceededException       如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException                          比如读写文件出错时
     * @throws InvalidExtensionException            文件校验异常
     */
    public static String upload(String baseDir, MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException, InvalidExtensionException {
        int fileNamelength = 0;
        String fileName = StringUtils.EMPTY;
        if (file != null) {
            checkFile(file, allowedExtension, fileNamelength);
            fileName = extractFilename(file);
            File desc = getAbsoluteFile(baseDir, fileName);
            file.transferTo(desc);
        }
        return fileName;
    }

    /**
     * 检查文件大小，文件名称大小，扩展名
     *
     * @param file             文件
     * @param allowedExtension 允许的扩展名
     * @param fileNamelength   文件名长度
     * @throws InvalidExtensionException
     */
    private static void checkFile(MultipartFile file, String[] allowedExtension, int fileNamelength) throws InvalidExtensionException {
        checkFile(file,fileNamelength);
        assertAllowed(file, allowedExtension);
    }

    /**
     * 检查文件大小，文件名称大小
     * @param file 文件
     * @param fileNamelength 文件名长度
     */
    private static void checkFile(MultipartFile file, int fileNamelength) {
        if (StringUtils.isNotEmpty(file.getOriginalFilename())) {
            fileNamelength = file.getOriginalFilename().length();
        }
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }
        assertFileSize(file);
    }



    /**
     * 通过流进行文件上传
     *
     * @param in 流
     */
    public static String upload2Cos(InputStream in,String fileName, String fileExtension, CosConfig cosConfig) throws IOException {
        try {
            return upload2Cos(in,fileName, fileExtension, MimeTypeUtils.getDefaultAllowedExtension(), cosConfig);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }


    /**
     * 上传文件至COS
     *
     * @param file {@link MultipartFile}
     * @param cosConfig {@link CosConfig}
     * @return 上传后的文件名称
     * @throws FileSizeLimitExceededException 文件超出限制大小
     * @throws IOException IO异常
     * @throws FileNameLengthLimitExceededException 文件名称超长
     */
    public static String upload2Cos(MultipartFile file, CosConfig cosConfig)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException {
        return upload2Cos(file, cosConfig, null);
    }

    /**
     * 上传文件至COS
     *
     * @param file {@link MultipartFile}
     * @param cosConfig {@link CosConfig}
     * @param filename 文件名称
     * @return 上传后的文件名称
     * @throws FileSizeLimitExceededException 文件超出限制大小
     * @throws IOException IO异常
     * @throws FileNameLengthLimitExceededException 文件名称超长
     */
    public static String upload2Cos(MultipartFile file, CosConfig cosConfig, String filename)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException {
        if (file == null) {
            return null;
        }
        int fileNameLength = 0;
        String md5Name;
        // 校验文件大小，文件名称大小，文件类型
        checkFile(file, fileNameLength);
        // 获取MD5名称
        md5Name = getMd5Name(file, filename);
        uploadCosClient(cosConfig, md5Name, file);
        return md5Name;
    }

    private static void uploadCosClient(CosConfig cosConfig, String fileName, MultipartFile file) throws IOException {
        COSClient cosClient = getCosClient(cosConfig);
        // 指定要上传到 COS 上对象键
        ObjectMetadata objectMetadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(), fileName, file.getInputStream(), objectMetadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        String cosImgUrlPrefix = cosConfig.getCosImgUrlPrefix();
        log.info("{}", cosImgUrlPrefix + fileName);
        log.info("腾讯cos上传信息：{}", new ObjectMapper().writeValueAsString(putObjectResult));
        cosClient.shutdown();
    }

    /**
     * 生成 cos 客户端
     * @param cosConfig 云配置
     * @return cos 客户端
     */
    private static COSClient getCosClient(CosConfig cosConfig){
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(cosConfig.getSecretId(), cosConfig.getSecretKey());
        // 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(cosConfig.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }
    /**
     * 将流中的文件传到腾讯云
     *
     * @param inputStream      输入流
     * @param fileExtension    被写入流的文件的后缀名
     * @param allowedExtension 允许的后缀名
     * @throws FileSizeLimitExceededException 文件大小超过上限
     * @throws InvalidExtensionException      文件后缀名非法
     */
    private static String upload2Cos(InputStream inputStream, String originFileName, String fileExtension, String[] allowedExtension, CosConfig cosConfig) throws FileSizeLimitExceededException, InvalidExtensionException {

        String fileName = DateUtils.datePath() + WeConstans.SLASH + originFileName;
        if (!isAllowedExtension(fileExtension, allowedExtension)) {
            throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, fileExtension, fileName);
        }
        COSClient cosClient = getCosClient(cosConfig);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(), fileName, inputStream, objectMetadata);
        cosClient.putObject(putObjectRequest);
        cosClient.shutdown();
        return fileName;
    }

    /**
     * 不校验后缀名的上传方法
     *
     * @param inputStream
     * @param originFileName
     * @param cosConfig
     * @return
     * @throws FileSizeLimitExceededException
     * @throws InvalidExtensionException
     */
    public static String upload2Cos(InputStream inputStream, String originFileName, CosConfig cosConfig) throws FileSizeLimitExceededException {

        String fileName = DateUtils.datePath() + WeConstans.SLASH + originFileName;
        COSClient cosClient = getCosClient(cosConfig);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(), fileName, inputStream, objectMetadata);
        cosClient.putObject(putObjectRequest);
        cosClient.shutdown();
        return fileName;
    }

    /**
     * 不校验后缀名，获取文件名称的上传方法 【编辑附件名称】
     *
     * @param inputStream {@link InputStream}
     * @param fileName 文件名称
     * @param cosConfig 云存储桶配置信息
     * @return 上传后的文件名称
     * @throws FileSizeLimitExceededException
     * @throws InvalidExtensionException
     */
    public static String reUpload2CosFileByName(InputStream inputStream, String fileName, CosConfig cosConfig) throws FileSizeLimitExceededException {
        COSClient cosClient = getCosClient(cosConfig);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(), fileName, inputStream, objectMetadata);
        cosClient.putObject(putObjectRequest);
        cosClient.shutdown();
        return fileName;
    }

    /**
     * 以新的文件名称重新上传文件-自V1.31.3重构文件上传代码后弃用，文件上传请使用uploadV2方法
     *
     * @param url 文件url
     * @param ruoYiConfig {@link RuoYiConfig}
     * @param fileName 文件名
     * @param corpId 企业ID
     * @return 新的文件Url
     */
    @Deprecated
    public static String reUploadFile(String url, RuoYiConfig ruoYiConfig, String fileName, String corpId) {
        String newUrl = null;
        try {
            // 路径不以"/profile"开头，直接使用URL获取inputStream流
            if (!url.startsWith(Constants.RESOURCE_PREFIX)) {
                // 获取原始的文件流
                InputStream inputStream = FileUtils.downloadFile(url);
                // COS的url前缀
                String imgUrlPrefix = ruoYiConfig.getFile().getCos().getCosImgUrlPrefix();
                // 以新的文件名称重新上传，获取文件名
                FileUploadUtils.reUpload2CosFileByName(inputStream, fileName, ruoYiConfig.getFile().getCos());
                newUrl = imgUrlPrefix + fileName;
            } else {
                // 本地上传，将url路径转换为绝对路径获取文件
                File file = new File(url.replace(Constants.RESOURCE_PREFIX, RuoYiConfig.getProfile()));
                // 将文件使用新的文件名称转换为Multipart对象
                MultipartFile multipartFile = convertToMultipartFile(file, fileName);
                // 获取日期编码后的文件名
                fileName = extractFilename(multipartFile);
                // 获取文件上传的绝对路径
                File desc = getAbsoluteFile(RuoYiConfig.getProfile(), fileName);
                // 上传文件到绝对路径
                multipartFile.transferTo(desc);
                // 拼接本地资源映射地址
                newUrl = getPathFileName(RuoYiConfig.getProfile()) + fileName;
            }
        } catch (IOException e) {
            log.info("[重新上传] 重新获取新的文件名URL异常，corpId:{}，上传的文件名:{}", corpId, fileName);
        }
        // 返回拼接好的文件Url
        return newUrl;
    }

    /**
     * 转换为multipart对象
     *
     * @param file 文件
     * @param newFileName 新文件名
     * @return MultipartFile
     */
    public static MultipartFile convertToMultipartFile(File file, String newFileName) {
        try {
            // 读取文件内容
            FileInputStream fileInputStream = new FileInputStream(file);

            // 使用MockMultipartFile构造器创建MultipartFile对象
            MultipartFile multipartFile = new MockMultipartFile(
                    // 文件名
                    newFileName,
                    // originalFilename
                    newFileName,
                    // content type（可以根据需要设置）
                    null,
                    // 文件内容的输入流
                    fileInputStream
            );
            return multipartFile;
        } catch (IOException e) {
            log.info("[重新上传文件] 转换文件multipart类型异常，异常原因：{}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 获取带有后缀的文件名
     *
     * @param connection {@link URLConnection}
     * @param fileName 文件名
     * @return 带有后缀的文件名
     */
    private static String getSuffixName(URLConnection connection, String fileName) {
        // 后缀名分隔符
        final String INTERCEPT = ".";
        // 文件类型位置
        final int SUFFIX_NUM = 2;
        // 如果文件名本身带有后缀名，则不拼接
        if (fileName.split(INTERCEPT).length >= SUFFIX_NUM) {
            return fileName;
        }
        // 获取文件类型
        String suffix = UrlFileTypeEnum.getSuffixByType(connection.getContentType());
        // 拼接文件类型
        if (StringUtils.isNotBlank(suffix)) {
            return fileName + suffix;
        }
        return fileName;
    }

    public static String uploadFile(String baseDir, MultipartFile file)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException {
        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }

        String fileName = extractFilename(file);

        File desc = getAbsoluteFile(baseDir, fileName);
        file.transferTo(desc);
        return getPathFileName(baseDir) + fileName;
    }

    /**
     * 编码文件名
     */
    private static String extractFilename(MultipartFile file) {
        String fileName;
        fileName = DateUtils.datePath() + WeConstans.SLASH + file.getOriginalFilename();
        return fileName;
    }

    private static File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            boolean isCreate = desc.createNewFile();
            if (!isCreate){
                log.error("文件创建失败");
            }
        }
        return desc;
    }

    public static String getPathFileName(String uploadDir) {
        int dirLastIndex = RuoYiConfig.getProfile().length() + 1;
        String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
        String pathFileName;
        if (org.apache.commons.lang3.StringUtils.isBlank(currentDir)) {
            pathFileName = Constants.RESOURCE_PREFIX + WeConstans.SLASH;
        } else {
            pathFileName = Constants.RESOURCE_PREFIX + WeConstans.SLASH + currentDir + WeConstans.SLASH;
        }
        return pathFileName;
    }

    /**
     * 文件大小校验，扩展名校验
     *
     * @param file 上传的文件
     * @throws FileSizeLimitExceededException 如果超出最大大小
     */
    public static void assertAllowed(MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, InvalidExtensionException {
        String fileName = StringUtils.EMPTY;
        if (file != null) {
            assertFileSize(file);
            fileName = file.getOriginalFilename();
        }

        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == MimeTypeUtils.getImageExtension()) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.getFlashExtension()) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.getMediaExtension()) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension,
                        fileName);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, fileName);
            }
        }

    }


    /**
     * 文件大小校验，扩展名校验
     *
     * @param fileName          上传的文件
     * @param allowedExtension  允许上传的文件类型
     */
    public static void fileSuffixVerify(String fileName, String[] allowedExtension){
        if (StringUtils.isBlank(fileName)) {
            throw new CustomException(ResultTip.TIP_FILE_NAME_IS_NULL);
        }
        String extension = FilenameUtils.getExtension(fileName);
        if (!isAllowedExtension(extension, allowedExtension)) {
            throw new CustomException(ResultTip.TIP_NOT_ALLOW_UPLOAD_FILE_TYPE);
        }
    }

    /**
     * 文件大小校验
     *
     * @param file 文件
     */
    public static void assertFileSize(MultipartFile file) {
        if (file != null && file.getSize() > DEFAULT_MAX_SIZE) {
            throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024);
        }
    }
    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension 扩展名
     * @param allowedExtension 允许的扩展名
     * @return 是否是允许
     */
    private static boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    private static String getExtension(MultipartFile file) {
        String extension = StringUtils.EMPTY;
        if (file != null) {
            extension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (StringUtils.isEmpty(extension) && file.getContentType() != null) {
                extension = MimeTypeUtils.getExtension(file.getContentType());
            }
        }
        return extension;
    }

    /**
     * 《本地文件上传第二版》
     * 该方法根据文件内容转换成MD5值，创建一个本地文件，并将上传的文件内容写入。
     *
     * @param file             上传的文件内容
     * @param allowedExtension 上传的文件类型
     * @return 返回上传成功的文件资源映射路径
     * @throws FileSizeLimitExceededException       如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException                          比如读写文件出错时
     * @throws InvalidExtensionException            文件校验异常
     */
    public static String uploadV2(MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException, InvalidExtensionException {
        return uploadV2(null, file, allowedExtension);
    }

    /**
     * 《本地文件上传第二版》
     * 该方法根据文件名称和内容转换成MD5值，创建一个本地文件，并将上传的文件内容写入。
     *
     * @param filename         上传的文件名称
     * @param file             上传的文件内容
     * @param allowedExtension 上传的文件类型
     * @return 返回上传成功的文件资源映射路径
     * @throws FileSizeLimitExceededException       如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException                          比如读写文件出错时
     * @throws InvalidExtensionException            文件校验异常
     */
    public static String uploadV2(String filename, MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException, InvalidExtensionException {
        if (file == null) {
            return null;
        }
        int filenameLength = 0;
        String md5Name;
        // 校验文件大小，文件名称大小，文件类型
        checkFile(file, allowedExtension, filenameLength);
        // 获取MD5名称
        md5Name = getMd5Name(file, filename);
        // 根据MD5名称，获取创建本地文件的完整路径
        String createFilePath = getCreateFilePath(md5Name);
        // 创建本地文件
        File desc = createNewLocalFile(createFilePath);
        // 将上传的文件数据写入本地文件
        file.transferTo(desc);
        return getPathFileName(RuoYiConfig.getUploadPath()) + DateUtils.datePath() + WeConstans.SLASH + md5Name;
    }

    /**
     * 根据MD5名称，获取本地创建文件的路径
     *
     * @param md5Name 文件MD5名称
     * @return 本地创建文件路径
     */
    private static String getCreateFilePath(String md5Name) {
        if (StringUtils.isBlank(md5Name)) {
            return null;
        }
        return RuoYiConfig.getUploadPath() + WeConstans.SLASH + DateUtils.datePath() + WeConstans.SLASH + md5Name;
    }

    /**
     * 根据指定的FileName，MultipartFile文件的内容获取MD5值，作为文件名称。
     *
     * @param file 上传的文件 {@link MultipartFile}
     * @return 转换MD5后的文件名称 + 后缀
     * @throws IOException
     */
    public static String getMd5Name(MultipartFile file, String filename) throws IOException {
        if (file == null) {
            throw new CustomException(ResultTip.TIP_MULTIPART_FILE_NOT_EXISTS);
        }
        // 获取文件字节数组
        byte[] bytes = file.getBytes();
        // 计算字节数组的 MD5 值
        String md5Hex = DigestUtils.md5DigestAsHex(bytes);
        if (StringUtils.isBlank(filename)) {
            // 文件名为空，使用原始的文件名称
            filename = file.getOriginalFilename();
        }
        // 获取文件的后缀名
        String fileExtension = filename.substring(filename.lastIndexOf("."));
        return md5Hex + fileExtension;
    }

    /**
     * 以文件的完整路径，创建需要上传的空文件。
     * example: C:/ruoyi/uploadPath/YYYY/MM/DD/filename.jpg
     *
     * @param uploadDir 上传的文件完整路径 + 文件名称
     * @return {@link File}
     * @throws IOException
     */
    private static File createNewLocalFile(String uploadDir) throws IOException {
        // 创建文件
        File desc = new File(uploadDir);
        // 判断父目录是否存在
        if (!desc.getParentFile().exists()) {
            // 不存在则创建
            desc.getParentFile().mkdirs();
        }
        // 判断文件是否存在
        if (!desc.exists()) {
            // 不存在则创建新文件
            boolean isCreate = desc.createNewFile();
            // 创建失败
            if (!isCreate) {
                log.error("本地文件创建失败，文件完整路径:{}", uploadDir);
            }
        }
        return desc;
    }
}
