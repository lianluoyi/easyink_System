package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.constant.emple.CustomerAssistantConstants;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获客链接渠道表实体类
 *
 * @author lichaoyu
 * @date 2023/8/22 9:33
 */
@Data
@NoArgsConstructor
@TableName("we_emple_code_channel")
public class WeEmpleCodeChannel extends BaseEntity {

    @ApiModelProperty("主键ID")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();
    @ApiModelProperty("获客链接id")
    @TableField("emple_code_id")
    private Long empleCodeId;
    @ApiModelProperty("自定义渠道名称")
    @TableField("name")
    private String name;
    @ApiModelProperty("自定义渠道的url")
    @TableField("channel_url")
    private String channelUrl;
    @ApiModelProperty("删除状态（默认正常） 0：正常；1：删除")
    @TableField("del_flag")
    private Boolean delFlag = Boolean.FALSE;

    public WeEmpleCodeChannel(String channelUrl, String name, Long empleCodeId) {
        this.channelUrl = channelUrl + CustomerAssistantConstants.STATE_URL + CustomerAssistantConstants.STATE_PREFIX + this.id;
        this.name = name;
        this.empleCodeId = empleCodeId;
    }

    public WeEmpleCodeChannel(String name, Long empleCodeId) {
        this.name = name;
        this.empleCodeId = empleCodeId;
    }

    /**
     * 处理自定义渠道url
     *
     * @param channelUrl 获客链接url
     */
    public void handleChannelUrl(String channelUrl) {
        if (StringUtils.isBlank(channelUrl)) {
            return;
        }
        // 截取掉hk_XXXXXXX部分， https://work.weixin.qq.com/ca/cawcde37b4d932257b?customer_channel=hk_XXXXXXX
        String originUrl = channelUrl.split(CustomerAssistantConstants.STATE_PREFIX)[0];
        this.channelUrl = originUrl + CustomerAssistantConstants.STATE_PREFIX + this.id;
    }
}
