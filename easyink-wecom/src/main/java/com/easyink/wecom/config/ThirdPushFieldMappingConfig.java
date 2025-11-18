package com.easyink.wecom.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 第三方推送字段映射配置
 *
 * @author easyink
 */
@Data
@Component
@ConfigurationProperties(prefix = "third-push.field-mapping")
public class ThirdPushFieldMappingConfig {

    /**
     * 交互模式字段映射
     */
    private String interactiveMode = "InteractiveMode";
    private String interactiveModeValue = "OB";

    /**
     * 项目名称字段映射
     */
    private String project = "Project";
    private String projectValue = "超高清升级项目企微用户满意度";

    /**
     * FID 时间戳生成
     */
    private String fid = "FID";
    /**
     * fields 字段内容
     */
    private String fields = "Fields";

    /**
     * 手机号字段映射
     */
    private String phone = "DEVICE_1";

    /**
     * 客户昵称字段映射
     */
    private String nickName = "CustomerField2";

    /**
     * 安装地址字段映射
     */
    private String installAddress = "CustomerField3";

    /**
     * 所属外包单位字段映射
     */
    private String outsourcingUnit = "CustomerField4";

    /**
     * 上门员工号字段映射
     */
    private String employeeId = "CustomerField5";

    /**
     * 是否满意字段映射 （1：非常满意；2：满意；3：不满意）
     */
    private String satisfaction = "CustomerField6";

    /**
     * 服务不满意内容字段映射
     */
    private String dissatisfactionContent = "CustomerField7";

    /**
     * 服务建议内容字段映射
     */
    private String serviceSuggestion = "CustomerField8";
}
