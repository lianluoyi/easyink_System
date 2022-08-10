package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import org.apache.ibatis.annotations.Param;

/**
 * 企业id相关配置Mapper接口
 *
 * @author admin
 * @date 2020-08-24
 */
public interface WeCorpAccountMapper extends BaseMapper<WeCorpAccount> {


    /**
     * 启用有效的企业微信账号
     *
     * @param corpId
     * @return
     */
    int startVailWeCorpAccount(@Param("corpId") String corpId);

    /**
     * 删企业配置
     *
     * @param corpId 企业ID
     * @return 影响行数
     */
    int delWeCorpAccount(@Param("corpId") String corpId);


}
