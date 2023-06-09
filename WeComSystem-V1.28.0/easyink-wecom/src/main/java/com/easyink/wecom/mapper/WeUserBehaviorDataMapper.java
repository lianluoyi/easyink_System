package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeUserBehaviorData;
import com.easyink.wecom.domain.dto.WePageCountDTO;
import com.easyink.wecom.domain.dto.statistics.CustomerOverviewDTO;
import com.easyink.wecom.domain.dto.statistics.StatisticsDTO;
import com.easyink.wecom.domain.query.WePageStateQuery;
import com.easyink.wecom.domain.vo.statistics.CustomerOverviewVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 联系客户统计数据 Mapper接口
 *
 * @author admin
 * @date 2021-02-24
 */
@Repository
public interface WeUserBehaviorDataMapper extends BaseMapper<WeUserBehaviorData> {


    /**
     * 批量更新员工主动发起会话数接口
     *
     * @param weUserBehaviorDataList {@link WeUserBehaviorData}
     * @return 结果
     */
    int batchUpdate(@Param("list") List<WeUserBehaviorData> weUserBehaviorDataList);

    /**
     * 按日期查询当天数据统计结果
     *
     * @param corpId    授权公司id
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return WeUserBehaviorDataDto
     */
    WePageCountDTO getCountDataByDayNew(@Param("corpId") String corpId, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

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

    /**
     * 获取群聊总数和 群成员总数的每周数据统计
     *
     * @param wePageStateQuery 入参
     * @return
     */
    List<WePageCountDTO> getGroupCntWeekDate(WePageStateQuery wePageStateQuery);

    /**
     * 获取客户概况-数据总览
     *
     * @param dto   {@link StatisticsDTO}
     * @return
     */
    CustomerOverviewVO getCustomerOverViewOfTotal(StatisticsDTO dto);

    /**
     * 获取客户概况-数据总览-员工维度
     *
     * @param dto   {@link CustomerOverviewDTO}
     * @return
     */
    List<CustomerOverviewVO> getCustomerOverViewOfUser(CustomerOverviewDTO dto);

    /**
     * 获取客户概况-数据总览-日期维度
     *
     * @param dto {@link CustomerOverviewDTO}
     * @return 原始数据列表
     */
    List<WeUserBehaviorData> getCustomerOverViewOfDate(CustomerOverviewDTO dto);
}
