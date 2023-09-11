package com.easyink.wecom.domain.dto.emplecode;

import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询获客链接DTO
 *
 * @author lichaoyu
 * @date 2023/8/29 10:48
 */
@Data
@ApiModel("查询获客链接DTO")
public class FindAssistantDTO extends BaseEntity {

    @ApiModelProperty("使用员工")
    private String useUserName;

    @ApiModelProperty("活动场景")
    private String scenario;

    @ApiModelProperty(value = "企业ID", hidden = true)
    private String corpId;

    @ApiModelProperty(value = "来源", hidden = true)
    private Integer source;

    @ApiModelProperty("链接url")
    private String qrCode;


    @Override
    public String getEndTime() {
        return DateUtils.parseEndDay(super.getEndTime());
    }

    @Override
    public String getBeginTime() {
        return DateUtils.parseBeginDay(super.getBeginTime());
    }
}
