package com.easywecom.wecom.domain.vo.autotag;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 规则列表VO
 *
 * @author tigger
 * 2022/2/28 14:44
 **/
@Data
public class TagRuleListVO {
    @ApiModelProperty("规则id")
    private Long id;
    @ApiModelProperty("规则名称")
    private String ruleName;
    @ApiModelProperty("标签名字列表")
    private List<TagInfoVO> tagList;
    @ApiModelProperty("启用禁用状态 0:禁用1:启用")
    private Boolean status;
    @ApiModelProperty("创建人")
    private String createBy;
    @ApiModelProperty("所属部门")
    private String mainDepartmentName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("创建时间")
    private Date createTime;
    private String corpId;

}
