package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名： AllocateWeGroupRESP
 *
 * @author 佚名
 * @date 2021/10/8 19:15
 */
@Data
@Builder
@AllArgsConstructor
@ApiModel("分配单个群聊结果")
public class AllocateWeGroupResp {

    @ApiModelProperty("成功数量")
    private Integer succeedNum;

    @ApiModelProperty("失败数量")
    private Integer failNum;

    @ApiModelProperty("员工客户群今日分配已达上限员工列表")
    private List<String> list;

    @ApiModelProperty("待分配数量")
    private Integer allocateNum;

    public AllocateWeGroupResp() {
        this.succeedNum = 0;
        this.failNum = 0;
        this.list = new ArrayList<>();
    }
}
