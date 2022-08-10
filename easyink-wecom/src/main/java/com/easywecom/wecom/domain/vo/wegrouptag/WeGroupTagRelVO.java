package com.easywecom.wecom.domain.vo.wegrouptag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：WeGroupTagRelVO
 *
 * @author Society my sister Li
 * @date 2021-11-15 15:59
 */
@Data
@ApiModel("客户群与标签数据")
public class WeGroupTagRelVO {

    @ApiModelProperty("客户群ID")
    private String chatId;

    @ApiModelProperty("标签数据")
    private List<WeGroupTagRelDetail> tagList;


}
