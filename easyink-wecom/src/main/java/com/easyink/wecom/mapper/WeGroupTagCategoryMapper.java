package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeGroupTag;
import com.easyink.wecom.domain.WeGroupTagCategory;
import com.easyink.wecom.domain.dto.statistics.WeTagStatisticsDTO;
import com.easyink.wecom.domain.dto.wegrouptag.FindWeGroupTagCategoryDTO;
import com.easyink.wecom.domain.dto.wegrouptag.PageWeGroupTagCategoryDTO;
import com.easyink.wecom.domain.vo.statistics.WeTagGroupListVO;
import com.easyink.wecom.domain.vo.wegrouptag.PageWeGroupTagCategoryVO;
import com.easyink.wecom.domain.vo.wegrouptag.WeGroupTagCategoryVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名：WeGroupTagCategoryMapper
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:58
 */
@Repository
public interface WeGroupTagCategoryMapper extends BaseMapper<WeGroupTagCategory> {

    /**
     * 校验标签组是否重名
     *
     * @param corpId 企业ID
     * @param name   标签组名
     * @return boolean
     */
    boolean checkSameName(@Param("corpId") String corpId, @Param("name") String name);

    /**
     * 批量删除标签组
     *
     * @param corpId 企业ID
     * @param idList 标签组ID列表
     * @return int
     */
    int delByIdList(@Param("corpId") String corpId, @Param("idList") List<Long> idList);

    /**
     * 查询标签组列表
     *
     * @param weGroupTagCategory weGroupTagCategory
     * @return List<WeGroupTagCategoryVO>
     */
    List<WeGroupTagCategoryVO> list(FindWeGroupTagCategoryDTO weGroupTagCategory);

    /**
     * 分页查询标签列表
     *
     * @param weGroupTagCategory weGroupTagCategory
     * @return List<PageWeGroupTagCategoryVO>
     */
    List<PageWeGroupTagCategoryVO> page(PageWeGroupTagCategoryDTO weGroupTagCategory);

    /**
     * 根据id查询数据
     *
     * @param corpId 企业ID
     * @param id     标签组ID
     * @return WeGroupTagCategoryVO
     */
    WeGroupTagCategoryVO findById(@Param("corpId") String corpId, @Param("id") Long id);

    /**
     * 获取所需标签组列表
     *
     * @param weTagStatisticsDTO 查询条件
     * @return 标签组列表
     */
    List<WeGroupTagCategory> getTagCategoryList(WeTagStatisticsDTO weTagStatisticsDTO);

}
