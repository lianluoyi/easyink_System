package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 类名：WeEmplyCodeAnalyseCountVO
 *
 * @author Society my sister Li
 * @date 2021-11-04 16:46
 */
@Data
@ApiModel("员工活码数据统计")
@AllArgsConstructor
@NoArgsConstructor
public class WeEmplyCodeAnalyseVO {

    @ApiModelProperty("每日统计详情")
    private List<WeEmplyCodeAnalyseCountVO> list;

    @ApiModelProperty("总添加人数")
    private Integer total;
}
