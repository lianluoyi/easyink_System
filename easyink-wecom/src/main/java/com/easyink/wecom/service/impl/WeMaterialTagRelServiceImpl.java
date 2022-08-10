package com.easyink.wecom.service.impl;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeMaterialTagRelEntity;
import com.easyink.wecom.mapper.WeMaterialTagRelMapper;
import com.easyink.wecom.service.WeMaterialTagRelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类名： WeMaterialTagRelServiceImpl
 *
 * @author 佚名
 * @date 2021/10/11 19:20
 */
@Service
@Slf4j
public class WeMaterialTagRelServiceImpl implements WeMaterialTagRelService {
    private final WeMaterialTagRelMapper weMaterialTagRelMapper;

    @Autowired
    public WeMaterialTagRelServiceImpl(WeMaterialTagRelMapper weMaterialTagRelMapper) {
        this.weMaterialTagRelMapper = weMaterialTagRelMapper;
    }


    /**
     * 删除标签联系
     *
     * @param weMaterialTagRelEntities 标签关联实体列表
     */
    @Override
    public void delTagRels(List<WeMaterialTagRelEntity> weMaterialTagRelEntities) {
        weMaterialTagRelMapper.deleteBatchByEntities(weMaterialTagRelEntities);
    }

    /**
     * 添加标签联系
     *
     * @param weMaterialTagRelEntities 素材关联列表
     */
    @Override
    public void addTagRels(List<WeMaterialTagRelEntity> weMaterialTagRelEntities) {
        weMaterialTagRelMapper.batchInsert(weMaterialTagRelEntities);
    }

    /**
     * 根据素材id删除联系
     *
     * @param materialIds 素材id
     */
    @Override
    public void delByMaterialId(List<Long> materialIds, String corpId) {
        if (CollectionUtils.isEmpty(materialIds)) {
            throw new CustomException(ResultTip.TIP_MISS_MATERIAL_ID);
        }
        //校验CorpId
        StringUtils.checkCorpId(corpId);
        weMaterialTagRelMapper.deleteBatchByMaterialId(materialIds, corpId);
    }
}
