package com.easyink.wecom.domain.vo.customerloss;

import com.easyink.wecom.domain.WeTag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 客户继承 流失提醒标签
 * 类名：CustomerLossTagVO
 *
 * @author lichaoyu
 * @date 2023/3/24 10:01
 */
@Data
public class CustomerLossTagVO {

    @ApiModelProperty("流失标签列表")
    private List<WeTag> weTags;
}
