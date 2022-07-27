package com.easywecom.wecom.domain.vo.radar;

import com.easywecom.wecom.domain.entity.radar.WeRadarTag;
import com.easywecom.wecom.domain.entity.radar.WeRadarUrl;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClassName： WeRadarVO
 *
 * @author wx
 * @date 2022/7/18 17:13
 */
@Data
@ApiModel("雷达VO")
@NoArgsConstructor
@AllArgsConstructor
public class WeRadarVO {

    @ApiModelProperty(value = "雷达id")
    private Long radarId;

    @ApiModelProperty(value = "雷达标题")
    private String radarTitle;

    @ApiModelProperty(value = "雷达类型")
    private Integer type;

    @ApiModelProperty("雷达内容")
    private WeRadarUrl weRadarUrl;

    @ApiModelProperty("客户标签")
    private List<WeRadarTag> radarTagList;

    @ApiModelProperty("总点击人数")
    private Integer clickNum;

    @ApiModelProperty("创建人id")
    private String createId;

    @ApiModelProperty("创建人姓名")
    private String createName;

    @ApiModelProperty("创建人所属部门id")
    private String departmentId;

    @ApiModelProperty("创建人所属部门姓名")
    private String departmentName;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty(value = "是否开启行为通知（1[true]是0[false]否）")
    private Boolean enableClickNotice;

    @ApiModelProperty(value = "是否允许轨迹记录（1[true]是 0[false]否)")
    private Boolean enableBehaviorRecord;

    @ApiModelProperty(value = "是否允许打上客户标签（ 1[true]是 0[false]否)")
    private Boolean enableCustomerTag;

    @ApiModelProperty(value = "更新后是否通知员工（true[1]是 false[0]否)")
    private Boolean enableUpdateNotice;
}
