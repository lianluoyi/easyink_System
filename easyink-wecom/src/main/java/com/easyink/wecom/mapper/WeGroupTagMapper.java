package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeGroupTag;
import com.easyink.wecom.domain.vo.wegrouptag.WeGroupTagRelDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名：WeGroupTagMapper
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:58
 */
@Repository
public interface WeGroupTagMapper extends BaseMapper<WeGroupTag> {

    /**
     * 批量插入
     *
     * @param list list
     * @return int
     */
    int batchInsert(@Param("list") List<WeGroupTag> list);

    /**
     * 批量删除标签
     *
     * @param corpId 企业ID
     * @param idList 需要删除的标签列表
     * @return int
     */
    int delTag(@Param("corpId") String corpId, @Param("list") List<Long> idList);

    /**
     * 根据标签组ID批量删除标签列表
     *
     * @param corpId      企业ID
     * @param groupIdList 标签组ID
     * @return int
     */
    int delByGroupId(@Param("corpId") String corpId, @Param("groupIdList") List<Long> groupIdList);

    /**
     * 根据groupId查询标签列表
     *
     * @param corpId  企业ID
     * @param groupId 标签组ID
     * @return List<WeGroupTag>
     */
    List<WeGroupTag> findByGroupId(@Param("corpId") String corpId, @Param("groupId") Long groupId);

    /**
     * 根据标签组ID查询标签数据
     *
     * @param corpId  企业ID
     * @param groupId 标签组ID
     * @return List<WeGroupTagRelDetail>
     */
    List<WeGroupTagRelDetail> findPageByGroupId(@Param("corpId") String corpId, @Param("groupId") Long groupId);

    /**
     * 根据groupIdList查询tagIdList
     *
     * @param corpId      企业ID
     * @param groupIdList 标签组ID集合
     * @return List<Long>
     */
    List<Long> getTagIdByGroupId(@Param("corpId") String corpId, @Param("groupIdList") List<Long> groupIdList);
}
