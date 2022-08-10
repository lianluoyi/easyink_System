package com.easyink.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagUserRel;
import com.easyink.wecom.mapper.autotag.WeAutoTagUserRelMapper;
import com.easyink.wecom.service.autotag.WeAutoTagUserRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        // 删除全部
        this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagUserRel>().eq(WeAutoTagUserRel::getRuleId, ruleId));
        // 重新插入
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
                    .in(WeAutoTagUserRel::getTargetId, notExistUserIdList));
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
    public List<Long> getCurrentUserIdAvailableCustomerRuleIdList(String cropId, List<Long> ruleCandidates, String userId) {
        List<Long> availableRuleIdList = new ArrayList<>();
        if (CollectionUtils.isEmpty(ruleCandidates) || StringUtils.isBlank(userId) || StringUtils.isBlank(cropId)) {
            return availableRuleIdList;
        }
        availableRuleIdList = this.baseMapper.listWeAutoTagRelByUserIdAndRuleIdList(cropId, userId, ruleCandidates);
        return availableRuleIdList;
    }

    /**
     * 查询该员工是否在标签规则的部门中
     * @param cropId
     * @param userId
     * @param hadUserScopeRuleIdList
     * @return
     */
    @Override
    public List<WeAutoTagUserRel> getInfoByUserIdFromDepartment(String cropId, String userId, List<Long> hadUserScopeRuleIdList) {
        List<WeAutoTagUserRel> weAutoTagUserRelList = new ArrayList<>();
        if (CollectionUtils.isEmpty(hadUserScopeRuleIdList) || StringUtils.isBlank(userId) || StringUtils.isBlank(cropId)) {
            return weAutoTagUserRelList;
        }
        weAutoTagUserRelList = this.baseMapper.listWeAutoTagUserRelByUserIdFromDepartment(cropId, userId,hadUserScopeRuleIdList);
        return weAutoTagUserRelList;
    }
}

