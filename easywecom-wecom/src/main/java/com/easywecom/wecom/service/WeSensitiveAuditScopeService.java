package com.easywecom.wecom.service;

import com.easywecom.wecom.domain.WeSensitiveAuditScope;

import java.util.List;

/**
 * 敏感词审计范围Service接口
 *
 * @author admin
 * @date 2020-12-29
 */
public interface WeSensitiveAuditScopeService {
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
     * 批量新增
     *
     * @param weSensitiveAuditScopeList
     * @return
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
     * 批量删除敏感词审计范围
     *
     * @param ids 需要删除的敏感词审计范围ID
     * @return 结果
     */
    int deleteWeSensitiveAuditScopeByIds(Long[] ids);

    /**
     * 删除敏感词审计范围信息
     *
     * @param id 敏感词审计范围ID
     * @return 结果
     */
    int deleteWeSensitiveAuditScopeById(Long id);

    int deleteAuditScopeBySensitiveId(Long sensitiveId);

    int deleteAuditScopeBySensitiveIds(Long[] sensitiveIds);
}
