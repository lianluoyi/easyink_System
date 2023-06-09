package com.easyink.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.dto.autotag.customer.CustomerSceneDTO;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerSceneTagRel;
import com.easyink.wecom.mapper.autotag.WeAutoTagCustomerSceneTagRelMapper;
import com.easyink.wecom.service.WeTagService;
import com.easyink.wecom.service.autotag.WeAutoTagCustomerSceneTagRelService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 新客标签场景与标签关系表(WeAutoTagCustomerSceneTagRel)表服务实现类
 *
 * @author tigger
 * @since 2022-02-27 15:52:32
 */
@Service("weAutoTagCustomerSceneTagRelService")
public class WeAutoTagCustomerSceneTagRelServiceImpl extends ServiceImpl<WeAutoTagCustomerSceneTagRelMapper, WeAutoTagCustomerSceneTagRel> implements WeAutoTagCustomerSceneTagRelService {

    @Autowired
    private WeTagService weTagService;

    /**
     * 批量添加
     *
     * @param toWeAutoTagCustomerSceneTagRelList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagCustomerSceneTagRel> toWeAutoTagCustomerSceneTagRelList) {
        return this.batchSave(toWeAutoTagCustomerSceneTagRelList, true);
    }

    /**
     * 批量添加或修改
     *
     * @param toWeAutoTagCustomerSceneTagRelList
     * @param insertOnly
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagCustomerSceneTagRel> toWeAutoTagCustomerSceneTagRelList, Boolean insertOnly) {
        if (CollectionUtils.isNotEmpty(toWeAutoTagCustomerSceneTagRelList)) {
            if (insertOnly) {
                return this.baseMapper.insertBatch(toWeAutoTagCustomerSceneTagRelList);
            }
            return this.baseMapper.insertOrUpdateBatch(toWeAutoTagCustomerSceneTagRelList);
        }
        return 0;
    }

    /**
     * 修改
     *
     * @param customerSceneList
     * @param toWeAutoTagCustomerSceneTagRelList
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int edit(List<CustomerSceneDTO> customerSceneList, List<WeAutoTagCustomerSceneTagRel> toWeAutoTagCustomerSceneTagRelList) {
        // 删除对应客户场景下的标签
        for (CustomerSceneDTO customerSceneDTO : customerSceneList) {
            Long customerSceneId = customerSceneDTO.getId();
            List<String> removeTagIdList = customerSceneDTO.getRemoveTagIdList();
            if (customerSceneId != null && CollectionUtils.isNotEmpty(removeTagIdList)) {
                this.removeBySceneIdAndTagIdList(customerSceneId, removeTagIdList);
            }
        }
        // 修改
        return this.batchSave(toWeAutoTagCustomerSceneTagRelList, false);
    }

    /**
     * 删除场景标签根据场景列表
     *
     * @param removeSceneIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeBySceneIdList(List<Long> removeSceneIdList) {
        if (CollectionUtils.isNotEmpty(removeSceneIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagCustomerSceneTagRel>()
                    .in(WeAutoTagCustomerSceneTagRel::getCustomerSceneId, removeSceneIdList));
        }
        return 0;
    }

    /**
     * 删除指定场景下的标签列表
     *
     * @param customerSceneId
     * @param removeTagIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeBySceneIdAndTagIdList(Long customerSceneId, List<String> removeTagIdList) {
        if (customerSceneId == null) {
            log.error("新客场景id为null");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (CollectionUtils.isNotEmpty(removeTagIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagCustomerSceneTagRel>()
                    .eq(WeAutoTagCustomerSceneTagRel::getCustomerSceneId, customerSceneId)
                    .in(WeAutoTagCustomerSceneTagRel::getTagId, removeTagIdList));
        }
        return 0;
    }

    /**
     * 根据规则id列表删除新客场景标签信息
     *
     * @param removeRuleIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByRuleIdList(List<Long> removeRuleIdList) {
        if (CollectionUtils.isNotEmpty(removeRuleIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagCustomerSceneTagRel>()
                    .in(WeAutoTagCustomerSceneTagRel::getRuleId, removeRuleIdList));
        }
        return 0;
    }

    /**
     * 获取场景下的标签列表
     *
     * @param sceneIdList
     * @return
     */
    @Override
    public List<WeTag> getTagListBySceneIdList(List<Long> sceneIdList) {
        if (CollectionUtils.isEmpty(sceneIdList)) {
            return Lists.newArrayList();
        }
        List<WeAutoTagCustomerSceneTagRel> tagRelList = this.list(new LambdaQueryWrapper<WeAutoTagCustomerSceneTagRel>()
                .in(WeAutoTagCustomerSceneTagRel::getCustomerSceneId, sceneIdList));
        Set<String> tagIdsSet = tagRelList.stream().map(WeAutoTagCustomerSceneTagRel::getTagId).collect(Collectors.toSet());

        return weTagService.list(new LambdaQueryWrapper<WeTag>()
                .in(WeTag::getTagId, tagIdsSet));
    }
}

