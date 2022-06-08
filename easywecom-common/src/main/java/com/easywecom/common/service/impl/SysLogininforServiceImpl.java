package com.easywecom.common.service.impl;

import com.easywecom.common.core.domain.system.SysLogininfor;
import com.easywecom.common.mapper.SysLogininforMapper;
import com.easywecom.common.service.ISysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author admin
 */
@Service
public class SysLogininforServiceImpl implements ISysLogininforService {

    @Autowired
    private SysLogininforMapper logininforMapper;

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininfor logininfor) {
        logininforMapper.insertLogininfor(logininfor);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor) {
        return logininforMapper.selectLogininforList(logininfor);
    }

    /**
     * 批量删除系统登录日志
     *
     *
     * @param corpId
     * @param infoIds 需要删除的登录日志ID
     * @return
     */
    @Override
    public int deleteLogininforByIds(String corpId, Long[] infoIds) {
        return logininforMapper.deleteLogininforByIds(corpId,infoIds);
    }

    /**
     * 清空系统登录日志
     * @param corpId
     */
    @Override
    public void cleanLogininfor(String corpId) {
        logininforMapper.deleteLogininforByIds(corpId,null);
    }
}
