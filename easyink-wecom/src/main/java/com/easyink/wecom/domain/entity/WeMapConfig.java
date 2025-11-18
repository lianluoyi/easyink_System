package com.easyink.wecom.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.annotation.EncryptField;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.StringUtils;

/**
 * 地图API配置实体
 *
 * @author wx
 * @date 2023/8/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("we_map_config")
@ApiModel("地图API配置实体")
public class WeMapConfig extends BaseEntity {

    @ApiModelProperty(value = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "企业ID，0表示系统默认配置")
    private String corpId;

    @ApiModelProperty(value = "后端API密钥")
    @EncryptField(EncryptField.FieldType.COMMON)
    private String apiKey;
    private String apiKeyEncrypt;

    @ApiModelProperty(value = "iframe API密钥")
    @EncryptField(EncryptField.FieldType.COMMON)
    private String iframeApiKey;
    private String iframeApiKeyEncrypt;

    @ApiModelProperty(value = "地图类型：1-腾讯地图, 2-高德地图, 3-百度地图")
    private Integer mapType;

    @ApiModelProperty(value = "接口调用限制信息列表，包含接口编码和调用限制次数")
    private String dailyLimit;

    @ApiModelProperty(value = "状态 0：停用 1：启用")
    private Integer status;

    /**
     * 是否企业配置
     *
     * @return
     */
    public boolean isCorpConfig() {
        return StringUtils.isNotBlank(corpId);
    }
}