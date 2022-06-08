package com.easywecom.common.service;

import com.easywecom.common.core.domain.system.SysOperLog;

import java.util.List;

/**
 * 操作日志 服务层
 *
 * @author admin
 */
public interface ISysOperLogService {
    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    void insertOperlog(SysOperLog operLog);

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    List<SysOperLog> selectOperLogList(SysOperLog operLog);

    /**
     * 批量删除系统操作日志
     *
     * @param corpId  公司ID
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    int deleteOperLogByIds(String corpId, Long[] operIds);

    /**
     * 清空操作日志
     *
     * @param corpId 公司ID
     */
    void cleanOperLog(String corpId);
}
