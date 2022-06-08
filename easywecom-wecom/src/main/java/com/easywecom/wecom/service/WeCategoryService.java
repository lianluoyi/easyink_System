package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.common.core.domain.Tree;
import com.easywecom.wecom.domain.WeCategory;
import com.easywecom.wecom.domain.dto.WeCategorySidebarSwitchDTO;
import com.easywecom.wecom.domain.vo.WeCategoryBaseInfoVO;

import java.util.List;

public interface WeCategoryService extends IService<WeCategory> {

    /**
     * 保存素材分类
     *
     * @param category 素材分类 信息
     */
    void insertWeCategory(WeCategory category);

    /**
     * 保存素材分类
     *
     * @param category 素材分类 信息
     */
    void updateWeCategory(WeCategory category);


    /**
     * 通过类型查询对应类目的分类信息列表
     *
     * @param corpId    企业ID
     * @param mediaType 类型
     * @return {@link WeCategory}s
     */
    List<? extends Tree<?>> findWeCategoryByMediaType(String corpId, Integer mediaType);

    /**
     * 根据corpId和ids删除素材分组
     *
     * @param corpId 企业ID
     * @param ids    需要删除的记录ID
     */
    void deleteWeCategoryById(String corpId, Long[] ids);

    /**
     * 初始化素材库分组
     *
     * @param corpId 授权企业ID
     * @return Boolean
     */
    Boolean initCategory(String corpId);

    /**
     * 初始化素材库分组
     *
     * @param corpId   授权企业ID
     * @param createBy 创建人
     * @return Boolean
     */
    Boolean initCategory(String corpId, String createBy);

    /**
     * 启用到侧边栏开关
     *
     * @param sidebarSwitchDTO sidebarSwitchDTO
     */
    void sidebarSwitch(WeCategorySidebarSwitchDTO sidebarSwitchDTO);

    /**
     * 查询可展示在侧边栏的素材类型
     *
     * @param corpId 企业ID
     * @return List<WeCategoryBaseInfoVO>
     */
    List<WeCategoryBaseInfoVO> findShowWeCategory(String corpId);

    /**
     * 查询corpId下的素材类型列表
     *
     * @param corpId 企业ID
     * @return List<WeCategoryBaseInfoVO>
     */
    List<WeCategoryBaseInfoVO> findListByCorpId(String corpId);
}
