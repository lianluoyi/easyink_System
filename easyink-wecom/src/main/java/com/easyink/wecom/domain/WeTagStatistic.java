package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据统计标签信息实体类
 *
 * @author lichaoyu
 * @date 2023/5/30 11:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeTagStatistic extends WeTag {
    @ApiModelProperty(value = "外部联系人的userid")
    @TableField("external_userid")
    private String externalUserid;

}
