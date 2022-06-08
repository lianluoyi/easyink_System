package com.easywecom.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： SopAttachmentVO
 *
 * @author 佚名
 * @date 2021/12/3 9:58
 */
@Data
@ApiModel("sop规则附件")
public class SopAttachmentVO {

    @ApiModelProperty(value = "话术库附件ID")
    private Long id;
    @ApiModelProperty(value = "0:海报,1:语音,2:视频,3:普通文件,4:文本,5:图文链接,6:小程序")
    private Integer mediaType;

    @ApiModelProperty(value = "话术内容")
    private String content;
    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;
    /**
     * 链接地址
     */
    @ApiModelProperty(value = "链接地址")
    private String url;
    /**
     * 封面
     */
    @ApiModelProperty(value = "封面")
    private String coverUrl;
    /**
     * 链接时使用：0 默认，1 自定义
     */
    @ApiModelProperty(value = "链接时使用：0 默认，1 自定义")
    private Boolean isDefined;
}
