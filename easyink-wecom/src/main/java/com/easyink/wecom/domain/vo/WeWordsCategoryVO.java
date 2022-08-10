package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：WeWordsCategoryVO
 *
 * @author Society my sister Li
 * @date 2021-10-25 10:42
 */
@Data
@ApiModel("企业话术库文件夹VO")
public class WeWordsCategoryVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("上级文件夹ID")
    private Long parentId;

    @ApiModelProperty("话术类型（0：企业话术，1：部门话术，2：我的话术）")
    private Integer type;

    @ApiModelProperty("使用范围（企业话术：存入根部门1，部门话术：员工主部门id，我的话术：员工id）")
    private String useRange;

    @ApiModelProperty("文件夹名称")
    private String name;

    @ApiModelProperty("文件夹排序")
    private Integer sort;
}
