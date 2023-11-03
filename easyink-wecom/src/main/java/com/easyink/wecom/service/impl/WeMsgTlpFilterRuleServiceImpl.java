package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.AddWayEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.wemsgtlp.LogicTypeEnum;
import com.easyink.common.enums.wemsgtlp.WeMsgTlpFilterEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeMsgTlp;
import com.easyink.wecom.domain.WeMsgTlpFilterRule;
import com.easyink.wecom.mapper.WeMsgTlpFilterRuleMapper;
import com.easyink.wecom.service.WeMsgTlpFilterRuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 欢迎语筛选条件Service业务层处理
 *
 * @author lichaoyu
 * @date 2023/10/25 15:42
 */
@Slf4j
@Service
public class WeMsgTlpFilterRuleServiceImpl extends ServiceImpl<WeMsgTlpFilterRuleMapper, WeMsgTlpFilterRule> implements WeMsgTlpFilterRuleService {


    @Override
    public boolean saveBatchFilterRules(Long msgTlpId, List<WeMsgTlpFilterRule> weMsgTlpFilterRules) {
        if (msgTlpId == null || CollectionUtils.isEmpty(weMsgTlpFilterRules)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 为每个筛选规则设置企业ID和关联条件
        for (WeMsgTlpFilterRule weMsgTlpFilterRule : weMsgTlpFilterRules) {
            weMsgTlpFilterRule.builderMsgTlpInfo(msgTlpId);
        }
        // 批量保存
        return this.saveBatch(weMsgTlpFilterRules);
    }

    /**
     * 是否发送欢迎语
     *
     * @param weMsgTlp {@link WeMsgTlp}
     * @param addWay   客户添加来源
     * @param gender   客户性别
     * @return true 是， false 否
     */
    @Override
    public boolean isSendMsgTlp(WeMsgTlp weMsgTlp, String addWay, Integer gender) {
        if (weMsgTlp == null) {
            return false;
        }
        // 获取欢迎语模板对应的过滤规则列表
        List<WeMsgTlpFilterRule> filterRuleList = this.baseMapper.getListByTlpId(weMsgTlp.getId());
        // 若存在规则列表，进行判断
        if (CollectionUtils.isNotEmpty(filterRuleList)) {
            // 关联条件为”且“
            if (LogicTypeEnum.AND.getType().equals(weMsgTlp.getMultiFilterAssociation())) {
                // TODO: 由于时间和个人能力原因，该处使用的是if-else判断，后续需要抽出共有的过滤方法，根据条件判断执行对应类的判断方法。便于后续该功能的拓展和维护及易读性，实现效果为：若需要新增一个条件判断，只需要添加一个新的类。Tower 任务: 欢迎语区分来源 ( https://tower.im/teams/636204/todos/75488 )
                return filterAssociationAnd(filterRuleList, addWay, gender);
            } else {
                // 关联条件为”或“
                return filterAssociationOr(filterRuleList, addWay, gender);
            }
        }
        return false;
    }

    /**
     * 关联筛选条件为且，所有的筛选条件都满足才发送欢迎语，有一个不满足就返回false
     *
     * @param filterRuleList {@link List<WeMsgTlpFilterRule>}
     * @param addWay         客户添加来源
     * @param gender         客户性别
     * @return true 条件都满足， false 条件有一个不满足
     */
    private boolean filterAssociationAnd(List<WeMsgTlpFilterRule> filterRuleList, String addWay, Integer gender) {
        if (CollectionUtils.isEmpty(filterRuleList)) {
            return false;
        }
        boolean isSend = true;
        for (WeMsgTlpFilterRule item : filterRuleList) {
            // 判断条件 true 是， false 不是
            boolean condition = item.getFilterCondition();
            // 过滤值
            String filterValue = item.getFilterValue();
            // 来源条件判断
            if (WeMsgTlpFilterEnum.SOURCE.getType().equals(item.getFilterType())) {
                // 来源特殊规则判断，若来源的规则超过2条，且某条规则包含”所有来源“，则不发送欢迎语
                if (specialAddWayJudge(filterRuleList)) {
                    isSend = false;
                    break;
                }
                // 条件不匹配就返回
                if (!isMarkCondition(condition, filterValue, addWay)) {
                    isSend = false;
                    break;
                }
            } else {
                // 性别特殊规则判断，若性别的规则超过2条，且某条规则包含”所有性别“，则不发送欢迎语
                if (specialGenderJudge(filterRuleList)) {
                    isSend = false;
                    break;
                }
                // 性别判断，条件不匹配就返回
                if (!isMarkCondition(condition, filterValue, gender.toString())) {
                    isSend = false;
                    break;
                }
            }
        }
        return isSend;
    }

    /**
     * 关联筛选条件为或，根据顺序，有一个条件满足就发送欢迎语，全都不满足就返回false
     *
     * @param filterRuleList {@link List<WeMsgTlpFilterRule>}
     * @param addWay         客户添加来源
     * @param gender         客户性别
     * @return true 有一个条件满足， false 所有条件都不满足。
     */
    private boolean filterAssociationOr(List<WeMsgTlpFilterRule> filterRuleList, String addWay, Integer gender) {
        if (CollectionUtils.isEmpty(filterRuleList)) {
            return false;
        }
        boolean isSend = false;
        for (WeMsgTlpFilterRule item : filterRuleList) {
            // 判断条件 true 是， false 不是
            boolean condition = item.getFilterCondition();
            // 过滤值
            String filterValue = item.getFilterValue();
            // 来源条件判断
            if (WeMsgTlpFilterEnum.SOURCE.getType().equals(item.getFilterType())) {
                // 条件匹配就返回
                if (isMarkCondition(condition, filterValue, addWay)) {
                    isSend = true;
                    break;
                }
            } else {
                // 性别判断，条件匹配就返回
                if (isMarkCondition(condition, filterValue, gender.toString())) {
                    isSend = true;
                    break;
                }
            }
        }
        return isSend;
    }

    /**
     * 来源特殊规则判断
     * 设置的规则是否大于2条，且规则中存在来源为”所有来源”
     *
     * @param filterRuleList {@link List<WeMsgTlpFilterRule>}
     * @return true 是，false 不是
     */
    private boolean specialAddWayJudge(List<WeMsgTlpFilterRule> filterRuleList) {
        if (CollectionUtils.isEmpty(filterRuleList)) {
            return false;
        }
        // 多个来源规则判断，若规则大于2条，且规则中存在来源为”所有来源“，表示不发送欢迎语
        int specialNum = 2;
        // 获取规则中类型是来源的规则列表
        List<WeMsgTlpFilterRule> addWayRuleList = filterRuleList.stream()
                .filter(item -> WeMsgTlpFilterEnum.SOURCE.getType().equals(item.getFilterType()))
                .collect(Collectors.toList());
        return addWayRuleList.size() >= specialNum && addWayRuleList.stream().anyMatch(item -> AddWayEnum.ALL_ADD_WAY.getCode().toString().equals(item.getFilterValue()));
    }

    /**
     * 性别特殊规则判断
     * 设置的规则是否大于2条，且规则中存在性别为”所有性别“
     *
     * @param filterRuleList {@link List<WeMsgTlpFilterRule>}
     * @return true 是，false 不是
     */
    private boolean specialGenderJudge(List<WeMsgTlpFilterRule> filterRuleList) {
        if (CollectionUtils.isEmpty(filterRuleList)) {
            return false;
        }
        // 多个规则判断，设置的规则是否大于2条，且规则中存在性别为”所有性别“
        int specialNum = 2;
        // 获取规则中类型是性别的规则列表
        List<WeMsgTlpFilterRule> genderRuleList = filterRuleList.stream()
                .filter(item -> WeMsgTlpFilterEnum.GENDER.getType().equals(item.getFilterType()))
                .collect(Collectors.toList());
        return genderRuleList.size() >= specialNum && genderRuleList.stream().anyMatch(item -> WeConstans.corpUserEnum.USER_SEX_TYPE_ALL.getKey().toString().equals(item.getFilterValue()));
    }

    /**
     * 根据条件判断是否匹配
     *
     * @param condition     判断条件，true：是，false：不是
     * @param filterValue   过滤的值
     * @param customerValue 客户的值
     * @return true 匹配，false 不匹配
     */
    private boolean isMarkCondition(boolean condition, String filterValue, String customerValue) {
        if (StringUtils.isAnyBlank(filterValue, customerValue)) {
            return false;
        }
        // 筛选条件判断类型
        if (condition) {
            // 判断条件来源是所有来源或性别是所有性别，直接返回ture
            if (AddWayEnum.ALL_ADD_WAY.getCode().toString().equals(filterValue) || WeConstans.corpUserEnum.USER_SEX_TYPE_ALL.getKey().toString().equals(filterValue)) {
                return true;
            }
            // 判断条件为是，属性值不匹配，表示不符合条件，返回false
            return filterValue.equals(customerValue);
        } else {
            // 判断条件来源是所有来源或性别是所有性别，直接返回false
            if (AddWayEnum.ALL_ADD_WAY.getCode().toString().equals(filterValue) || WeConstans.corpUserEnum.USER_SEX_TYPE_ALL.getKey().toString().equals(filterValue)) {
                return false;
            }
            // 判断条件为不是，属性值匹配，表示不符合条件，返回false
            return !filterValue.equals(customerValue);
        }
    }
}
