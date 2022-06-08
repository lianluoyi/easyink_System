package com.easywecom.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagUserRel;
import com.easywecom.wecom.mapper.autotag.WeAutoTagUserRelMapper;
import com.easywecom.wecom.service.autotag.WeAutoTagUserRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标签与员工使用范围表(WeAutoTagUserRel)表服务实现类
 *
 * @author tigger
 * @since 2022-02-27 15:52:46
 */
@Service("weAutoTagUserRelService")
public class WeAutoTagUserRelServiceImpl extends ServiceImpl<WeAutoTagUserRelMapper, WeAutoTagUserRel> implements WeAutoTagUserRelService {

    /**
     * 批量添加
     *
     * @param toWeAutoTagUserRel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagUserRel> toWeAutoTagUserRel) {
        return this.batchSave(toWeAutoTagUserRel, true);
    }

    /**
     * 批量添加或修改
     *
     * @param toWeAutoTagUserRel
     * @param insertOnly
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagUserRel> toWeAutoTagUserRel, Boolean insertOnly) {
        if (CollectionUtils.isNotEmpty(toWeAutoTagUserRel)) {
            if (insertOnly) {
                return this.baseMapper.insertBatch(toWeAutoTagUserRel);
            }
            return this.baseMapper.insertOrUpdateBatch(toWeAutoTagUserRel);
        }
        return 0;
    }

    /**
     * 修改员工使用范围
     *
     * @param toWeAutoTagUserRel
     * @param ruleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int edit(List<WeAutoTagUserRel> toWeAutoTagUserRel, Long ruleId) {
        if (ruleId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (CollectionUtils.isEmpty(toWeAutoTagUserRel)) {
            // 删除全部,表示全选
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagUserRel>().eq(WeAutoTagUserRel::getRuleId, ruleId));
        }
        // 查询数据库中不存在的
        List<String> modifyUserIdList = toWeAutoTagUserRel.stream().map(WeAutoTagUserRel::getUserId).collect(Collectors.toList());

        List<WeAutoTagUserRel> notExistWeAutoTagUserRelList = this.list(new LambdaQueryWrapper<WeAutoTagUserRel>()
                .eq(WeAutoTagUserRel::getRuleId, ruleId)
                .notIn(WeAutoTagUserRel::getUserId, modifyUserIdList));
        List<String> notExistUserIdList = notExistWeAutoTagUserRelList.stream().map(WeAutoTagUserRel::getUserId).collect(Collectors.toList());

        // 删除不存在的
        this.removeByRuleIdAndUserIdList(ruleId, notExistUserIdList);

        // 修改
        return this.batchSave(toWeAutoTagUserRel, false);
    }

    /**
     * 删除规则下的userId列表
     *
     * @param ruleId
     * @param notExistUserIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByRuleIdAndUserIdList(Long ruleId, List<String> notExistUserIdList) {
        if (ruleId == null) {
            log.error("ruleId为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (CollectionUtils.isNotEmpty(notExistUserIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagUserRel>()
                    .eq(WeAutoTagUserRel::getRuleId, ruleId)
                    .in(WeAutoTagUserRel::getUserId, notExistUserIdList));
        }
        return 0;
    }

    /**
     * 根据ruleIdList删除员工使用范围
     *
     * @param removeRuleIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByRuleIdList(List<Long> removeRuleIdList) {
        if (CollectionUtils.isNotEmpty(removeRuleIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagUserRel>()
                    .in(WeAutoTagUserRel::getRuleId, removeRuleIdList));
        }
        return 0;
    }

    /**
     * 获取可用的规则id列表
     *
     * @param ruleCandidates 候选规则id列表
     * @param userId         员工id
     * @return
     */
    @Override
    public List<Long> getCurrentUserIdAvailableCustomerRuleIdList(List<Long> ruleCandidates, String userId) {
        List<Long> availableRuleIdList = new ArrayList<>();
        if (CollectionUtils.isEmpty(ruleCandidates)) {
            return availableRuleIdList;
        }
        List<WeAutoTagUserRel> weAutoTagUserRelList = this.baseMapper.selectList(new LambdaQueryWrapper<WeAutoTagUserRel>()
                .in(WeAutoTagUserRel::getRuleId, ruleCandidates));
        //                hadCurrentUserRelRuleIdSet
        Set<Long> availableHadUserScopeRuleIdList = weAutoTagUserRelList
                .stream()
                .map(WeAutoTagUserRel::getRuleId)
                .collect(Collectors.toSet());


        List<Long> notHaveUserRelRuleIdList = new ArrayList<>(ruleCandidates);
        // 符合条件的没有员工关系的规则,直接添加
        notHaveUserRelRuleIdList.removeAll(availableHadUserScopeRuleIdList);
        availableRuleIdList.addAll(notHaveUserRelRuleIdList);

        // 判断有员工关系的规则是否符合当前员工
        for (WeAutoTagUserRel weAutoTagUserRel : weAutoTagUserRelList) {
            if (userId.equals(weAutoTagUserRel.getUserId())) {
                availableRuleIdList.add(weAutoTagUserRel.getRuleId());
            }
        }

        return availableRuleIdList;
    }
}

