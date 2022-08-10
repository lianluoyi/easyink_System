package com.easyink.wecom.domain.vo.autotag.record.group;

import com.easyink.wecom.domain.vo.autotag.record.TagRuleRecordVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 群标签规则记录VO
 *
 * @author tigger
 * 2022/3/1 17:07
 **/
@Data
public class GroupTagRuleRecordVO extends TagRuleRecordVO {

    @ApiModelProperty("进入的客户群")
    private String groupName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("进入群聊时间")
    private Date joinTime;
}
