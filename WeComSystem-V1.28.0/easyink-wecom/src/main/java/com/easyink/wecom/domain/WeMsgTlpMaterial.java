package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.AttachmentTypeEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.entity.form.WeFormMaterial;
import com.easyink.wecom.domain.vo.radar.WeRadarVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;


/**
 * 欢迎语素材表 we_msg_tlp_material
 *
 * @author tigger
 * 2022/1/4 15:46
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("we_msg_tlp_material")
@ApiModel("欢迎语素材表")
public class WeMsgTlpMaterial {

    @ApiModelProperty(value = "主键id", hidden = true)
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "默认欢迎语模板id")
    @TableField("default_msg_id")
    private Long defaultMsgId;

    @ApiModelProperty(value = "特殊规则欢迎语模板id(如果不存在特殊时段欢迎语，且没有素材则该字段为0)")
    @TableField("special_msg_id")
    private Long specialMsgId;

    @ApiModelProperty(value = "素材类型 0:文本 1:图片 2:链接 3:小程序 4:文件 5:视频媒体文件, 7:雷达")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "文本内容,链接消息标题,小程序消息标题，(前端: 图片,文件,视频的标题)")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "图片url,链接封面url,小程序picurl,文件url,视频url")
    @TableField("pic_url")
    private String picUrl;

    @ApiModelProperty(value = "链接消息描述,小程序appid(前端: 文件大小)")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "链接url,小程序page")
    @TableField("url")
    private String url;

    @ApiModelProperty(value = "排序字段")
    @TableField("sort_no")
    private Integer sortNo;

    @ApiModelProperty(value = "其他id, 素材类型为雷达时存储雷达id，为智能表单时为存储表单id")
    @TableField("extra_id")
    private Long extraId;

    @ApiModelProperty(value = "雷达VO")
    @TableField(exist = false)
    private WeRadarVO radar;

    @ApiModelProperty(value = "表单素材")
    @TableField(exist = false)
    private WeFormMaterial form;
    
    public WeMsgTlpMaterial(Integer type, String content) {
        this.type = type;
        this.content = content;
    }

    public WeMsgTlpMaterial(String content, String picUrl, String description, String url) {
        this.content = content;
        this.picUrl = picUrl;
        this.description = description;
        this.url = url;
    }

    /**
     * 校验附件素材类型对应必填字段
     */
    public void checkContent() {
        switch (AttachmentTypeEnum.getByMessageType(this.getType())) {
            case TEXT:
                if (checkNotEmpty(this.content)) {
                    break;
                }
            case IMAGE:
                // 图片url和图片大小
                if (checkNotEmpty(this.picUrl, this.description) && Integer.parseInt(this.description) <= WeConstans.DEFAULT_MAX_IMAGE_SIZE) {
                    break;
                }
            case LINK:
                if (checkNotEmpty(this.content, this.picUrl, this.description, this.url)) {
                    break;
                }
            case FORM:
            case RADAR:
                if (!ObjectUtils.isEmpty(this.extraId)) {
                    break;
                }
            case MINIPROGRAM:
                if (checkNotEmpty(this.content, this.picUrl, this.description, this.url)) {
                    break;
                }
            case FILE:
                // 文件url和文件大小
                if (checkNotEmpty(this.picUrl, this.description) && Integer.parseInt(this.description) <= WeConstans.DEFAULT_MAX_FILE_SIZE) {
                    break;
                }
            case VIDEO:
                // 视频url和视频大小,视频无需限制，超出会转化为link形式
                if (checkNotEmpty(this.picUrl, this.description)) {
                    break;
                }
            default:
                throw new CustomException(ResultTip.TIP_NOT_FIND_ATTACHMENT_TYPE);
        }
    }

    private boolean checkNotEmpty(String... content) {
        return !ObjectUtils.isEmpty(content);
    }




}
