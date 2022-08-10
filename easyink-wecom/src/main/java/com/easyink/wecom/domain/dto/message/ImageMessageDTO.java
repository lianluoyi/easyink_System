package com.easyink.wecom.domain.dto.message;

import com.easyink.wecom.domain.dto.common.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名： 图片海报消息DTO 《ImageMessageDTO》
 *
 * @author 佚名
 * @date 2022/1/14 13:59
 */
@Data
@ApiModel("图片海报消息DTO 《ImageMessageDTO》")
@NoArgsConstructor
public class ImageMessageDTO {

    @ApiModelProperty(value = "图片的media_id，可以通过 <a href=\"https://work.weixin.qq.com/api/doc/90000/90135/90253\">素材管理接口</a>获得", hidden = true)
    private String media_id;

    @ApiModelProperty("图片的链接，仅可使用<a href=\"https://work.weixin.qq.com/api/doc/90000/90135/90256\">上传图片接口</a>得到的链接")
    private String pic_url;

    @ApiModelProperty("图片标题")
    private String title;

    public ImageMessageDTO(String mediaId, String title) {
        this.title = title;
        this.media_id = mediaId;
    }

    public Image toImage() {
        return new Image(this.media_id, this.pic_url);
    }
}
