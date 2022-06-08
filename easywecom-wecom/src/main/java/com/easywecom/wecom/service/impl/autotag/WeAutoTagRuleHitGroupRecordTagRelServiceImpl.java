package com.easywecom.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitGroupRecordTagRel;
import com.easywecom.wecom.mapper.autotag.WeAutoTagRuleHitGroupRecordTagRelMapper;
import com.easywecom.wecom.service.autotag.WeAutoTagGroupSceneTagRelService;
import com.easywecom.wecom.service.autotag.WeAutoTagRuleHitGroupRecordTagRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户标签命中记录(WeAutoTagRuleHitGroupRecordTagRel)表服务实现类
 *
 * @author tigger
 * @since 2022-03-02 15:42:29
 */
@Service("weAutoTagRuleHitGroupRecordTagRelService")
public class WeAutoTagRuleHitGroupRecordTagRelServiceImpl extends ServiceImpl<WeAutoTagRuleHitGroupRecordTagRelMapper, WeAutoTagRuleHitGroupRecordTagRel> implements WeAutoTagRuleHitGroupRecordTagRelService {

    @Autowired
    private WeAutoTagGroupSceneTagRelService weAutoTagGroupSceneTagRelService;

    /**
     * 组装群标签关系记录
     *
     * @param ruleId                规则id
     * @param newJoinCustomerIdList 涉及的客户id列表
     * @param chatId                群id
     * @param tagList               标签列表
     * @return
     */
    @Override
    public List<WeAutoTagRuleHitGroupRecordTagRel> buildTagRel(Long ruleId, List<String> newJoinCustomerIdList, String chatId, List<WeTag> tagList) {
        List<WeAutoTagRuleHitGroupRecordTagRel> tagRelList = new ArrayList<>();
        if (ruleId == null || CollectionUtils.isEmpty(newJoinCustomerIdList) || StringUtils.isBlank(chatId)
                || CollectionUtils.isEmpty(tagList)) {
            log.error("参数异常,取消构建群标签记录数据");
            return tagRelList;
        }
        for (String customerId : newJoinCustomerIdList) {
            for (WeTag weTag : tagList) {
                tagRelList.add(new WeAutoTagRuleHitGroupRecordTagRel(ruleId, weTag.getTagId(), weTag.getName(), customerId, chatId));
            }
        }
        return tagRelList;
    }
}

