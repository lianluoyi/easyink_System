package com.easyink.wecom.service;

import com.easyink.wecom.domain.dto.wegrouptag.*;
import com.easyink.wecom.domain.vo.wegrouptag.PageWeGroupTagCategoryVO;
import com.easyink.wecom.domain.vo.wegrouptag.WeGroupTagCategoryVO;

import java.util.List;

/**
 * 类名：WeGroupTagCategoryService
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:57
 */
public interface WeGroupTagCategoryService {

    /**
     * 新增群标签组
     *
     * @param weGroupTagCategoryDTO weGroupTagCategoryDTO
     * @return 群标签组信息 {@link WeGroupTagCategoryVO}
     */
    WeGroupTagCategoryVO add(AddWeGroupTagCategoryDTO weGroupTagCategoryDTO);

    /**
     * 编辑标签组
     *
     * @param weGroupTagCategoryDTO weGroupTagCategoryDTO
     * @return 群标签组信息 {@link WeGroupTagCategoryVO}
     */
    WeGroupTagCategoryVO update(UpdateWeGroupTagCategoryDTO weGroupTagCategoryDTO);

    /**
     * 删除标签组
     *
     * @param weGroupTagCategoryDTO weGroupTagCategoryDTO
     */
    void delete(DelWeGroupTagCategoryDTO weGroupTagCategoryDTO);

    /**
     * 查询标签组列表
     *
     * @param weGroupTagCategory weGroupTagCategory
     * @return List<WeGroupTagCategoryVO>
     */
    List<WeGroupTagCategoryVO> list(FindWeGroupTagCategoryDTO weGroupTagCategory);

    /**
     * 查询分页数据
     *
     * @param weGroupTagCategory weGroupTagCategory
     * @return List<PageWeGroupTagCategoryVO>
     */
    List<PageWeGroupTagCategoryVO> page(PageWeGroupTagCategoryDTO weGroupTagCategory);

    /**
     * 查询标签组详情
     *
     * @param weGroupTagCategory weGroupTagCategory
     * @return WeGroupTagCategoryVO
     */
    WeGroupTagCategoryVO findInfo(FindWeGroupTagCategoryDTO weGroupTagCategory);
}
