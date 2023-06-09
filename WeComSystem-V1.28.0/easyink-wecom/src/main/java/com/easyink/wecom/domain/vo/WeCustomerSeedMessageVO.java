package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： WeCustomerSeedMessageVO
 *
 * @author 佚名
 * @date 2021/10/15 16:42
 */
@ApiModel("子消息体VO WeCustomerSeedMessageVO")
@Data
public class WeCustomerSeedMessageVO {
    @ApiModelProperty(value = "微信消息表id")
    private Long messageId;

    @ApiModelProperty(value = "消息文本内容，最多4000个字节")
    private String content;

    @ApiModelProperty(value = "图片消息：图片的media_id，可以通过 <a href=\"https://work.weixin.qq.com/api/doc/90000/90135/90253\">素材管理接口</a>获得")
    private String mediaId;

    @ApiModelProperty(value = "视频名称")
    private String videoName;

    @ApiModelProperty(value = "视频url")
    private String videoUrl;

    @ApiModelProperty(value = "图片名称")
    private String picName;

    @ApiModelProperty(value = "图片消息：图片的链接，仅可使用<a href=\"https://work.weixin.qq.com/api/doc/90000/90135/90256\">上传图片接口</a>得到的链接")
    private String picUrl;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件url")
    private String fileUrl;

    @ApiModelProperty(value = "链接消息：图文消息标题, 存储雷达表单标题，用户群发详情显示")
    private String linkTitle;

    @ApiModelProperty(value = "链接消息：图文消息封面的url")
    private String linkPicurl;

    @ApiModelProperty(value = "链接消息：图文消息的描述，最多512个字节")
    private String linDesc;

    @ApiModelProperty(value = "链接消息：图文消息数据来源(0:默认,1:自定义)")
    private String isDefined;

    @ApiModelProperty(value = "链接消息：图文消息的链接")
    private String linkUrl;

    @ApiModelProperty(value = "小程序消息标题，最多64个字节")
    private String miniprogramTitle;

    @ApiModelProperty(value = "小程序消息封面的mediaid，封面图建议尺寸为520*416")
    private String miniprogramMediaId;

    @ApiModelProperty(value = "小程序appid，必须是关联到企业的小程序应用")
    private String appid;

    @ApiModelProperty(value = "消息类型 0 图片消息 2视频 3文件 4 文本消息   5 链接消息   6 小程序消息")
    private String messageType;

    @ApiModelProperty(value = "小程序page路径")
    private String page;

    @ApiModelProperty(value = "主键id")
    private Long seedMessageId;
}
