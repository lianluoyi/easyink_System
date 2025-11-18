package com.easyink.wecom.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.domain.dto.statistics.EmpleCodeStatisticDTO;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeBaseVO;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeDateVO;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeUserVO;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 活码统计Mapper接口
 *
 * @author lichaoyu
 * @date 2023/7/5 14:35
 */
@Repository
@Mapper
public interface WeEmpleCodeStatisticMapper extends BaseMapper<WeEmpleCodeStatistic> {

    /**
     * 活码统计-数据总览
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeBaseVO}
     */
    EmpleCodeBaseVO listEmpleTotal(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-员工维度-获取基础数据
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeUserVO}
     */
    List<EmpleCodeUserVO> listEmpleUser(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-员工维度-获取截止当前时间每个员工对应的有效客户数
     *
     * @param corpId             企业ID
     * @param externalUserIdList 客户ID列表
     * @param userIds            员工ID列表
     * @param time               时间 格式为 YYYY-MM-DD HH:MM:SS
     * @param position
     * @return {@link EmpleCodeUserVO}
     */
    List<EmpleCodeUserVO> listUserCustomerRel(@Param("corpId") String corpId, @Param("externalUserIdList") List<String> externalUserIdList, @Param("userIds") List<String> userIds, @Param("time") String time, @Param("beginTime") String beginTime, @Param("position") String position);

    /**
     * 活码统计-活码维度-获取基础数据
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeUserVO}
     */
    List<EmpleCodeVO> listEmple(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-活码维度-获取截止当前时间每个活码对应的有效客户数
     *
     * @param corpId          企业ID
     * @param empleCodeIdList 活码ID列表
     * @param userIds         员工ID列表
     * @param time            时间 格式为 YYYY-MM-DD HH:MM:SS
     * @param position
     * @return {@link EmpleCodeUserVO}
     */
    List<EmpleCodeVO> listStateUserCustomerRel(@Param("corpId") String corpId, @Param("empleCodeIdList") List<String> empleCodeIdList, @Param("userIds") List<String> userIds, @Param("time") String time, @Param("beginTime") String beginTime, @Param("position") String position);

    /**
     * 活码统计-日期维度-获取基础数据
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeDateVO}
     */
    List<EmpleCodeDateVO> listEmpleDate(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-日期维度-获取每个日期下的留存客户数和累计客户数
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeDateVO}
     */
    List<EmpleCodeDateVO> listDateAccumulateRetainCnt(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-日期维度-获取截止当前时间每个日期对应的有效客户数
     *
     * @param corpId             企业ID
     * @param externalUserIdList 客户id列表
     * @param userIds            员工ID列表
     * @param time               时间 格式为 YYYY-MM-DD HH:MM:SS
     * @param position 职务过滤
     * @return {@link EmpleCodeUserVO}
     */
    List<EmpleCodeDateVO> listEmpleDateUserCustomerRel(@Param("corpId") String corpId, @Param("externalUserIdList") List<String> externalUserIdList, @Param("userIds") List<String> userIds, @Param("time") String time, @Param("position") String position);

    /**
     * 批量插入或更新统计数据
     *
     * @param list 统计数据
     * @return 结果
     */
    Integer batchInsertOrUpdate(@Param("list") List<WeEmpleCodeStatistic> list);

    /**
     * 渠道统计列表客户关系统计数
     * @param corpId 企业id
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param stateList state列表
     * @param userIds 员工id
     * @param position 职位过滤
     * @return
     */
    int listUserCustomerRelTotal(@Param("corpId") String corpId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("stateList") List<String> stateList, @Param("userIds") List<String> userIds, @Param("position") String position);
}

