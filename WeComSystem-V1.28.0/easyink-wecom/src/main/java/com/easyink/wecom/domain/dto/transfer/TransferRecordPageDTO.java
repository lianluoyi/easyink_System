package com.easyink.wecom.domain.dto.transfer;

import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名: 接替记录列表请求实体
 *
 * @author : silver_chariot
 * @date : 2021/12/1 15:19
 */
@Data
@ApiModel("接替记录列表请求参数")
public class TransferRecordPageDTO extends BaseEntity {

    @ApiModelProperty(value = "员工昵称")
    private String customerName;

    @ApiModelProperty(value = "员工姓名")
    private String takeoverUsername;

    @ApiModelProperty(value = "接替状态,多个状态查询用,隔开")
    private String status;

    @ApiModelProperty(value = "公司id,不必传")
    private String corpId;


}
