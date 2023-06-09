package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeGroupStatistic;
import com.easyink.wecom.domain.dto.WePageCountDTO;
import com.easyink.wecom.domain.query.WePageStateQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 群聊数据统计数据
 * Mapper接口
 *
 * @author admin
 * @date 2021-02-24
 */
@Repository
public interface WeGroupStatisticMapper extends BaseMapper<WeGroupStatistic> {

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
     * 获取群成员统计总数
     *
     * @param corpId    企业id
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 群成员总数
     */
    Integer getGroupMemberCnt(@Param("corpId") String corpId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);


    /**
     * 获取群聊总数和 群成员总数的每周数据统计
     *
     * @param wePageStateQuery 入参
     * @return
     */
    List<WePageCountDTO> getGroupCntWeekDate(WePageStateQuery wePageStateQuery);

    /**
     * 获取群聊总数和群成员的每月数据统计 （当月的截止至 date )
     *
     * @param date 昨日日期
     * @param corpId    企业id
     * @return
     */
    List<WePageCountDTO> getMonthChatCntDate(@Param("date") Date date, @Param("corpId") String corpId);
}
