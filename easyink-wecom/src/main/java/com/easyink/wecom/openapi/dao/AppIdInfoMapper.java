package com.easyink.wecom.openapi.dao;

import com.easyink.wecom.openapi.domain.entity.AppIdInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名: appId持久层接口
 *
 * @author : silver_chariot
 * @date : 2022/3/14 14:04
 */
@Mapper
@Repository
public interface AppIdInfoMapper {
    /**
     * 获取开发参数
     *
     * @param corpId 企业ID
     * @return 开发参数 {@link AppIdInfo}
     */
    AppIdInfo get(@Param("corpId") String corpId);

    /**
     * 插入
     *
     * @param appIdInfo {@link AppIdInfo}
     * @return affected rows
     */
    Integer insert(AppIdInfo appIdInfo);

    /**
     * 更新
     *
     * @param appIdInfo {@link AppIdInfo}
     * @return affected rows
     */
    Integer update(AppIdInfo appIdInfo);

    /**
     * 获取系统全部的app信息
     *
     * @return appId集合   {@link AppIdInfo}
     */
    List<AppIdInfo> getAll();
}
