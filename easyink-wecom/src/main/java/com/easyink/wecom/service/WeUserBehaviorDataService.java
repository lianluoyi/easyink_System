package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeUserBehaviorData;
import com.easyink.wecom.domain.dto.WePageCountDTO;
import com.easyink.wecom.domain.dto.statistics.CustomerActivityDTO;
import com.easyink.wecom.domain.dto.statistics.StatisticsDTO;
import com.easyink.wecom.domain.query.WePageStateQuery;
import com.easyink.wecom.domain.vo.statistics.CustomerOverviewVO;

import java.util.List;

/**
 * 联系客户统计数据 Service接口
 *
 * @author admin
 * @date 2021-02-24
 */
public interface WeUserBehaviorDataService extends IService<WeUserBehaviorData> {

    /**
     * 查询列表
     */
    List<WeUserBehaviorData> queryList(WeUserBehaviorData weUserBehaviorData);

    /**
     * 按日期查询当天数据统计结果
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
