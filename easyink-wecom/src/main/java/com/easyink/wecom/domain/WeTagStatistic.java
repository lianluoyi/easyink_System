package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeTagStatistic weTagStatistic = (WeTagStatistic) o;
        return externalUserid.equals(weTagStatistic.getExternalUserid()) && super.getTagId().equals(weTagStatistic.getTagId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getTagId(), externalUserid);
    }
}
