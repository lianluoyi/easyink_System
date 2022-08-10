package com.easyink.wecom.mapper;

import com.easyink.wecom.domain.WeMaterialConfig;
import org.springframework.stereotype.Repository;

/**
 * 类名：WeMaterialConfigMapper
 *
 * @author Society my sister Li
 * @date 2021-10-11
 */
@Repository
public interface WeMaterialConfigMapper {


    /**
     * 更新配置
     *
     * @param weMaterialConfig 素材配置
     * @return int
     */
    int update(WeMaterialConfig weMaterialConfig);

    /**
     * 新增配置
     *
     * @param weMaterialConfig 素材配置
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
