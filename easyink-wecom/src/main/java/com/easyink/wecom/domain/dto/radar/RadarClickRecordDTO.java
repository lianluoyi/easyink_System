package com.easyink.wecom.domain.dto.radar;


import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.entity.radar.WeRadarClickRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName： RadarClickRecordDTO
 *
 * @author wx
 * @date 2022/7/20 9:18
 */
@Data
@ApiModel("雷达点击记录DTO")
public class RadarClickRecordDTO {

    @ApiModelProperty("雷达点击记录表ID，主键id")
    private Long id;

    @ApiModelProperty(value = "雷达id")
    private String radarId;

    @ApiModelProperty(value = "发送活码用户id")
    private String userId;

    @ApiModelProperty(value = "发送雷达链接的用户名称")
    private String userName;

    @ApiModelProperty(value = "客户id")
    private String externalUserId;

    @ApiModelProperty(value = "用户头像")
    private String headImageUrl;

    @ApiModelProperty(value = "客户名称")
    private String externalUserName;

    @ApiModelProperty(value = "渠道类型（（0未知渠道,1员工活码，2朋友圈，3群发，4侧边栏,5欢迎语,6 客户SOP,7群SOP，8新客进群，9群日历，10自定义渠道）")
    private Integer channelType;

    @ApiModelProperty(value = "未知渠道 COMMENT 渠道名")
    private String channelName;

    @ApiModelProperty(value = "外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来")
    private String unionId;

    @ApiModelProperty(value = "公众号/小程序open_id")
    private String openId;

    /**
     * 构造雷达点击记录实体
     *
     * @return
     */
    public WeRadarClickRecord buildData() {
        WeRadarClickRecord radarClickRecord = new WeRadarClickRecord();
        BeanUtils.copyProperties(this, radarClickRecord, "id");
        return radarClickRecord;
    }
}
