package com.easyink.wecom.utils;

import com.easyink.common.exception.CustomException;
import com.easyink.framework.config.PasswordConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 密码工具类，用于密码相关的校验和处理
 * 使用PasswordConfigurationProperties进行配置管理
 */
@Slf4j
@Component
public class PasswordUtils {

    @Autowired
    private PasswordConfigurationProperties passwordConfig;

    /**
     * 常见弱密码列表
     */
    private static final List<String> WEAK_PASSWORDS = Arrays.asList(
            "123456", "password", "12345678", "qwerty", "111111", "123123",
            "admin123", "admin", "administrator", "root123", "root", "test123", "test"
    );

    /**
     * 密码强度正则表达式（包含大写字母、小写字母、数字、特殊字符）
     */
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    /**
     * 校验密码是否符合强度要求
     *
     * @param password 密码
     * @return 校验结果描述
     */
    public String validatePasswordStrength(String password) {
        Integer minPasswordLength = passwordConfig.getMinPasswordLength();
        Boolean checkWeakPassword = passwordConfig.isCheckWeakPassword();
        Boolean requireDigit = passwordConfig.isRequireDigit();
        Boolean requireLowercase = passwordConfig.isRequireLowercase();
        Boolean requireUppercase = passwordConfig.isRequireUppercase();
        Boolean requireSpecialChar = passwordConfig.isRequireSpecialChar();

        if (password == null || password.length() < minPasswordLength) {
            return "密码长度不能少于" + minPasswordLength + "位";
        }

        if (checkWeakPassword && WEAK_PASSWORDS.contains(password.toLowerCase())) {
            return "密码过于简单，请避免使用常见弱密码";
        }

        // 构建动态正则表达式
        StringBuilder regexBuilder = new StringBuilder("^");
        
        if (requireDigit) {
            regexBuilder.append("(?=.*[0-9])");
        }
        if (requireLowercase) {
            regexBuilder.append("(?=.*[a-z])");
        }
        if (requireUppercase) {
            regexBuilder.append("(?=.*[A-Z])");
        }
        if (requireSpecialChar) {
            regexBuilder.append("(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])");
        }
        
        regexBuilder.append(".{").append(minPasswordLength).append(",}$");
        
        Pattern dynamicPattern = Pattern.compile(regexBuilder.toString());
        
        if (!dynamicPattern.matcher(password).matches()) {
            StringBuilder message = new StringBuilder("密码必须包含");
            if (requireDigit) message.append("数字、");
            if (requireLowercase) message.append("小写字母、");
            if (requireUppercase) message.append("大写字母、");
            if (requireSpecialChar) message.append("特殊字符、");
            message.setLength(message.length() - 1); // 移除最后一个逗号
            message.append("，且长度不少于").append(minPasswordLength).append("位");
            return message.toString();
        }

        return null; // 返回null表示密码符合要求
    }

    /**
     * 检查新密码是否与旧密码相同
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void checkPasswordNotSame(String oldPassword, String newPassword) {
        if (oldPassword.equals(newPassword)) {
            throw new CustomException("新密码不能与当前密码相同");
        }
    }

    /**
     * 检查密码是否过期
     *
     * @param lastPasswordChangeTime 上次修改密码时间
     * @return 是否过期
     */
    public boolean isPasswordExpired(LocalDateTime lastPasswordChangeTime) {
        Boolean passwordExpireCheckEnabled = passwordConfig.isPasswordExpireCheckEnabled();
        Integer passwordExpireDays = passwordConfig.getPasswordExpireDays();
        
        if (!passwordExpireCheckEnabled || lastPasswordChangeTime == null) {
            return false;
        }

        LocalDateTime expirationDate = lastPasswordChangeTime.plusDays(passwordExpireDays);
        return LocalDateTime.now().isAfter(expirationDate);
    }

    /**
     * 获取密码过期天数
     *
     * @return 过期天数
     */
    public Integer getPasswordExpireDays() {
        return passwordConfig.getPasswordExpireDays();
    }

    /**
     * 是否启用密码过期检查
     *
     * @return 是否启用
     */
    public Boolean isPasswordExpireCheckEnabled() {
        return passwordConfig.isPasswordExpireCheckEnabled();
    }

    /**
     * 获取密码最小长度
     *
     * @return 最小长度
     */
    public Integer getMinPasswordLength() {
        return passwordConfig.getMinPasswordLength();
    }

    /**
     * 获取密码配置摘要信息
     *
     * @return 配置摘要
     */
    public String getConfigurationSummary() {
        return passwordConfig.getConfigurationSummary();
    }

    /**
     * 获取完整的密码配置信息
     *
     * @return 密码配置对象
     */
    public PasswordConfigurationProperties getPasswordConfig() {
        return passwordConfig;
    }
}