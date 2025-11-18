package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeCorpThirdPartyConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 企业第三方服务推送配置Mapper接口
 *
 * @author easyink
 * @date 2024-01-01
 */
@Mapper
public interface WeCorpThirdPartyConfigMapper extends BaseMapper<WeCorpThirdPartyConfig> {

    /**
     * 根据企业ID获取第三方配置
     *
     * @param corpId 企业ID
     * @return 第三方配置
     */
    WeCorpThirdPartyConfig selectByCorpId(@Param("corpId") String corpId);

    /**
     * 根据企业ID更新配置状态
     *
     * @param corpId 企业ID
     * @param status 状态
     * @return 更新结果
     */
    int updateStatusByCorpId(@Param("corpId") String corpId, @Param("status") Integer status);

}
