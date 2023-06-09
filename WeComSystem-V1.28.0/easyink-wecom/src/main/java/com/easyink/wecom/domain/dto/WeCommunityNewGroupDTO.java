package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 类名： 社群运营 新客自动拉群
 *
 * @author 佚名
 * @date 2021/9/18 14:15
 */
@Data
@ApiModel
public class WeCommunityNewGroupDTO {
    @ApiModelProperty("企业id")
    private String corpId;

    @ApiModelProperty("活码名称")
    @NotNull(message = "活码名不能为空")
    @Size(max = 32, message = "活码名称长度已超出限制")
    private String codeName;

    @ApiModelProperty("指定的员工(id)")
    @NotNull(message = "使用员工不能为空")
    private List<String> emplList;

    @ApiModelProperty("欢迎语")
    @NotNull(message = "欢迎语不能为空")
    @Size(max = 2000, message = "欢迎语长度已超出限制")
    private String welcomeMsg;

    @ApiModelProperty("群活码ID")
    @NotNull(message = "群活码不能为空")
    private Long groupCodeId;

    @ApiModelProperty("标签id列表")
    private List<String> tagList;

    @ApiModelProperty("是否跳过验证自动加好友")
    private Boolean skipVerify = true;

}
