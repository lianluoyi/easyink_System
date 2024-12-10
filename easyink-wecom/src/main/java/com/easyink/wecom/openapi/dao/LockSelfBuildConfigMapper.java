package com.easyink.wecom.openapi.dao;

import com.easyink.wecom.openapi.domain.entity.LockSelfBuildConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 类名: appId持久层接口
 *
 * @author : silver_chariot
 * @date : 2022/3/14 14:04
 */
@Mapper
@Repository
public interface LockSelfBuildConfigMapper {
    /**
     * 获取开发参数
     *
     * @param encryptCorpId 企业ID
     * @return 开发参数 {@link LockSelfBuildConfig}
     */
    LockSelfBuildConfig get(@Param("encryptCorpId") String encryptCorpId);

    /**
     * 插入
     *
     * @param config
     * @return affected rows
     */
    Integer insertOrUpdate(LockSelfBuildConfig config);


}
