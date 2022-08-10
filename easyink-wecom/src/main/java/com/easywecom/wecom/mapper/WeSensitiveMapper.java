package com.easywecom.wecom.mapper;


import com.easywecom.wecom.domain.WeSensitive;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 敏感词设置Mapper接口
 *
 * @author admin
 * @date 2020-12-29
 */
@Repository
public interface WeSensitiveMapper {
    /**
     * 查询敏感词设置
     *
     * @param id 敏感词设置ID
     * @return 敏感词设置
     */
    WeSensitive selectWeSensitiveById(Long id);

    List<WeSensitive> selectWeSensitiveByIds(Long[] ids);

    /**
     * 查询敏感词设置列表
     *
     * @param weSensitive 敏感词设置
     * @return 敏感词设置集合
     */
    List<WeSensitive> selectWeSensitiveList(WeSensitive weSensitive);

    /**
     * 新增敏感词设置
     *
     * @param weSensitive 敏感词设置
     * @return 结果
     */
    int insertWeSensitive(WeSensitive weSensitive);

    /**
     * 修改敏感词设置
     *
     * @param weSensitive 敏感词设置
     * @return 结果
     */
    int updateWeSensitive(WeSensitive weSensitive);

    int batchUpdateWeSensitive(List<WeSensitive> weSensitiveList);

    /**
     * 删除敏感词设置
     *
     * @param id 敏感词设置ID
     * @return 结果
     */
    int deleteWeSensitiveById(Long id);

    /**
     * 批量删除敏感词设置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteWeSensitiveByIds(Long[] ids);
}
