package com.easywecom.wecom.domain.dto.transfer;

import com.easywecom.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名: 获取客户分配详情请求参数实体
 *
 * @author : silver_chariot
 * @date : 2021/12/9 9:48
 */
@Data
public class GetResignedTransferDetailDTO extends BaseEntity {

    @ApiModelProperty(value = "接替人id", required = true)
    private String takeOverUserId;

    @ApiModelProperty(value = "原跟进人成员id" ,required = true)
    private String handoverUserId;

    @ApiModelProperty(value = "员工离职时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dimissionTime;

    @ApiModelProperty(value = "企业ID,可不传")
    private String corpId;

    @ApiModelProperty(value = "总分配记录id", required = true)
    private Long recordId;
}
