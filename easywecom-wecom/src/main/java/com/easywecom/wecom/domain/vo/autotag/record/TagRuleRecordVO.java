package com.easywecom.wecom.domain.vo.autotag.record;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 标签规则记录VO
 *
 * @author tigger
 * 2022/3/1 16:56
 **/
@Data
public abstract class TagRuleRecordVO {
    @ApiModelProperty("客户id")
    private String customerId;
    @ApiModelProperty("客户名称")
    private String customerName;
    @ApiModelProperty("客户头像")
    private String avatar;
    @ApiModelProperty("员工id")
    private String userId;
    @ApiModelProperty("员工姓名")
    private String userName;
    @ApiModelProperty("外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户")
    private Integer type;
    @ApiModelProperty("客户企业全称")
    private String corpFullName;
    @ApiModelProperty("去重后的标签列表")
    private List<String> tagNameDistinctList;
}
