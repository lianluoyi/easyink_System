package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群发消息  群发消息  子消息表(包括 文本消息、图片消息、链接消息、小程序消息)  we_customer_seedMessage
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_customer_seedMessage")
public class WeCustomerSeedMessage extends BaseEntity {
    @ApiModelProperty(value = "微信消息表id")
    @TableField("message_id")
    private Long messageId;

    @ApiModelProperty(value = "消息文本内容，最多4000个字节")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "图片消息：图片的media_id，可以通过 <a href=\"https://work.weixin.qq.com/api/doc/90000/90135/90253\">素材管理接口</a>获得")
    @TableField("media_id")
    private String mediaId;

    @ApiModelProperty(value = "视频名称")
    @TableField("video_name")
    private String videoName;

    @ApiModelProperty(value = "视频url")
    @TableField("video_url")
    private String videoUrl;

    @ApiModelProperty(value = "视频大小")
    @TableField("size")
    private Long size;

    @ApiModelProperty(value = "图片名称")
    @TableField("pic_name")
    private String picName;

    @ApiModelProperty(value = "图片消息：图片的链接，仅可使用<a href=\"https://work.weixin.qq.com/api/doc/90000/90135/90256\">上传图片接口</a>得到的链接")
    @TableField("pic_url")
    private String picUrl;

    @ApiModelProperty(value = "文件名称")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty(value = "文件url")
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty(value = "链接消息：图文消息标题")
    @TableField("link_title")
    private String linkTitle;

    @ApiModelProperty(value = "链接消息：图文消息封面的url")
    @TableField("link_picurl")
    private String linkPicurl;

    @ApiModelProperty(value = "链接消息：图文消息的描述，最多512个字节")
    @TableField("lin_desc")
    private String linDesc;

    @ApiModelProperty(value = "链接消息：图文消息数据来源(0:默认,1:自定义)")
    @TableField("is_defined")
    private Boolean isDefined;

    @ApiModelProperty(value = "链接消息：图文消息的链接")
    @TableField("link_url")
    private String linkUrl;

    @ApiModelProperty(value = "小程序消息标题，最多64个字节")
    @TableField("miniprogram_title")
    private String miniprogramTitle;

    @ApiModelProperty(value = "小程序消息封面的mediaid，封面图建议尺寸为520*416")
    @TableField("miniprogram_media_id")
    private String miniprogramMediaId;

    @ApiModelProperty(value = "小程序appid，必须是关联到企业的小程序应用")
    @TableField("appid")
    private String appid;

    @ApiModelProperty(value = "消息类型 0 图片消息 2视频 3文件 4 文本消息   5 链接消息   6 小程序消息")
    @TableField("message_type")
    private String messageType;

    @ApiModelProperty(value = "小程序page路径")
    @TableField("page")
    private String page;

    @ApiModelProperty(value = "")
    @TableField("del_flag")
    private Integer delFlag;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("seed_message_id")
    private Long seedMessageId;

    @ApiModelProperty(value = "雷达id")
    @TableField("radar_id")
    private Long radarId;

}
