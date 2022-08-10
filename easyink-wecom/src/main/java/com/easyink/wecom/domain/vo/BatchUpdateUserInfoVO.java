package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名：BatchUpdateUserInfoVO
 *
 * @author Society my sister Li
 * @date 2021-11-16 17:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("批量操作员工信息返回数据实体")
public class BatchUpdateUserInfoVO {

    @ApiModelProperty("操作成功的数量")
    private Integer successCount;

    @ApiModelProperty("操作失败的数量")
    private Integer failCount;

    @ApiModelProperty("操作失败的原因")
    private String fileUrl;
}
