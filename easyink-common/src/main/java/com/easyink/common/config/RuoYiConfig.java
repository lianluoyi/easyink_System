package com.easyink.common.config;

import com.easyink.common.constant.emple.EmployCodeConstants;
import com.easyink.common.utils.OsUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author admin
 */
@Component
@Data
@ConfigurationProperties(prefix = "ruoyi")
public class RuoYiConfig {

    /**
     * 当前代码版本
     */
    private String version;
    /**
     * icp 备案
     */
    private String icp;
    /**
     * 版本说明
     */
    private String releaseNotes;
    /**
     * 默认应用名
     */
    private String defaultAppName;

    /**
     * 上传路径
     */
    private static String profile;

    /**
     * 获取地址开关
     */
    private boolean addressEnabled;

    /**
     * 企业微信账号登录系统默认密码
     */
    private String weUserDefaultPwd;
    /**
     * 登录密码RSA公钥
     */
    private String loginRsaPublicKey ;
    /**
     * 登录密码RSA私钥
     */
    private String loginRsaPrivateKey;

    private FileConfig file;
    /**
     * 服务器类型
     */
    private String serverType;
    /**
     * 三方引用默认域名
     */
    private ThirdDefaultDomainConfig thirdDefaultDomain;

    private WeProvider provider;

    private WeCrypt selfBuild;
    /**
     * 客户专属活码过期时间, 单位小时
     */
    private Integer customerEmpleCodeExpireTime = EmployCodeConstants.CUSTOMER_EMPLOY_EXPIRE_HOURS;

    /**
     * 匿名访问的URL
     */
    private String[] anonUrl;

    /**
     * 是否允许返回异常详情给前端
     */
    private boolean enableExceptionDetailResp;

    /**
     * 压测标识
     */
    private boolean pressure;

    /**
     * 是否内部应用服务器
     *
     * @return true是内部，false三方应用
     */
    public boolean isInternalServer() {
        String internalServerType = "internal";
        return StringUtils.isBlank(serverType) || internalServerType.equals(serverType);
    }

    /**
     * 是否三方应用服务器
     *
     * @return true是三方应用，false内部
     */
    public boolean isThirdServer() {
        return !isInternalServer();
    }

    public static String getProfile() {
        if (OsUtils.isWindows()) {
            return "C:/ruoyi/uploadPath";

        }
        return "/app/project/pic";
    }


    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getProfile() + "/avatar";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath() {
        return getProfile() + "/download/";
    }

    /**
     * 获取会话存档素材下载路径
     */
    public static String getDownloadWeWorkPath() {
        return getProfile() + "/download/media_data/{}/{}";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getProfile();
    }


}
