package com.easywecom.wecom.domain.vo.autotag.record.keyword;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 关键词触发详情VO
 *
 * @author tigger
 * 2022/3/1 17:01
 **/
@Data
public class KeywordRecordDetailVO {
    @ApiModelProperty("关键词")
    private String keyword;
    @ApiModelProperty("触发时间")
    private String createTime;
    @ApiModelProperty("触发文本")
    private String fromText;
}
