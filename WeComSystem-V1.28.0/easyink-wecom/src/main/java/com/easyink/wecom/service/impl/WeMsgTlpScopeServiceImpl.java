package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.WeMsgTlpScope;
import com.easyink.wecom.mapper.WeMsgTlpScopeMapper;
import com.easyink.wecom.service.WeMsgTlpScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板使用人员范围Service业务层处理
 *
 * @author admin
 * @date 2020-10-04
 */
@Service
public class WeMsgTlpScopeServiceImpl extends ServiceImpl<WeMsgTlpScopeMapper, WeMsgTlpScope> implements WeMsgTlpScopeService {
    private WeMsgTlpScopeService weMsgTlpScopeService;

    @Lazy
    @Autowired
    public WeMsgTlpScopeServiceImpl(WeMsgTlpScopeService weMsgTlpScopeService) {
        this.weMsgTlpScopeService = weMsgTlpScopeService;
    }

    /**
     * 插入好友欢迎语使用范围 增量新增
     *
     * @param defaultMsgId 默认欢迎语id
     * @param useUserIds   员工ids
     */
    @Override
    public void saveScopeBatch(Long defaultMsgId, List<String> useUserIds) {
        if (defaultMsgId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeMsgTlpScope> scopeBatchList = new ArrayList<>();
        for (String useUserId : useUserIds) {
            scopeBatchList.add(new WeMsgTlpScope(null, defaultMsgId, useUserId));
        }
        // 这里存在唯一索引,唯一索引存在则忽略，不存在则插入
        weMsgTlpScopeService.batchSaveOrUpdate(scopeBatchList);
    }

    /**
     * 修改欢迎语员工使用范围
     *
     * @param defaultMsgId 默认欢迎语id
     * @param useUserIds   员工ids
     */
    @Override
    public void updateScope(Long defaultMsgId, List<String> useUserIds) {
        if (defaultMsgId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 删除不在userIdList中的员工使用范围
        weMsgTlpScopeService.deleteNotInUserIds(defaultMsgId, useUserIds);
        // 增量新增
        weMsgTlpScopeService.saveScopeBatch(defaultMsgId, useUserIds);
    }

    /**
     * 批量更新
     *
     * @param scopeBatchList
     */
    @Override
    public void batchSaveOrUpdate(List<WeMsgTlpScope> scopeBatchList) {
        baseMapper.batchSaveOrUpdate(scopeBatchList);
    }

    /**
     * 删除不存在集合中的的记录
     *
     * @param defaultMsgId 欢迎语id
     * @param useUserIds   员工userIds
     */
    @Override
    public void deleteNotInUserIds(Long defaultMsgId, List<String> useUserIds) {
        baseMapper.deleteNotInUserIds(defaultMsgId, useUserIds);
    }
}
