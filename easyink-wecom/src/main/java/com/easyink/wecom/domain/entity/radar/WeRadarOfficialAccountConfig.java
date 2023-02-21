package com.easyink.wecom.domain.entity.radar;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * 雷达公众号配置
 * @author wx
 * @TableName we_radar_official_account_config
 * @date 2023/1/11 19:46
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="we_radar_official_account_config")
@Data
@NoArgsConstructor
public class WeRadarOfficialAccountConfig extends BaseEntity {
    /**
     * 企业id
     */
    @TableField(value = "corp_id")
    private String corpId;

    /**
     * 公众号appid
     */
    @TableField(value = "app_id")
    private String appId;

}