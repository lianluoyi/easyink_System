package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 类名： sop待办任务素材表
 *
 * @author 佚名
 * @date 2021-12-07 18:30:22
 */
@Data
@TableName("we_operations_center_sop_task")
@ApiModel("sop待办任务素材表实体")
public class WeOperationsCenterSopTaskEntity {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableField("id")
    private Long id;
    /**
     * 企业id
     */
    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    private String corpId;
    /**
     * 0:海报,1:语音,2:视频,3:普通文件,4:文本,5:图文链接,6:小程序
     */
    @ApiModelProperty(value = "0:海报,1:语音,2:视频,3:普通文件,4:文本,5:图文链接,6:小程序")
    @TableField("media_type")
    private Integer mediaType;
    /**
     * 内容详情
     */
    @ApiModelProperty(value = "内容详情")
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
    private Integer isDefined;

}
