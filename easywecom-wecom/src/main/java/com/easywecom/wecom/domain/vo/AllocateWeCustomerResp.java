package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 类名： AllocateWeCustomerRESP
 *
 * @author 佚名
 * @date 2021/10/8 19:16
 */
@Data
@Builder
@AllArgsConstructor
@ApiModel("分配群聊结果")
public class AllocateWeCustomerResp {
    /**
     * 成功数量
     */
    @ApiModelProperty("成功数量")
    private Integer succeedNum;
    /**
     * 失败数量
     */
    @ApiModelProperty("失败数量")
    private Integer failNum;


    @ApiModelProperty("待分配数量")
    private Integer allocateNum;

    public AllocateWeCustomerResp() {
        this.succeedNum = 0;
        this.failNum = 0;
    }
}
