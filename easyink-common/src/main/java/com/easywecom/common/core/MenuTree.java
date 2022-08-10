package com.easywecom.common.core;

import com.easywecom.common.core.domain.TreeSelect;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单树对象实体
 *
 * @author : silver_chariot
 * @date : 2021/8/25 10:50
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuTree {

    @ApiModelProperty(value = "已选择的菜单")
    private List<Integer> checkedKeys;

    @ApiModelProperty(value = "菜单树")
    private List<TreeSelect> menus;
}
