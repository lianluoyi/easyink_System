package com.easywecom.wecom.domain.vo;

import com.easywecom.wecom.domain.WeLeaveUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author admin
 * @Description:
 * @Date: create in 2020/9/21 0021 23:45
 */
@Data
@ApiModel("离职继承实体")
public class WeLeaveUserInfoAllocateVO {
    @ApiModelProperty("原跟进人id列表")
    private List<String> handoverUserids;

    @ApiModelProperty(value = "原跟进人列表")
    @NotEmpty(message = "原跟进人列表不能为空")
    private List<WeLeaveUser> handoverUserList;

    @ApiModelProperty(value = "接替者id")
    @NotBlank(message = "接替者id不能为空")
    private String takeoverUserid;
    /**
     * 公司id
     */
    @NotBlank(message = "公司id不能为空")
    private String corpId;
}
