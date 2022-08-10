package com.easywecom.wecom.service;

import com.easywecom.wecom.domain.WeMaterialTagRelEntity;

import java.util.List;

/**
 * 类名： 素材标签联系接口
 *
 * @author 佚名
 * @date 2021/10/11 17:48
 */
public interface WeMaterialTagRelService {
    /**
     * 删除标签联系
     *
     * @param weMaterialTagRelEntities 标签关联实体列表
     */
    void delTagRels(List<WeMaterialTagRelEntity> weMaterialTagRelEntities);

    /**
     * 添加标签联系
     *
     * @param weMaterialTagRelEntities 素材联系列表
     */
    void addTagRels(List<WeMaterialTagRelEntity> weMaterialTagRelEntities);

    /**
     * 根据素材id删除联系
     *
     * @param materialIds 素材id
     * @param corpId 企业id
     */
    void delByMaterialId(List<Long> materialIds, String corpId);

}
