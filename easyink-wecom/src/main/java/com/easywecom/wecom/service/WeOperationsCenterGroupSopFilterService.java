package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeOperationsCenterGroupSopFilterEntity;
import com.easywecom.wecom.domain.vo.sop.FindGroupSopFilterVO;

import java.util.List;

/**
 * 类名： 群SOP筛选群聊条件接口
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
public interface WeOperationsCenterGroupSopFilterService extends IService<WeOperationsCenterGroupSopFilterEntity> {

    /**
     * 根据corpId和sopIdList删除数据
     *
     * @param corpId    企业ID
     * @param sopIdList sopIdList
     */
    void delByCorpIdAndSopIdList(String corpId, List<Long> sopIdList);

    /**
     * 更新群筛选条件
     *
     * @param sopFilterEntity sopFilterEntity
     * @param sopType         SOP类型
     * @param cycleStart      循环开始时间
     * @param cycleEnd        循环结束时间
     */
    void updateGroupSopFilter(WeOperationsCenterGroupSopFilterEntity sopFilterEntity, Integer sopType, String cycleStart, String cycleEnd);

    /**
     * 根据corpId和sopId查询数据
     *
     * @param corpId  企业Id
     * @param sopId   sopId
     * @param sopType sop类型
     * @return FindGroupSopFilterVO
     */
    FindGroupSopFilterVO getDataBySopId(String corpId, Long sopId, Integer sopType);
}

