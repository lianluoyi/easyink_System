package com.easywecom.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitKeywordRecordTagRel;
import com.easywecom.wecom.mapper.autotag.WeAutoTagRuleHitKeywordRecordTagRelMapper;
import com.easywecom.wecom.service.autotag.WeAutoTagKeywordTagRelService;
import com.easywecom.wecom.service.autotag.WeAutoTagRuleHitKeywordRecordTagRelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 客户标签命中记录(WeAutoTagRuleHitKeywordRecordTagRel)表服务实现类
 *
 * @author tigger
 * @since 2022-03-02 14:51:30
 */
@Slf4j
@Service("weAutoTagRuleHitKeywordRecordTagRelService")
public class WeAutoTagRuleHitKeywordRecordTagRelServiceImpl extends ServiceImpl<WeAutoTagRuleHitKeywordRecordTagRelMapper, WeAutoTagRuleHitKeywordRecordTagRel> implements WeAutoTagRuleHitKeywordRecordTagRelService {

    @Autowired
    private WeAutoTagKeywordTagRelService weAutoTagKeywordTagRelService;

    /**
     * 组装标签记录
     *
     * @param corpId           企业id
     * @param userId           员工id
     * @param customerId       客户id
     * @param matchedRuleIdSet 匹配到的规则id
     * @return
     */
    @Override
    public List<WeAutoTagRuleHitKeywordRecordTagRel> buildTagRecord(String corpId, String userId, String customerId, Set<Long> matchedRuleIdSet) {
        List<WeAutoTagRuleHitKeywordRecordTagRel> batchList = new ArrayList<>();
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(corpId) || StringUtils.isBlank(corpId)) {
            log.error("参数异常,取消构建关键词标签记录数据");
            return batchList;
        }
        if (CollectionUtils.isEmpty(matchedRuleIdSet)) {
            log.info("无匹配到的规则id,取消构建标签记录数据");
            return batchList;
        }
        for (Long ruleId : matchedRuleIdSet) {
            HashSet<Long> ruleIdSet = new HashSet<>();
            ruleIdSet.add(ruleId);
            List<WeTag> lsit = weAutoTagKeywordTagRelService.getTagListByRuleIdList(ruleIdSet);
            for (WeTag weTag : lsit) {
                batchList.add(new WeAutoTagRuleHitKeywordRecordTagRel(ruleId, weTag.getTagId(), weTag.getName(), customerId, userId));
            }
        }
        return batchList;
    }
}

