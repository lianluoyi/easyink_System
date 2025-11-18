package com.easyink.wecom.domain.model.mapapi;

import com.alibaba.fastjson.JSONObject;
import com.easyink.wecom.domain.entity.WeMapConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tigger
 * 2025/5/8 23:51
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeMapConfigModel {
    @ApiModelProperty(value = "企业ID，0表示系统默认配置")
    private String corpId;

    @ApiModelProperty(value = "后端API密钥")
    private String apiKey;

    @ApiModelProperty(value = "iframe API密钥")
    private String iframeApiKey;

    @ApiModelProperty(value = "地图类型：1-腾讯地图, 2-高德地图, 3-百度地图")
    private Integer mapType;

    @ApiModelProperty(value = "接口调用限制信息列表，包含接口编码和调用限制次数")
    private List<ApiLimitInfo> dailyLimitList;

    @ApiModelProperty(value = "状态 0：停用 1：启用")
    private Integer status;

    public WeMapConfigModel(WeMapConfig corpConfig) {
        this.corpId = corpConfig.getCorpId();
        this.apiKey = corpConfig.getApiKey();
        this.iframeApiKey = corpConfig.getIframeApiKey();
        this.mapType = corpConfig.getMapType();
        this.dailyLimitList = StringUtils.isBlank(corpConfig.getDailyLimit())? new ArrayList<>(): JSONObject.parseArray(corpConfig.getDailyLimit(), ApiLimitInfo.class);
        this.status = corpConfig.getStatus();
    }

    /**
     * 是否企业配置
     *
     * @return
     */
    public boolean isCorpConfig() {
        return StringUtils.isNotBlank(corpId);
    }
}
