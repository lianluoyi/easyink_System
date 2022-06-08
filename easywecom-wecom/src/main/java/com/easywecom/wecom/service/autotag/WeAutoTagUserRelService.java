package com.easywecom.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagUserRel;

import java.util.List;

/**
 * 标签与员工使用范围表(WeAutoTagUserRel)表服务接口
 *
 * @author tigger
 * @since 2022-02-27 15:52:46
 */
public interface WeAutoTagUserRelService extends IService<WeAutoTagUserRel> {

    /**
     * 批量添加
     *
     * @param toWeAutoTagUserRel
     * @return
     */
    int batchSave(List<WeAutoTagUserRel> toWeAutoTagUserRel);

    /**
     * 批量添加
     *
     * @param toWeAutoTagUserRel
     * @param insertOnly
     * @return
     */
    int batchSave(List<WeAutoTagUserRel> toWeAutoTagUserRel, Boolean insertOnly);

    /**
     * 修改员工使用范围
     *
     * @param toWeAutoTagUserRel
     * @param ruleId
     * @return
     */
    int edit(List<WeAutoTagUserRel> toWeAutoTagUserRel, Long ruleId);

    /**
     * 删除规则下的userId列表
     *
     * @param ruleId
     * @param notExistUserIdList
     * @return
     */
    int removeByRuleIdAndUserIdList(Long ruleId, List<String> notExistUserIdList);

    /**
     * 根据ruleIdList删除员工使用范围
     *
     * @param removeRuleIdList
     * @return
     */
    int removeByRuleIdList(List<Long> removeRuleIdList);

    /**
     * 获取可用的规则id列表
     *
     * @param ruleCandidates 候选规则id列表
     * @param userId         员工id
     * @return
     */
    List<Long> getCurrentUserIdAvailableCustomerRuleIdList(List<Long> ruleCandidates, String userId);
}

