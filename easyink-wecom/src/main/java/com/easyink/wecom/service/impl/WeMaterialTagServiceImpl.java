package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeMaterialTagEntity;
import com.easyink.wecom.domain.WeMaterialTagRelEntity;
import com.easyink.wecom.domain.vo.WeMaterialTagVO;
import com.easyink.wecom.mapper.WeMaterialTagMapper;
import com.easyink.wecom.mapper.WeMaterialTagRelMapper;
import com.easyink.wecom.service.WeMaterialTagRelService;
import com.easyink.wecom.service.WeMaterialTagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名： 素材标签接口
 *
 * @author 佚名
 * @date 2021/10/11 18:34
 */
@Service
@Slf4j
public class WeMaterialTagServiceImpl implements WeMaterialTagService {
    private final WeMaterialTagMapper weMaterialTagMapper;
    private final WeMaterialTagRelService weMaterialTagRelService;
    private final WeMaterialTagRelMapper weMaterialTagRelMapper;

    @Autowired
    public WeMaterialTagServiceImpl(WeMaterialTagMapper weMaterialTagMapper, WeMaterialTagRelService weMaterialTagRelService, WeMaterialTagRelMapper weMaterialTagRelMapper) {
        this.weMaterialTagMapper = weMaterialTagMapper;
        this.weMaterialTagRelService = weMaterialTagRelService;
        this.weMaterialTagRelMapper = weMaterialTagRelMapper;
    }

    /**
     * 批量打标签
     *
     * @param materialIds 素材id列表
     * @param tagIds      标签id列表
     */
    @Override
    public void markTags(List<Long> materialIds, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(materialIds) || CollectionUtils.isEmpty(tagIds)) {
            throw new CustomException(ResultTip.TIP_MISS_MATERIAL_TAG_PARAMETER);
        }
        //获得关系实体列表
        List<WeMaterialTagRelEntity> weMaterialTagRelEntities = buildTagRelList(materialIds, tagIds);
        //插入关系
        weMaterialTagRelService.addTagRels(weMaterialTagRelEntities);
    }

    /**
     * 获得关系实体列表
     *
     * @param materialIds 素材id列表
     * @param tagIds      标签id列表
     * @return {@link List<WeMaterialTagRelEntity>}
     */
    private List<WeMaterialTagRelEntity> buildTagRelList(List<Long> materialIds, List<Long> tagIds) {
        List<WeMaterialTagRelEntity> weMaterialTagRelEntities = new ArrayList<>();
        for (Long materialId : materialIds) {
            List<WeMaterialTagRelEntity> entities = tagIds.stream().map(tagId -> {
                //组合关系实体
                WeMaterialTagRelEntity weMaterialTagRelEntity = new WeMaterialTagRelEntity();
                weMaterialTagRelEntity.setMaterialId(materialId);
                weMaterialTagRelEntity.setMaterialTagId(tagId);
                return weMaterialTagRelEntity;
            }).collect(Collectors.toList());
            weMaterialTagRelEntities.addAll(entities);
        }
        return weMaterialTagRelEntities;
    }

    /**
     * 批量移除标签
     *
     * @param tagIds 标签id列表
     * @param materialIds 素材id列表
     */
    @Override
    public void removeTags(List<Long> tagIds, List<Long> materialIds) {
        if (CollectionUtils.isEmpty(tagIds) || CollectionUtils.isEmpty(materialIds)) {
            throw new CustomException(ResultTip.TIP_MISS_MATERIAL_TAG_PARAMETER);
        }

        weMaterialTagRelService.delTagRels(buildTagRelList(materialIds, tagIds));
    }

    /**
     * 删除标签
     *
     * @param tagId 标签id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delTag(Long tagId) {
        //删除实体联系
        weMaterialTagRelMapper.delete(new LambdaQueryWrapper<WeMaterialTagRelEntity>().eq(WeMaterialTagRelEntity::getMaterialTagId, tagId));
        //删除标签
        weMaterialTagMapper.deleteById(tagId);
    }

    /**
     * 新增标签
     *
     * @param weMaterialTagEntity 标签实体
     */
    @Override
    public void insertTag(WeMaterialTagEntity weMaterialTagEntity) {
        //校验CorpId
        StringUtils.checkCorpId(weMaterialTagEntity.getCorpId());
        //重名校验
        Integer nameCount = weMaterialTagMapper.selectCount(new LambdaQueryWrapper<WeMaterialTagEntity>()
                .eq(WeMaterialTagEntity::getTagName, weMaterialTagEntity.getTagName())
                .eq(WeMaterialTagEntity::getCorpId, weMaterialTagEntity.getCorpId())).intValue();
        if (nameCount > 0) {
            throw new CustomException(ResultTip.TIP_MATERIAL_COVER);
        }
        weMaterialTagMapper.insert(weMaterialTagEntity);
    }

    /**
     * 根据名称查询标签
     *
     * @param name   名称
     * @param corpId 企业id
     * @return {@link List<WeMaterialTagVO>}
     */
    @Override
    public List<WeMaterialTagVO> listByName(String name, String corpId) {
        StringUtils.checkCorpId(corpId);
        return weMaterialTagMapper.listByName(name, corpId);
    }

    /**
     * 查询素材列表具备的标签
     *
     * @param materialIdList 素材列表
     * @return {@link List<WeMaterialTagVO>}
     */
    @Override
    public List<WeMaterialTagVO> listChecked(List<Long> materialIdList) {
        if (CollectionUtils.isEmpty(materialIdList)) {
            return new ArrayList<>();
        }
        return weMaterialTagMapper.listChecked(materialIdList);
    }
}
