package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeOperationsCenterGroupSopFilterCycleEntity;

import java.util.List;

/**
 * 类名： 群SOP筛选群聊条件接口
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
public interface WeOperationsCenterGroupSopFilterCycleService extends IService<WeOperationsCenterGroupSopFilterCycleEntity> {

    /**
     * 根据corpId和sopIdList删除数据
     *
     * @param corpId    企业ID
     * @param sopIdList sopIdList
     */
    void delByCorpIdAndSopIdList(String corpId, List<Long> sopIdList);

    /**
     * 保存或更新数据
     *
     * @param corpId     企业ID
     * @param sopId      sopId
     * @param cycleStart 循环起始时间
     * @param cycleEnd   循环结束时间
     */
    void saveOrUpdateSopFilterCycle(String corpId, Long sopId, String cycleStart, String cycleEnd);

    /**
     * 根据sopId查询数据
     *
     * @param corpId 企业ID
     * @param sopId  sopId
     * @return WeOperationsCenterGroupSopFilterCycleEntity
     */
    WeOperationsCenterGroupSopFilterCycleEntity getDataBySopId(String corpId, Long sopId);
}

