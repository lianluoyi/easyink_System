package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.enums.WeCategoryMediaTypeEnum;
import com.easyink.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author 佚名
 * @description: 素材分类
 * @date 2021-7-28
 **/

@Data
@TableName("we_category")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("素材分类")
public class WeCategory extends BaseEntity {


    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "授权企业ID", required = true)
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "0海报、1语音、2视频、3普通文件、4文本、5链接、6小程序", allowableValues = "range[0,6]", required = true)
    @TableField("media_type")
    private Integer mediaType;


    @ApiModelProperty(value = "是否启用到侧边栏(0否，1是)")
    @TableField("use_flag")
    private Boolean using;

    @ApiModelProperty(value = "分类名称", required = true)
    @NotBlank(message = "分类名称不能为空")
    @TableField("name")
    @Size(max = 16, message = "分类名称长度已超出限制")
    private String name;

    @ApiModelProperty(value = "0  未删除 1 已删除", allowableValues = "range[0,1]", hidden = true)
    @TableField("del_flag")
    private Integer delFlag;

    public WeCategory(String corpId, WeCategoryMediaTypeEnum typeEnum, String createBy){
        this.id = SnowFlakeUtil.nextId();
        this.corpId = corpId;
        this.mediaType = typeEnum.getMediaType();
        this.name = typeEnum.getName();
        this.setCreateBy(createBy);
        this.setUpdateBy(createBy);
        this.delFlag = WeConstans.WE_CUSTOMER_MSG_RESULT_NO_DEFALE;
        this.using = WeConstans.DEFAULT_WE_MATERIAL_USING;
    }
}
