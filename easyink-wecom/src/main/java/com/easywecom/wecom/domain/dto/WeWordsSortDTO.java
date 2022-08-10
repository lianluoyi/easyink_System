package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： 话术库排序DTO
 *
 * @author 佚名
 * @date 2021/11/4 20:59
 */
@ApiModel("话术库排序DTO")
@Data
public class WeWordsSortDTO {
    @ApiModelProperty(value = "话术ID及新的sort", required = true)
    private List<WeWordsChangeSortDTO> wordsChangeSortDTOList;

    @ApiModelProperty(hidden = true)
    private String corpId;

}
