package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.enums.WeTempMaterialEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author 佚名
 * @description: 企业微信上传临时素材实体
 * @date 2021-7-29
 **/
@ApiModel("素材实体《WeMaterial》")
@Data
@TableName("we_material")
public class WeMaterial extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "分类id", required = true)
    @TableField("category_id")
    @NotNull(message = "categoryId不能为空")
    private Long categoryId;

    @ApiModelProperty(value = "本地资源文件地址", required = true)
    @TableField("material_url")
    @NotBlank(message = "materialUrl不能为空")
    private String materialUrl;

    @ApiModelProperty(value = "文本内容、图片文案")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "图片名称", required = true)
    @TableField("material_name")
    @Size(max = 32, message = "名称长度已超出限制")
    @NotBlank(message = "materialName不能为空")
    private String materialName;

    @ApiModelProperty(value = "摘要")
    @TableField("digest")
    @Size(max = 64, message = "摘要长度已超出限制")
    private String digest;

    @ApiModelProperty(value = "封面本地资源文件")
    @TableField("cover_url")
    private String coverUrl;

    @ApiModelProperty(value = "音频时长")
    @TableField("audio_time")
    private String audioTime;

    @ApiModelProperty(value = "过期时间")
    @TableField("expire_time")
    private String expireTime;

    @ApiModelProperty(value = "是否发布到侧边栏（0否，1是）")
    @TableField("show_material")
    private Boolean showMaterial;

    @ApiModelProperty(value = "是否为临时素材")
    @TableField("temp_flag")
    private Integer tempFlag = WeTempMaterialEnum.MATERIAL.getTempFlag();

    @ApiModelProperty(value = "链接时使用：0 默认，1 自定义")
    @TableField("is_defined")
    private Boolean isDefined;

    @ApiModelProperty(value = "链接时使用(0,不转化为雷达，1：转化为雷达)")
    @TableField("enable_convert_radar")
    private Boolean enableConvertRadar;

    @ApiModelProperty(value = "保存雷达时使用，使用员工活码，新客进群时")
    @TableField("radar_id")
    private Long radarId;
}
