package com.easyink.wecom.domain.dto.moment;

import com.easyink.common.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 类名： SearchMomentContentDTO
 *
 * @author 佚名
 * @date 2022/1/12 22:13
 */
@Data
@ApiModel("朋友圈列表DTO")
public class SearchMomentContentDTO {
    @ApiModelProperty("朋友圈内容")
    private String content;

    @ApiModelProperty("发布类型（-1全部 0：企业 1：个人）")
    @Max(1)
    @Min(-1)
    private Integer type;

    @ApiModelProperty("开始时间")
    private String beginTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty(value = "企业id", hidden = true)
    @JsonIgnore
    private String corpId;
    @ApiModelProperty(value = "页码", required = true)
    @NotNull(message = "页码不能为空")
    private Integer pageNum;

    @ApiModelProperty(value = "页面条数", required = true)
    @NotNull(message = "页数大小不能为空")
    private Integer pageSize;

    public SearchMomentContentDTO initTime() {
        this.setBeginTime(DateUtils.parseBeginDay(this.getBeginTime()));
        this.setEndTime(DateUtils.parseEndDay(this.getEndTime()));
        return this;
    }
}
