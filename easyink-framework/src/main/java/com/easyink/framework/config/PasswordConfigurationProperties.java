package com.easyink.framework.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * 密码配置属性类
 * 参考CasConfigurationProperties的配置读取方式
 * 
 * @author system
 * @date 2024-12-19
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "password")
public class PasswordConfigurationProperties {

    /**
     * 密码过期配置
     */
    private Expire expire = new Expire();

    /**
     * 密码强度配置
     */
    private Strength strength = new Strength();

    /**
     * 密码过期配置内部类
     */
    @Data
    public static class Expire {
        /**
         * 密码过期天数，默认90天
         */
        private Integer days = 90;

        /**
         * 是否开启密码过期校验，默认false
         */
        private Check check = new Check();

        /**
         * 密码过期检查配置内部类
         */
        @Data
        public static class Check {
            /**
             * 是否启用密码过期检查
             */
            private Boolean enabled = false;
        }
    }

    /**
     * 密码强度配置内部类
     */
    @Data
    public static class Strength {
        /**
         * 密码最小长度，默认8位
         */
        private Integer minLength = 8;

        /**
         * 是否要求包含大写字母，默认true
         */
        private Boolean requireUppercase = true;

        /**
         * 是否要求包含小写字母，默认true
         */
        private Boolean requireLowercase = true;

        /**
         * 是否要求包含数字，默认true
         */
        private Boolean requireDigit = true;

        /**
         * 是否要求包含特殊字符，默认true
         */
        private Boolean requireSpecialChar = true;

        /**
         * 是否检查弱密码，默认true
         */
        private Boolean checkWeakPassword = true;
    }

    /**
     * 配置初始化后的验证和日志打印
     */
    @PostConstruct
    public void init() {
        log.info("=== 密码配置信息加载完成 ===");
        log.info("密码过期天数(expire.days): {}", expire.getDays());
        log.info("密码过期检查启用(expire.check.enabled): {}", expire.getCheck().getEnabled());
        log.info("密码最小长度(strength.minLength): {}", strength.getMinLength());
        log.info("要求大写字母(strength.requireUppercase): {}", strength.getRequireUppercase());
        log.info("要求小写字母(strength.requireLowercase): {}", strength.getRequireLowercase());
        log.info("要求数字(strength.requireDigit): {}", strength.getRequireDigit());
        log.info("要求特殊字符(strength.requireSpecialChar): {}", strength.getRequireSpecialChar());
        log.info("检查弱密码(strength.checkWeakPassword): {}", strength.getCheckWeakPassword());
        log.info("=== 密码配置信息结束 ===");

        // 配置验证
        validateConfiguration();
    }

    /**
     * 验证密码配置的合理性
     */
    private void validateConfiguration() {
        boolean hasErrors = false;
        boolean hasWarnings = false;

        // 验证密码过期天数
        if (expire.getDays() == null || expire.getDays() <= 0) {
            log.error("密码配置错误: expire.days 必须大于0，当前值: {}", expire.getDays());
            hasErrors = true;
        }

        // 验证密码最小长度
        if (strength.getMinLength() == null || strength.getMinLength() < 6) {
            log.error("密码配置错误: strength.minLength 不能小于6位，当前值: {}", strength.getMinLength());
            hasErrors = true;
        }

        // 验证密码最小长度不能过大
        if (strength.getMinLength() > 50) {
            log.warn("密码配置警告: strength.minLength 设置过大({})，可能影响用户体验", strength.getMinLength());
            hasWarnings = true;
        }

        // 验证密码过期天数不能过短
        if (expire.getDays() < 7) {
            log.warn("密码配置警告: expire.days 设置过短({}天)，可能影响用户体验", expire.getDays());
            hasWarnings = true;
        }

        // 验证密码过期天数不能过长
        if (expire.getDays() > 365) {
            log.warn("密码配置警告: expire.days 设置过长({}天)，可能影响安全性", expire.getDays());
            hasWarnings = true;
        }

        // 验证所有强度要求都为false的情况
        if (!strength.getRequireUppercase() && !strength.getRequireLowercase() 
            && !strength.getRequireDigit() && !strength.getRequireSpecialChar()) {
            log.warn("密码配置警告: 所有字符类型要求都设置为false，密码强度要求过低");
            hasWarnings = true;
        }

        if (hasErrors) {
            log.error("密码配置存在错误，请检查 application.yml 中的 password 配置项");
        } else if (hasWarnings) {
            log.warn("密码配置存在警告，请根据实际情况调整配置");
        } else {
            log.info("密码配置验证通过");
        }
    }

    /**
     * 获取密码过期天数
     */
    public Integer getPasswordExpireDays() {
        return expire.getDays();
    }

    /**
     * 是否启用密码过期检查
     */
    public Boolean isPasswordExpireCheckEnabled() {
        return expire.getCheck().getEnabled();
    }

    /**
     * 获取密码最小长度
     */
    public Integer getMinPasswordLength() {
        return strength.getMinLength();
    }

    /**
     * 是否要求包含大写字母
     */
    public Boolean isRequireUppercase() {
        return strength.getRequireUppercase();
    }

    /**
     * 是否要求包含小写字母
     */
    public Boolean isRequireLowercase() {
        return strength.getRequireLowercase();
    }

    /**
     * 是否要求包含数字
     */
    public Boolean isRequireDigit() {
        return strength.getRequireDigit();
    }

    /**
     * 是否要求包含特殊字符
     */
    public Boolean isRequireSpecialChar() {
        return strength.getRequireSpecialChar();
    }

    /**
     * 是否检查弱密码
     */
    public Boolean isCheckWeakPassword() {
        return strength.getCheckWeakPassword();
    }

    /**
     * 获取配置摘要信息
     */
    public String getConfigurationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("密码配置摘要: ");
        summary.append("过期天数=").append(expire.getDays()).append("天, ");
        summary.append("过期检查=").append(expire.getCheck().getEnabled() ? "启用" : "禁用").append(", ");
        summary.append("最小长度=").append(strength.getMinLength()).append("位, ");
        summary.append("字符要求=[");
        if (strength.getRequireUppercase()) summary.append("大写字母 ");
        if (strength.getRequireLowercase()) summary.append("小写字母 ");
        if (strength.getRequireDigit()) summary.append("数字 ");
        if (strength.getRequireSpecialChar()) summary.append("特殊字符");
        summary.append("], ");
        summary.append("弱密码检查=").append(strength.getCheckWeakPassword() ? "启用" : "禁用");
        return summary.toString();
    }
} 