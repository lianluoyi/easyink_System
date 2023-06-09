package com.easyink.wecom.mapper;

import com.easyink.wecom.domain.WeSensitiveAuditScope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 敏感词审计范围Mapper接口
 *
 * @author admin
 * @date 2020-12-29
 */
@Repository
public interface WeSensitiveAuditScopeMapper {
    /**
     * 查询敏感词审计范围
     *
     * @param id 敏感词审计范围ID
     * @return 敏感词审计范围
     */
    WeSensitiveAuditScope selectWeSensitiveAuditScopeById(Long id);

    /**
     * 查询敏感词审计范围列表
     *
     * @param weSensitiveAuditScope 敏感词审计范围
     * @return 敏感词审计范围集合
     */
    List<WeSensitiveAuditScope> selectWeSensitiveAuditScopeList(WeSensitiveAuditScope weSensitiveAuditScope);

    /**
     * 新增敏感词审计范围
     *
     * @param weSensitiveAuditScope 敏感词审计范围
     * @return 结果
     */
    int insertWeSensitiveAuditScope(WeSensitiveAuditScope weSensitiveAuditScope);

    /**
     * 批量新增敏感词审计范围
     *
     * @param weSensitiveAuditScopeList 敏感词审计范围List
     * @return 结果
     */
    int insertWeSensitiveAuditScopeList(List<WeSensitiveAuditScope> weSensitiveAuditScopeList);

    /**
     * 修改敏感词审计范围
     *
     * @param weSensitiveAuditScope 敏感词审计范围
     * @return 结果
     */
    int updateWeSensitiveAuditScope(WeSensitiveAuditScope weSensitiveAuditScope);

    /**
     * 删除敏感词审计范围
     *
     * @param id 敏感词审计范围ID
     * @return 结果
     */
    int deleteWeSensitiveAuditScopeById(Long id);

    /**
     * 批量删除敏感词审计范围
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteWeSensitiveAuditScopeByIds(Long[] ids);

    /**
     * 根据敏感词表id删除关联数据
     *
     * @param sensitiveId
     * @return
     */
    int deleteAuditScopeBySensitiveId(Long sensitiveId);

    int deleteAuditScopeBySensitiveIds(Long[] sensitiveIds);
}
