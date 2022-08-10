package com.easyink.wecom.service;

import com.easyink.wecom.domain.WeMaterialConfig;

/**
 * 类名：WeMaterialConfigService
 *
 * @author Society my sister Li
 * @date 2021-10-11
 */
public interface WeMaterialConfigService {


    /**
     * 更新素材配置信息
     *
     * @param weMaterialConfig weMaterialConfig
     * @return int
     */
    int update(WeMaterialConfig weMaterialConfig);

    /**
     * 新增素材配置信息
     *
     * @param weMaterialConfig weMaterialConfig
     * @return int
     */
    int insert(WeMaterialConfig weMaterialConfig);

    /**
     * 查询素材详细信息
     *
     * @param corpId corpId
     * @return {@link WeMaterialConfig}
     */
    WeMaterialConfig findByCorpId(String corpId);

}
