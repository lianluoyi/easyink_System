package com.easyink.wecom.domain.dto.autotag.batchtag;

import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询和导出批量打标签详情DTO
 *
 * @author lichaoyu
 * @date 2023/6/6 9:07
 */
@ApiModel("查询和导出批量打标签详情DTO")
@Data
public class BatchTagTaskDetailDTO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "企业ID")
    private String corpId;

    @ApiModelProperty(value = "批量打标签任务ID")
    private String taskId;

    @ApiModelProperty(value = "批量打标签任务名称,用于导出文件命名")
    private String taskName;

    @ApiModelProperty(value = "客户信息 可以是external_userid、unionid、手机号")
    private String customerInfo;
    private String customerInfoEncrypt;

    @ApiModelProperty(value = "打标签状态（0 待执行， 1 成功 ， 2 失败）")
    private Integer status;


}
