package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.WeMapConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 地图API配置Mapper
 *
 * @author wx
 * @date 2023/8/3
 */
@Mapper
@Repository
public interface WeMapConfigMapper extends BaseMapper<WeMapConfig> {

    /**
     * 获取系统默认配置
     *
     * @param mapType 地图类型
     * @return 系统配置
     */
    WeMapConfig getDefaultConfig(@Param("mapType") Integer mapType);
    
    /**
     * 获取企业配置
     *
     * @param corpId 企业ID
     * @param mapType 地图类型
     * @return 企业配置
     */
    WeMapConfig getCorpConfig(@Param("corpId") String corpId, @Param("mapType") Integer mapType);
} 