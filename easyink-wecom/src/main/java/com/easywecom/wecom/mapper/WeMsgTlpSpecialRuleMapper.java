package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeMsgTlpScope;
import com.easywecom.wecom.domain.WeMsgTlpSpecialRule;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 特殊规则欢迎语Mapper接口
 *
 * @author admin
 * @date 2020-10-04
 */
@Repository
public interface WeMsgTlpSpecialRuleMapper extends BaseMapper<WeMsgTlpSpecialRule> {
    /**
     * 根据默认欢迎语id获取特殊时段欢迎语list
     *
     * @param id 默认欢迎语id
     * @return
     */
    List<WeMsgTlpSpecialRule> getListByDefaultMsgId(Long id);
}
