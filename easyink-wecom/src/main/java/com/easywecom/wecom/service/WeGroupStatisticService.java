package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeGroupStatistic;
import com.easywecom.wecom.domain.dto.WePageCountDTO;
import com.easywecom.wecom.domain.query.WePageStateQuery;

import java.util.List;

/**
 * 群聊数据统计数据
 * Service接口
 *
 * @author admin
 * @date 2021-02-24
 */
public interface WeGroupStatisticService extends IService<WeGroupStatistic> {

    /**
     * 查询列表
     */
    List<WeGroupStatistic> queryList(WeGroupStatistic weGroupStatistic);

    /**
     * 按日期查询指定时间的数据统计结果
     *
     * @param corpId    授权公司id
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return WePageCountDTO
     */
    WePageCountDTO getCountDataByDayNew(String corpId, String beginTime, String endTime);

    /**
     * 按天维度查询数据统计
     *
     * @param wePageStateQuery 入参
     * @return List<WeUserBehaviorDataDto>
     */
    List<WePageCountDTO> getDayCountData(WePageStateQuery wePageStateQuery);

    /**
     * 按周维度查询数据统计
     *
     * @param wePageStateQuery 入参
     * @return List<WeUserBehaviorDataDto>
     */
    List<WePageCountDTO> getWeekCountData(WePageStateQuery wePageStateQuery);

    /**
     * 按月维度查询数据统计
     *
     * @param wePageStateQuery 入参
     * @return List<WeUserBehaviorDataDto>
     */
    List<WePageCountDTO> getMonthCountData(WePageStateQuery wePageStateQuery);

}
