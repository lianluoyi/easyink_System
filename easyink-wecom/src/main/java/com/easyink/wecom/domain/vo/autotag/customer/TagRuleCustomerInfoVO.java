package com.easyink.wecom.domain.vo.autotag.customer;

import com.easyink.wecom.domain.vo.autotag.TagRuleUserListVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 新客规则详情
 *
 * @author tigger
 * 2022/2/28 15:06
 **/
@Data
public class TagRuleCustomerInfoVO extends TagRuleUserListVO {
    @ApiModelProperty("新客场景列表")
    private List<CustomerSceneVO> customerSceneList;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("生效周期开始时间")
    private Date effectBeginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("生效周期结束时间")
    private Date effectEndTime;
}
