package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.dto.statistics.*;
import com.easyink.wecom.domain.entity.WeUserCustomerMessageStatistics;
import com.easyink.wecom.domain.vo.statistics.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 员工客户发送消息统计数据（每天统计一次，会话存档ES中统计）(WeUserCustomerMessageStatistics)表数据库访问层
 *
 * @author wx
 * @since 2023-02-13 09:32:48
 */
public interface WeUserCustomerMessageStatisticsMapper extends BaseMapper<WeUserCustomerMessageStatistics>{

    /**
     * 查询历史日期范围下员工主动发起会话数
     *
     * @param corpId 企业ID
     * @return 结果
     */
    List<WeUserCustomerMessageStatistics> findHistoryData(@Param("corpId") String corpId);

    /**
     * 获取员工服务-数据总览-员工维度
     *
     * @param dto   {@link UserServiceDTO}
     * @return {@link UserServiceVO}
     */
    List<UserServiceVO> getUserServiceOfUser(UserServiceDTO dto);

    /**
     * 获取员工服务-数据总览
     *
     * @param dto   {@link UserServiceDTO}
     * @return  {@link UserServiceVO}
     */
    UserServiceVO getUserServiceOfTotal(StatisticsDTO dto);

    /**
     * 获取员工服务-数据详情-时间维度
     *
     * @param dto {@link UserServiceDTO}
     * @return {@link UserServiceTimeDTO}
     */
    List<UserServiceTimeDTO> getUserServiceOfTime(UserServiceDTO dto);

    /**
     * 获取客户活跃度-日期维度
     *
     * @param dto   {@link CustomerActivityDTO}
     * @param userIdList 员工id列表
     * @return  {@link ChatMessageCntVO}
     */
    CustomerActivityOfDateVO getCustomerActivityOfDate(@Param("dto") CustomerActivityDTO dto, @Param("userIdList") List<String> userIdList);

    /**
     * 获取客户活跃度-日期维度-趋势
     *
     * @param dto   {@link CustomerActivityDTO}
     * @return  {@link SendMessageCntVO}
     */
    List<SendMessageCntVO> getCustomerActivityOfDateTrend(CustomerActivityDTO dto);

    /**
     * 获取客户活跃度-员工维度
     *
     * @param dto        {@link CustomerActivityDTO}
     * @param userIdList 员工id列表
     * @return @link CustomerActivityOfUserVO}
     */
    List<CustomerActivityOfUserVO> getCustomerActivityOfUser(@Param("dto") CustomerActivityDTO dto, @Param("userIdList") List<String> userIdList);

    /**
     * 获取客户活跃度-客户维度
     *
     * @param dto        {@link CustomerActivityDTO}
     * @param userIdList 员工id列表
     * @return @link CustomerActivityOfCustomerVO}
     */
    List<CustomerActivityOfCustomerVO> getCustomerActivityOfCustomer(@Param("dto") CustomerActivityDTO dto, @Param("userIdList") List<String> userIdList);

    /**
     * 获取客户活跃度-员工维度-详情
     *
     * @param dto   {@link CustomerActivityUserDetailDTO}
     * @return  {@link CustomerActivityOfCustomerVO}
     */
    List<CustomerActivityOfCustomerVO> getCustomerActivityOfUserDetail(CustomerActivityUserDetailDTO dto);

    /**
     * 获取单个员工有对话的数据
     *
     * @param corpId     企业id
     * @param userIdList 员工id列表
     * @param beginTime  开始时间，格式YYYY-MM-DD
     * @param endTime    结束时间，格式YYYY-MM-DD
     * @return {@link UserServiceTimeDTO}
     */
    List<UserServiceTimeDTO> getFilterOfUser(@Param("corpId") String corpId, @Param("list") List<String> userIdList, @Param("beginTime") String beginTime, @Param("endTime") String endTime);
}

