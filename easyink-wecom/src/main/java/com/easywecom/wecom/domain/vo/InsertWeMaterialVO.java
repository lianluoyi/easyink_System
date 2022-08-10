package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 类名：InsertWeMaterialVO
 *
 * @author Society my sister Li
 * @date 2021-10-20 17:16
 */
@Data
@AllArgsConstructor
@ApiModel("新增素材返回实体")
public class InsertWeMaterialVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;
}
