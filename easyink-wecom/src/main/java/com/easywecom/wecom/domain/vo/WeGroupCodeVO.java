package com.easywecom.wecom.domain.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群活码简略信息
 *
 * @author admin
 * @Date 2021/3/26 14:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("群活码简略信息")
public class WeGroupCodeVO {

    @ApiModelProperty("群活码ID")
    private Long id;

    @ApiModelProperty("群活码链接")
    private String codeUrl;

    @ApiModelProperty("群活码名")
    private String activityName;

    @ApiModelProperty("群活码描述")
    private String activityDesc;

    @ApiModelProperty("创建类型 1:群二维码 2: 企微活码")
    private Integer createType;
}