package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.WelcomeMsgRuleTypeEnum;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.WeMsgTlpSpecialRule;
import com.easywecom.wecom.mapper.WeMsgTlpSpecialRuleMapper;
import com.easywecom.wecom.service.WeMsgTlpSpecialRuleService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 模板使用人员范围Service接口
 *
 * @author admin
 * @date 2020-10-04
 */
@Service
public class WeMsgTlpSpecialRuleServiceImpl extends ServiceImpl<WeMsgTlpSpecialRuleMapper, WeMsgTlpSpecialRule> implements WeMsgTlpSpecialRuleService {

    private WeMsgTlpSpecialRuleMapper weMsgTlpSpecialRuleMapper;
    private WeMsgTlpSpecialRuleService weMsgTlpSpecialRuleService;

    @Lazy
    @Autowired
    public WeMsgTlpSpecialRuleServiceImpl(WeMsgTlpSpecialRuleMapper weMsgTlpSpecialRuleMapper, WeMsgTlpSpecialRuleService weMsgTlpSpecialRuleService) {
        this.weMsgTlpSpecialRuleMapper = weMsgTlpSpecialRuleMapper;
        this.weMsgTlpSpecialRuleService = weMsgTlpSpecialRuleService;
    }

    /**
     * 批量插入员工特殊欢迎语
     *
     * @param defaultMsgId         默认欢迎语id
     * @param weMsgTlpSpecialRules 特殊时段欢迎语
     */
    @Override
    public void saveSpecialMsgBatch(Long defaultMsgId, List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules) {
        if (defaultMsgId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        checkTime(weMsgTlpSpecialRules);
        for (WeMsgTlpSpecialRule weMsgTlpSpecialRule : weMsgTlpSpecialRules) {
            // 目前只有一个特殊规则，写死
            weMsgTlpSpecialRule.setRuleType(WelcomeMsgRuleTypeEnum.WEEKEND.getType());
            weMsgTlpSpecialRule.setMsgTlpId(defaultMsgId);
            weMsgTlpSpecialRule.setWeekends(String.join(",", weMsgTlpSpecialRule.getWeekendList()));
        }
        weMsgTlpSpecialRuleService.saveBatch(weMsgTlpSpecialRules);
    }

    /**
     * 修改特殊欢迎语
     *
     * @param removeSpecialRuleIds 需要删除的特殊欢迎语ids
     * @param weMsgTlpSpecialRules 添加或修改的特殊欢迎语
     * @param defaultMsgId         默认欢迎语id
     */
    @Override
    public void updateSpecialRuleMsg(List<Long> removeSpecialRuleIds, List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules, Long defaultMsgId) {
        if (defaultMsgId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (CollectionUtils.isNotEmpty(removeSpecialRuleIds)) {
            weMsgTlpSpecialRuleService.removeByIds(removeSpecialRuleIds);
        }
        // 3.2 修改会删除特殊欢迎语
        if (CollectionUtils.isNotEmpty(weMsgTlpSpecialRules)) {
            for (WeMsgTlpSpecialRule weMsgTlpSpecialRule : weMsgTlpSpecialRules) {
                weMsgTlpSpecialRule.setMsgTlpId(defaultMsgId);
                weMsgTlpSpecialRule.setWeekends(String.join(",", weMsgTlpSpecialRule.getWeekendList()));
            }
            checkTime(weMsgTlpSpecialRules);
            weMsgTlpSpecialRuleService.saveOrUpdateBatch(weMsgTlpSpecialRules);
        }
    }

    /**
     * 查询所有,处理weekends和weekendList的数据转换
     *
     * @param defaultMsgId
     * @return
     */
    @Override
    public List<WeMsgTlpSpecialRule> listAll(Long defaultMsgId) {
        List<WeMsgTlpSpecialRule> allList = weMsgTlpSpecialRuleMapper.selectList(new LambdaQueryWrapper<WeMsgTlpSpecialRule>()
                .eq(WeMsgTlpSpecialRule::getMsgTlpId, defaultMsgId));
        for (WeMsgTlpSpecialRule weMsgTlpSpecialRule : allList) {
            String weekends = weMsgTlpSpecialRule.getWeekends();
            String[] split = weekends.split(",");
            weMsgTlpSpecialRule.setWeekendList(split);
        }
        return allList;
    }


    /**
     * 校验时段是否重复
     */
    private void checkTime(List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules) {

        // 校验时间前的准备
        preCheckTime(weMsgTlpSpecialRules);

        // 不重复标准， 1.相同周 2. T1end < T2begin &&  || T2end < T1begin
        for (int i = 0; i < weMsgTlpSpecialRules.size(); i++) {
            for (int j = i + 1; j < weMsgTlpSpecialRules.size(); j++) {
                WeMsgTlpSpecialRule t1 = weMsgTlpSpecialRules.get(i);
                WeMsgTlpSpecialRule t2 = weMsgTlpSpecialRules.get(j);
                // 存在相同周则需要比较time
                if (judgeWeekendSame(t1.getWeekendList(), t2.getWeekendList())) {
                    if (t1.getWeekendEndTime().compareTo(t2.getWeekendBeginTime()) > 0 && t1.getWeekendEndTime().compareTo(t2.getWeekendEndTime()) <= 0
                            || t2.getWeekendEndTime().compareTo(t1.getWeekendBeginTime()) > 0 && t2.getWeekendEndTime().compareTo(t1.getWeekendEndTime()) <= 0) {
                        throw new CustomException(ResultTip.TIP_MSG_REPEATED_TIME);
                    }
                }
            }
        }
    }

    /**
     * 校验时间前的准备
     *
     * @param weMsgTlpSpecialRules
     */
    private void preCheckTime(List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules) {
        // 校验结束时间大于开始时间
        checkEndAfterBegin(weMsgTlpSpecialRules);
    }

    /**
     * 校验结束时间大于开始时间
     */
    private void checkEndAfterBegin(List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules) {
        if (CollectionUtils.isNotEmpty(weMsgTlpSpecialRules)) {
            for (WeMsgTlpSpecialRule weMsgTlpSpecialRule : weMsgTlpSpecialRules) {
                if (weMsgTlpSpecialRule.getWeekendBeginTime().compareTo(weMsgTlpSpecialRule.getWeekendEndTime()) > 0) {
                    throw new CustomException(ResultTip.TIP_START_AFTER_END_TIME);
                }
            }
        }
    }

    /**
     * 判断是否存在相同周
     *
     * @param w1 周数据1
     * @param w2 周数据2
     */
    private boolean judgeWeekendSame(String[] w1, String[] w2) {
        Arrays.sort(w1);
        Arrays.sort(w2);
        for (String s1 : w1) {
            for (String s2 : w2) {
                if (s1.equals(s2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
