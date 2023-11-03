package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeMsgTlpFilterRule;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 欢迎语筛选条件Mapper接口
 *
 * @author lichaoyu
 * @date 2023/10/25 15:45
 */
@Repository
public interface WeMsgTlpFilterRuleMapper extends BaseMapper<WeMsgTlpFilterRule> {

    /**
     * 根据关联的欢迎语模板id获取筛选条件列表
     *
     * @param msgTlpId 欢迎语模板id
     * @return 筛选条件列表
     */
    List<WeMsgTlpFilterRule> getListByTlpId(@Param("msgTlpId") Long msgTlpId);
}
