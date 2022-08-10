package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 类名： WeEmpleCodeMaterial
 *
 * @author 李小琳
 * @date 2021/9/30 15:59
 */
@Data
@TableName("we_emple_code_material")
@ApiModel("员工活码附件表")
@AllArgsConstructor
@NoArgsConstructor
public class WeEmpleCodeMaterial {

    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "员工活码ID")
    @TableField("emple_code_id")
    private Long empleCodeId;

    @ApiModelProperty(value = "素材ID", required = true)
    @TableField("media_id")
    private Long mediaId;

    @ApiModelProperty(value = "'素材类型（-1：群活码、0：图片、1：语音、2：视频，3：文件、4：文本、5：图文链接、6：小程序）", required = true)
    @TableField("media_type")
    private Integer mediaType;


    public WeEmpleCodeMaterial(Long empleCodeId, Long mediaId, Integer mediaType) {
        this.empleCodeId = empleCodeId;
        this.mediaId = mediaId;
        this.mediaType = mediaType;
    }
}
