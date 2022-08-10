package com.easywecom.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagCustomerRuleEffectTime;
import com.easywecom.wecom.mapper.autotag.WeAutoTagCustomerRuleEffectTimeMapper;
import com.easywecom.wecom.service.autotag.WeAutoTagCustomerRuleEffectTimeService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 新客规则生效时间表(WeAutoTagCustomerRuleEffectTime)表服务实现类
 *
 * @author tigger
 * @since 2022-02-27 15:52:29
 */
@Service("weAutoTagCustomerRuleEffectTimeService")
public class WeAutoTagCustomerRuleEffectTimeServiceImpl extends ServiceImpl<WeAutoTagCustomerRuleEffectTimeMapper, WeAutoTagCustomerRuleEffectTime> implements WeAutoTagCustomerRuleEffectTimeService {

    /**
     * 修改
     *
     * @param weAutoTagCustomerRuleEffectTime
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int edit(WeAutoTagCustomerRuleEffectTime weAutoTagCustomerRuleEffectTime) {
        // 不传时间,表示永久生效,删除掉改记录行
        if (StringUtils.isBlank(weAutoTagCustomerRuleEffectTime.getEffectBeginTime())
                || StringUtils.isBlank(weAutoTagCustomerRuleEffectTime.getEffectEndTime())) {
            // 删除
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagCustomerRuleEffectTime>()
                    .eq(WeAutoTagCustomerRuleEffectTime::getRuleId, weAutoTagCustomerRuleEffectTime.getRuleId()));
        }
        return this.baseMapper.insertOrUpdateBatch(Collections.singletonList(weAutoTagCustomerRuleEffectTime));
    }

    /**
     * 根据规则id列表删除新客规则生效时间信息
     *
     * @param removeRuleIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByRuleIdList(List<Long> removeRuleIdList) {
        if (CollectionUtils.isNotEmpty(removeRuleIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagCustomerRuleEffectTime>()
                    .in(WeAutoTagCustomerRuleEffectTime::getRuleId, removeRuleIdList));
        }
        return 0;
    }

    /**
     * 从normalRuleIdList中查询存在规则id的规则列表
     *
     * @param normalRuleIdList 总的规则id列表
     * @param corpId           企业id
     * @return
     */
    @Override
    public List<Long> selectHadEffectTimeRule(List<Long> normalRuleIdList, String corpId) {
        if (CollectionUtils.isEmpty(normalRuleIdList) || StringUtils.isBlank(corpId)) {
            return Lists.newArrayList();
        }
        List<WeAutoTagCustomerRuleEffectTime> weAutoTagCustomerRuleEffectTimeList =
                this.baseMapper.selectList(new LambdaQueryWrapper<WeAutoTagCustomerRuleEffectTime>()
                        .in(WeAutoTagCustomerRuleEffectTime::getRuleId, normalRuleIdList));
        Set<Long> ruleIdSet = weAutoTagCustomerRuleEffectTimeList.stream().map(WeAutoTagCustomerRuleEffectTime::getRuleId)
                .collect(Collectors.toSet());
        return new ArrayList<>(ruleIdSet);
    }
}

