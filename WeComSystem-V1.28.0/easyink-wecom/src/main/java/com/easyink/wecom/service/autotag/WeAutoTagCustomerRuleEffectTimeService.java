package com.easyink.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerRuleEffectTime;

import java.util.List;

/**
 * 新客规则生效时间表(WeAutoTagCustomerRuleEffectTime)表服务接口
 *
 * @author tigger
 * @since 2022-02-27 15:52:29
 */
public interface WeAutoTagCustomerRuleEffectTimeService extends IService<WeAutoTagCustomerRuleEffectTime> {

    /**
     * 修改
     *
     * @param weAutoTagCustomerRuleEffectTime
     */
    int edit(WeAutoTagCustomerRuleEffectTime weAutoTagCustomerRuleEffectTime);

    /**
     * 根据规则id列表删除新客规则生效时间信息
     *
     * @param removeRuleIdList
     * @return
     */
    int removeByRuleIdList(List<Long> removeRuleIdList);

    /**
     * 从normalRuleIdList中查询存在规则id的规则列表
     *
     * @param normalRuleIdList 总的规则id列表
     * @param corpId           企业id
     * @return
     */
    List<Long> selectHadEffectTimeRule(List<Long> normalRuleIdList, String corpId);
}

