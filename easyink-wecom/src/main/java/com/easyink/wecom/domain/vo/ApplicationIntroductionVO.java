package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 类名: ApplicationIntroductionVO
 *
 * @author: 1*+
 * @date: 2021-10-15 15:37
 */
@Data
@Builder
@ApiModel("应用简介实体")
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationIntroductionVO {

    @ApiModelProperty(value = "应用ID")
    private Integer appid;

    @ApiModelProperty(value = "应用名")
    private String name;

    @ApiModelProperty(value = "应用描述")
    private String description;

    @ApiModelProperty(value = "应用头像")
    private String logoUrl;

    @ApiModelProperty(value = "上架时间")
    private Date createTime;

    /**
     * 开发类型（1自研，2三方）
     */
    @ApiModelProperty(value = "开发类型（1自研，2三方）")
    private Integer developmentType;
    /**
     * 侧边栏url:自研存储相对路径，三方开发存储完整url
     */
    @ApiModelProperty(value = "侧边栏url:自研存储相对路径，三方开发存储完整url")
    private String sidebarRedirectUrl;
    /**
     * 应用入口url
     */
    @ApiModelProperty(value = "应用入口url")
    private String applicationEntranceUrl;
}
