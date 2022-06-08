package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeCategory;
import com.easywecom.wecom.domain.dto.WeCategorySidebarSwitchDTO;
import com.easywecom.wecom.domain.vo.WeCategoryBaseInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WeCategoryMapper extends BaseMapper<WeCategory> {

    /**
     * 根据corpId和ids删除记录
     *
     * @param corpId 企业ID
     * @param ids    需要删除的记录ID
     */
    void deleteWeCategoryById(@Param("corpId") String corpId, @Param("ids") Long[] ids);

    /**
     * 批量插入数据
     *
     * @param list list
     * @return int
     */
    int batchInsert(List<WeCategory> list);

    /**
     * 根据corpId和mediaType 查询数据
     *
     * @param corpId        企业ID
     * @param mediaTypeList 素材类型
     * @return List<WeCategory>
     */
    List<WeCategory> selectByCorpIdAndMediaType(@Param("corpId") String corpId, @Param("mediaTypeList") List<Integer> mediaTypeList);

    /**
     * 启用到侧边栏开关
     *
     * @param sidebarSwitchDTO sidebarSwitchDTO
     */
    void updateSidebarSwitch(WeCategorySidebarSwitchDTO sidebarSwitchDTO);

    /**
     * 根据corpId和using查询素材类型
     *
     * @param corpId 企业ID
     * @param using  是否展示到侧边栏
     * @return List<WeCategoryBaseInfoVO>
     */
    List<WeCategoryBaseInfoVO> selectByCorpIdAndUsing(@Param("corpId") String corpId, @Param("using") Boolean using);
}
