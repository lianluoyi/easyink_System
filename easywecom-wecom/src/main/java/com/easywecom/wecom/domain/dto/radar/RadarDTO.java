package com.easywecom.wecom.domain.dto.radar;

import cn.hutool.core.util.ObjectUtil;
import com.easywecom.common.core.domain.BaseEntity;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.wecom.domain.entity.radar.WeRadar;
import com.easywecom.wecom.domain.entity.radar.WeRadarTag;
import com.easywecom.wecom.domain.entity.radar.WeRadarUrl;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ClassName： AddRadarDTO
 *
 * @author wx
 * @date 2022/7/18 15:27
 */
@Data
@ApiModel("雷达DTO")
public class RadarDTO {

    @ApiModelProperty(value = "雷达id")
    private Long id;

    @ApiModelProperty(value = "雷达类型（1个人雷达，2部门雷达，3企业雷达）")
    private Integer type;

    @ApiModelProperty(value = "corpId")
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "雷达标题")
    @NotBlank(message = "雷达标题不得为空")
    @Size(max = 32, message = "标题长度超过限制,最多32个字符")
    private String radarTitle;

    @ApiModelProperty("雷达链接实体")
    @Valid
    @NotNull(message = "雷达链接不能为空")
    private WeRadarUrl weRadarUrl;

    @ApiModelProperty(value = "是否开启行为通知（1[true]是0[false]否）")
    private Boolean enableClickNotice;

    @ApiModelProperty(value = "是否允许轨迹记录（1[true]是 0[false]否)")
    private Boolean enableBehaviorRecord;

    @ApiModelProperty(value = "是否允许打上客户标签（ 1[true]是 0[false]否)")
    private Boolean enableCustomerTag;

    @ApiModelProperty(value = "更新后是否通知员工（true[1]是 false[0]否)")
    private Boolean enableUpdateNotice;

    @ApiModelProperty(value = "雷达标签")
    private List<WeRadarTag> radarTagList;

    @ApiModelProperty(value = "员工主部门")
    @JsonIgnore
    private Long mainDepartment;

    public WeRadar buildRadarData() {
        WeRadar radar = new WeRadar();
        BeanUtils.copyProperties(this, radar, "id");
        final WeRadarUrl weRadarUrl = this.getWeRadarUrl();
        radar.setUrl(weRadarUrl.getUrl());
        radar.setIsDefined(weRadarUrl.getIsDefined());
        radar.setCoverUrl(ObjectUtil.isNotEmpty(weRadarUrl.getCoverUrl()) ? weRadarUrl.getCoverUrl() : StringUtils.EMPTY);
        radar.setTitle(ObjectUtil.isNotEmpty(weRadarUrl.getTitle()) ? weRadarUrl.getTitle() : StringUtils.EMPTY);
        radar.setContent(ObjectUtil.isNotEmpty(weRadarUrl.getContent()) ? weRadarUrl.getContent() : StringUtils.EMPTY);
        return radar;
    }

    public List<WeRadarTag> buildRadarTags(Long radarId) {
        if (Boolean.TRUE.equals(this.getEnableCustomerTag())) {
            this.getRadarTagList().forEach(item -> item.setRadarId(radarId));
            return this.getRadarTagList();
        }
        return Collections.emptyList();
    }

}
