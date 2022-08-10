package com.easyink.wecom.service;

import com.easyink.wecom.domain.WeMaterialTagEntity;
import com.easyink.wecom.domain.vo.WeMaterialTagVO;

import java.util.List;

/**
 * 类名： 素材标签接口
 *
 * @author 佚名
 * @date 2021/10/11 17:24
 */
public interface WeMaterialTagService {
    /**
     * 批量打标签
     *
     * @param materialIds 素材id列表
     * @param tagIds      标签id列表
     */
    void markTags(List<Long> materialIds, List<Long> tagIds);

    /**
     * 批量移除标签
     *
     * @param tagIds 标签id列表
     * @param materialIds 素材id列表
     */
    void removeTags(List<Long> tagIds, List<Long> materialIds);

    /**
     * 删除标签
     *
     * @param tagId 标签id
     */
    void delTag(Long tagId);

    /**
     * 新增标签
     *
     * @param weMaterialTagEntity 标签实体
     */
    void insertTag(WeMaterialTagEntity weMaterialTagEntity);

    /**
     * 根据名称查询标签
     *
     * @param name   名称
     * @param corpId 企业id
     * @return {@link List<WeMaterialTagVO>}
     */
    List<WeMaterialTagVO> listByName(String name, String corpId);

    /**
     * 查询素材列表具备的标签
     *
     * @param materialIdList 素材列表
     * @return {@link List<WeMaterialTagVO>}
     */
    List<WeMaterialTagVO> listChecked(List<Long> materialIdList);



}
