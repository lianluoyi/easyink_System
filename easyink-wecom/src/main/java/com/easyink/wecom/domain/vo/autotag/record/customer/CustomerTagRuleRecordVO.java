package com.easyink.wecom.domain.vo.autotag.record.customer;

import com.easyink.wecom.domain.vo.autotag.record.TagRuleRecordVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 新客标签规则记录VO
 *
 * @author tigger
 * 2022/3/1 17:12
 **/
@Data
public class CustomerTagRuleRecordVO extends TagRuleRecordVO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("添加时间")
    private Date addTime;

}
