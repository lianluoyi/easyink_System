package com.easyink.wecom.domain.vo.sop;

import com.easyink.wecom.domain.WeOperationsCenterGroupSopFilterEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 类名：FindGroupSopFilterVO
 *
 * @author Society my sister Li
 * @date 2021-12-07 20:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("查询群SOP过滤条件")
public class FindGroupSopFilterVO extends WeOperationsCenterGroupSopFilterEntity {

    @ApiModelProperty(value = "循环SOP的开始时间", required = true)
    private String cycleStart;

    @ApiModelProperty(value = "循环SOP的结束时间", required = true)
    private String cycleEnd;

    @ApiModelProperty(value = "群主信息", required = true)
    private List<BaseGroupSopWeUserVO> ownerList;

    @ApiModelProperty(value = "标签信息", required = true)
    private List<BaseGroupSopTagVO> tagList;


    public FindGroupSopFilterVO(WeOperationsCenterGroupSopFilterEntity groupSopFilterEntity,String cycleStart,String cycleEnd){
        this.setId(groupSopFilterEntity.getId());
        this.setCorpId(groupSopFilterEntity.getCorpId());
        this.setSopId(groupSopFilterEntity.getSopId());
        this.setOwner(groupSopFilterEntity.getOwner());
        this.setTagId(groupSopFilterEntity.getTagId());
        this.setIncludeTagMode(groupSopFilterEntity.getIncludeTagMode());
        this.setCreateTime(groupSopFilterEntity.getCreateTime());
        this.setEndTime(groupSopFilterEntity.getEndTime());
        this.setCycleStart(cycleStart);
        this.setCycleEnd(cycleEnd);
    }
}
