package com.easyink.wecom.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.WeLockSidebarConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 络客侧边栏配置返回VO
 *
 * @author wx
 * 2023/3/21 17:35
 **/
@Data
public class WeLockSidebarConfigVO {

    @ApiModelProperty("络客app_id")
    @TableField("app_id")
    private String appId;

    @ApiModelProperty("络客app_id")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("络客app_secret")
    @TableField("app_secret")
    private String appSecret;

    /**
     * 构造方法
     *
     * @param config    {@link WeLockSidebarConfig}
     */
    public WeLockSidebarConfigVO(WeLockSidebarConfig config) {
        if (config == null) {
            return;
        }
        BeanUtils.copyProperties(config, this);
    }
}
