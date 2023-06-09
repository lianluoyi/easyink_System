package com.easyink.wecom.domain.vo.sop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：BaseWeOperationsCenterSopVo
 *
 * @author Society my sister Li
 * @date 2021-12-02 09:40
 */
@Data
@ApiModel("sop基本信息")
public class BaseWeOperationsCenterSopVo {

    @ApiModelProperty("sopId")
    private Long id;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String corpId;

    @ApiModelProperty("sop名称")
    private String name;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("sop类型")
    private Integer sopType;

    @ApiModelProperty("过滤条件")
    private Integer filterType;

    @ApiModelProperty("是否开启")
    private Boolean isOpen;

    @ApiModelProperty("创建人userId")
    private String createBy;

    @ApiModelProperty("创建人userName")
    private String createUserName;

    @ApiModelProperty("创建人主部门名称")
    private String mainDepartmentName;

    @ApiModelProperty("使用员工/群聊")
    private List<WeOperationsCenterSopScopeVO> scopeList;
}
