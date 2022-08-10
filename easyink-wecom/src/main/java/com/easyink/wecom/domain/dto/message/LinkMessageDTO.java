package com.easyink.wecom.domain.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名： 链接DTO 《LinkMessageDTO》
 *
 * @author 佚名
 * @date 2022/1/6 14:36
 */

@Data
@ApiModel("链接DTO 《LinkMessageDTO》")
@NoArgsConstructor
public class LinkMessageDTO {

    /**
     * 图文消息标题
     */
    @ApiModelProperty("图文消息标题")
    private String title;

    /**
     * 图文消息封面的url
     */
    @ApiModelProperty("图文消息封面的url")
    private String picurl;

    /**
     * 图文消息的描述，最多512个字节
     */
    @ApiModelProperty("图文消息的描述，最多512个字节")
    private String desc;

    /**
     * 图文消息的链接
     */
    @ApiModelProperty("图文消息的链接")
    private String url;

    @ApiModelProperty("图片封面mediaId 可通过上传附件资源接口获得")
    private String media_id;

    @ApiModelProperty("链接消息：图文消息数据来源(0:默认,1:自定义)")
    private Boolean isDefined;

    public LinkMessageDTO(String mediaId, String title, String url, String content) {
        this.media_id = mediaId;
        this.title = title;
        this.desc = content;
        this.url = url;
    }
}
