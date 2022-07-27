package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.enums.GroupMessageType;
import com.easywecom.common.utils.spring.SpringUtils;
import com.easywecom.wecom.domain.vo.radar.WeRadarVO;
import com.easywecom.wecom.service.WeCustomerMessageService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 话术库附件表
 *
 * @author 佚名
 * @date 2021-10-25 17:25:52
 */
@Data
@TableName("we_words_detail")
@ApiModel("话术库附件表实体")
@NoArgsConstructor
public class WeWordsDetailEntity {
    /**
     * 话术库附件ID
     */
    @ApiModelProperty(value = "话术库附件ID")
    @TableField("id")
    private Long id;
    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID")
    @TableField("corp_id")
    private String corpId;
    /**
     * 话术主表ID
     */
    @ApiModelProperty(value = "话术ID更新附件时传参")
    @TableField("group_id")
    private Long groupId;

    @ApiModelProperty(value = "sop规则id 不保存到数据库")
    @TableField(exist = false)
    @JsonIgnore
    private Long ruleId;
    /**
     * 0:海报,1:语音,2:视频,3:普通文件,4:文本,5:图文链接,6:小程序
     */
    @ApiModelProperty(value = "0:海报,1:语音,2:视频,3:普通文件,4:文本,5:图文链接,6:小程序,7:雷达", required = true)
    @TableField("media_type")
    @NotNull
    private Integer mediaType;
    /**
     * 话术详情
     */
    @ApiModelProperty(value = "话术内容")
    @TableField("content")
    private String content;
    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    @TableField("title")
    private String title;
    /**
     * 链接地址
     */
    @ApiModelProperty(value = "链接地址")
    @TableField("url")
    private String url;
    /**
     * 封面
     */
    @ApiModelProperty(value = "封面")
    @TableField("cover_url")
    private String coverUrl;
    /**
     * 链接时使用：0 默认，1 自定义
     */
    @ApiModelProperty(value = "链接时使用：0 默认，1 自定义")
    @TableField("is_defined")
    private Boolean isDefined;

    @ApiModelProperty("视频大小(朋友圈时有保存维护，话术没保存)")
    @TableField("size")
    private Long size;

    @ApiModelProperty("朋友圈临时素材id")
    @TableField(exist = false)
    private String mediaid;

    @ApiModelProperty("雷达id")
    @TableField("radar_id")
    private Long radarId;

    @ApiModelProperty("雷达VO")
    @TableField(exist = false)
    private WeRadarVO radar;

    public WeWordsDetailEntity(String corpId, Integer mediaType, String content) {
        this.corpId = corpId;
        this.mediaType = mediaType;
        this.content = content;
    }

    public void initMediaId(String corpId) {
        WeCustomerMessageService weCustomerMessageService = SpringUtils.getBean(WeCustomerMessageService.class);
        if (GroupMessageType.IMAGE.getType().equals(this.getMediaType().toString())) {
            String mediaId = weCustomerMessageService.buildMediaId(this.getUrl(), GroupMessageType.IMAGE.getMessageType(), this.getTitle(), corpId);
            this.setMediaid(mediaId);
        }
        if (GroupMessageType.VIDEO.getType().equals(this.getMediaType().toString()) && this.getSize() != null && this.getSize() < WeConstans.DEFAULT_MAX_VIDEO_SIZE){
            //获取mediaId
            String mediaId = weCustomerMessageService.buildMediaId(this.getUrl(), GroupMessageType.VIDEO.getMessageType(), this.getTitle(), corpId);
            this.setMediaid(mediaId);
        }
    }
}
