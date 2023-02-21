package com.easyink.wecom.domain.dto.statistics;

import com.easyink.common.core.domain.RootEntity;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.bean.BeanUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 客户好评率查询DTO
 *
 * @author wx
 * 2023/2/17 0:19
 **/
@Data
public class GoodCommitDTO extends RootEntity {
    @ApiModelProperty("客户好评率排序 正序 ASC 倒叙 DESC 不需要排序 null")
    private String customerPositiveCommentsRateSort;

    @ApiModelProperty("员工idList")
    private List<String> userIds;

    @ApiModelProperty("开始时间/添加时间的开始时间")
    private String beginTime;

    @ApiModelProperty("结束时间/添加时间的结束时间")
    private String endTime;

    public GoodCommitDTO(StatisticsDTO dto) {
        BeanUtils.copyProperties(dto, this);
        this.setBeginTime(DateUtils.time2Date(dto.getBeginTime()));
        this.setEndTime(DateUtils.time2Date(dto.getEndTime()));
    }

    public String getBeginTime() {
        return DateUtils.parseBeginDay(beginTime);
    }

    public String getEndTime() {
        return DateUtils.parseEndDay(endTime);
    }

    /**
     * 企业id
     */
    private String corpId;

}
