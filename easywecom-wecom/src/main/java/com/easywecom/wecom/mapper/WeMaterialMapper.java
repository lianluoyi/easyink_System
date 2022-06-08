package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeMaterial;
import com.easywecom.wecom.domain.dto.*;
import com.easywecom.wecom.domain.vo.WeMaterialCountVO;
import com.easywecom.wecom.domain.vo.WeMaterialVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 素材mapper
 *
 * @author admin
 * @date 2020-10-09
 */
@Repository
public interface WeMaterialMapper extends BaseMapper<WeMaterial> {


    /**
     * 添加素材信息
     *
     * @param weMaterial 素材信息
     * @return
     */
    int insertWeMaterial(AddWeMaterialDTO weMaterial);

    /**
     * 批量插入数据
     *
     * @param list 素材数据集合
     * @return {@link int}
     */
    int batchInsertWeMaterial(List<WeMaterial> list);

    /**
     * 更新素材信息
     *
     * @param weMaterial
     * @return
     */
    int updateWeMaterial(WeMaterial weMaterial);

    /**
     * 查询素材详细信息
     *
     * @param id id
     * @return {@link WeMaterial}
     */
    WeMaterial findWeMaterialById(Long id);

    /**
     * 查询素材列表
     *
     * @param findWeMaterialDTO 搜索条件
     * @return {@link WeMaterial}s
     */
    List<WeMaterialVO> findWeMaterials(FindWeMaterialDTO findWeMaterialDTO);

    /**
     * 更换分组
     *
     * @param categoryId 类目id
     * @param material   素材id
     * @return int
     */
    int resetCategory(@Param("categoryId") String categoryId, @Param("material") String material);

    /**
     * 根据id列表获取素材Vo列表
     *
     * @param ids 素材id列表
     * @return 结果
     */
    List<WeMaterialVO> findMaterialVoListByIds(Long[] ids);

    /**
     * 素材发布/批量发布
     *
     * @param showMaterialSwitchDTO showMaterialSwitchDTO
     */
    void showMaterialSwitch(ShowMaterialSwitchDTO showMaterialSwitchDTO);

    /**
     * 过期素材恢复
     *
     * @param restoreMaterialDTO restoreMaterialDTO
     */
    void restore(RestoreMaterialDTO restoreMaterialDTO);

    /**
     * 批量删除素材
     *
     * @param removeMaterialDTO removeMaterialDTO
     */
    void deleteByIdList(RemoveMaterialDTO removeMaterialDTO);

    /**
     * 根据corpId和idList删除素材
     *
     * @param corpId 企业ID
     * @param idList 素材列表
     */
    int deleteByIdListAndCorpId(@Param("corpId") String corpId, @Param("idList") List<Long> idList);

    /**
     * 查询已过期且超过最后期限时间的素材
     *
     * @param corpId         企业ID
     * @param lastRemoveDate 最后移除期限
     * @return List<Long>
     */
    List<Long> findExpireMaterialByLastExpireTime(@Param("corpId") String corpId, @Param("lastRemoveDate") Date lastRemoveDate);

    /**
     * 查询素材数量、发布到侧边栏数量
     *
     * @param findWeMaterialDTO 筛选条件
     * @return {@link WeMaterialCountVO}
     */
    WeMaterialCountVO getMaterialCount(FindWeMaterialDTO findWeMaterialDTO);

    /**
     * 根据materialSort排序查询素材详情
     *
     * @param materialSort 已排序的素材,如：xxx1,xxx2,xxx3
     * @param corpId       企业ID
     * @return List<AddWeMaterialDTO>
     */
    List<AddWeMaterialDTO> getListByMaterialSort(@Param("materialSort") String[] materialSort, @Param("corpId") String corpId);
}
