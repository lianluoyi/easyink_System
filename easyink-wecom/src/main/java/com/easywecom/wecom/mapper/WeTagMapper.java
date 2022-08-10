package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeTag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 企业微信标签Mapper接口
 *
 * @author admin
 * @date 2020-09-07
 */
@Repository
public interface WeTagMapper extends BaseMapper<WeTag> {
    /**
     * 查询企业微信标签
     *
     * @param id 企业微信标签ID
     * @return 企业微信标签
     */
    WeTag selectWeTagById(Long id);

    /**
     * 修改企业微信标签
     *
     * @param weTag 企业微信标签
     * @return 结果
     */
    int updateWeTag(WeTag weTag);

    /**
     * 删除企业微信标签
     * @deprecated 调用方法已弃用
     * @param groupIds 企业微信标签组Id
     * @return 结果
     */
    @Deprecated
    int deleteWeTagByGroupId(String[] groupIds);

    /**
     * 删除企业微信标签
     *
     * @param id 企业微信标签ID
     * @return 结果
     */
    int deleteWeTagById(@Param("id") String id, @Param("corpId") String corpId);

    /**
     * 批量插入/更新标签
     *
     * @param weTags 标签集合
     */
    Integer batchInsert(@Param("list") List<WeTag> weTags);
}
