package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 聊天侧边栏展示素材类型
 *
 * @author Society my sister Li
 * @date 2021-10-14 10:43
 */
@Data
@ApiModel("聊天侧边栏实体")
public class WeCategoryBaseInfoVO {

    @ApiModelProperty(value = "素材类型主键ID")
    private Long id;

    @ApiModelProperty(value = "素材类型")
    private Integer mediaType;

    @ApiModelProperty(value = "素材分类名称")
    private String name;

    @ApiModelProperty(value = "是否发布到侧边栏")
    private Boolean using;
}
