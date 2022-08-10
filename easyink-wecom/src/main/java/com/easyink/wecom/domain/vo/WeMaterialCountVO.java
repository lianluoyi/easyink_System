package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 素材数量统计VO
 *
 * @author 佚名
 * @date 2021/10/14 16:14
 */
@Data
@ApiModel("素材数量统计VO 《WeMaterialCountVO》")
public class WeMaterialCountVO {

    @ApiModelProperty("素材数量")
    Integer materialNum;

    @ApiModelProperty("发布到侧边栏数量")
    Integer materialSideBarNum;
}
