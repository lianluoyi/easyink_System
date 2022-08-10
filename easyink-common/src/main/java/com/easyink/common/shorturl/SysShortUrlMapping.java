package com.easyink.common.shorturl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 类名: 短链长链映射实体
 *
 * @author : silver_chariot
 * @date : 2022/7/18 16:41
 **/
@Data
@ApiModel("长链-短链映射表实体")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysShortUrlMapping {
    @TableField("id")
    @ApiModelProperty(value = "id,短链 ")
    private Long id;

    @TableField("short_code")
    @ApiModelProperty(value = "短链后面的唯一字符串（用于和域名拼接成短链） ")
    private String shortCode;

    @TableField("long_url")
    @ApiModelProperty(value = "原链接（长链接） ")
    private String longUrl;

    @TableField("append_info")
    @ApiModelProperty(value = "附加信息")
    private String appendInfo;

    @TableField("create_time")
    @ApiModelProperty(value = "创建时间 ")
    private Date createTime;

    @TableField("create_by")
    @ApiModelProperty(value = "创建人 ")
    private String createBy;

    /**
     * 设置附加信息
     *
     * @param appendInfo 附加信息 实体{@link ShortUrlAppendInfo}
     */
    public void setAppend(ShortUrlAppendInfo appendInfo) {
        if (appendInfo != null) {
            this.appendInfo = JSON.toJSONString(appendInfo);
        }
    }

    /**
     * 获取附加信息
     *
     * @return 附件信息 {@link ShortUrlAppendInfo }
     */
    public ShortUrlAppendInfo getAppend() {
        if (StringUtils.isBlank(this.appendInfo)) {
            return null;
        }
        return JSON.parseObject(this.appendInfo, ShortUrlAppendInfo.class);
    }


}
