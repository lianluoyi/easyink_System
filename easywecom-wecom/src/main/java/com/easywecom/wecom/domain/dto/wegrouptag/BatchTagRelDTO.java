package com.easywecom.wecom.domain.dto.wegrouptag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 类名：BatchTagRelDTO
 *
 * @author Society my sister Li
 * @date 2021-11-15 14:35
 */
@Data
@ApiModel("批量打标签")
public class BatchTagRelDTO {

    @ApiModelProperty("客户群chatId集合")
    @Size(min = 1, message = "chatIdList不能为空")
    @NotNull(message = "chatIdList不能为空")
    private List<String> chatIdList;

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty("标签ID集合")
    @Size(min = 1, message = "tagIdList不能为空")
    @NotNull(message = "tagIdList不能为空")
    private List<Long> tagIdList;
}
