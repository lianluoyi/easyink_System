package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeMsgTlpSpecialRule;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 特殊规则欢迎语Mapper接口
 *
 * @author admin
 * @date 2020-10-04
 */
@Repository
public interface WeMsgTlpSpecialRuleService extends IService<WeMsgTlpSpecialRule> {
    /**
     * 批量插入员工特殊欢迎语
     *
     * @param defaultMsgId         默认欢迎语id
     * @param weMsgTlpSpecialRules 特殊时段欢迎语
     */
    void saveSpecialMsgBatch(Long defaultMsgId, List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules);

    /**
     * 修改特殊欢迎语
     *
     * @param removeSpecialRuleIds 需要删除的特殊欢迎语ids
     * @param weMsgTlpSpecialRules 添加或修改的特殊欢迎语
     * @param defaultMsgId         默认欢迎语id
     */
    void updateSpecialRuleMsg(List<Long> removeSpecialRuleIds, List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules, Long defaultMsgId);


    /**
     * 查询所有,处理weekends和weekendList的映射
     *
     * @return
     * @param defaultMsgId
     */
    List<WeMsgTlpSpecialRule> listAll(Long defaultMsgId);

}
