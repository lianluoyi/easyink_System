package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.dto.statistics.*;
import com.easyink.wecom.domain.entity.WeUserCustomerMessageStatistics;
import com.easyink.wecom.domain.vo.statistics.*;

import java.util.List;


/**
 * 员工客户发送消息统计数据（每天统计一次，会话存档ES中统计）(WeUserCustomerMessageStatistics)表数据库访问层
 *
 * @author wx
 * @since 2023-02-13 09:32:48
 */
public interface WeUserCustomerMessageStatisticsMapper extends BaseMapper<WeUserCustomerMessageStatistics>{

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
     * @return  {@link ChatMessageCntVO}
     */
    List<CustomerActivityOfDateVO> getCustomerActivityOfDate(CustomerActivityDTO dto);

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
     * @param dto   {@link CustomerActivityDTO}
     * @return  @link CustomerActivityOfUserVO}
     */
    List<CustomerActivityOfUserVO> getCustomerActivityOfUser(CustomerActivityDTO dto);

    /**
     * 获取客户活跃度-客户维度
     *
     * @param dto   {@link CustomerActivityDTO}
     * @return  @link CustomerActivityOfCustomerVO}
     */
    List<CustomerActivityOfCustomerVO> getCustomerActivityOfCustomer(CustomerActivityDTO dto);

    /**
     * 获取客户活跃度-员工维度-详情
     *
     * @param dto   {@link CustomerActivityUserDetailDTO}
     * @return  {@link CustomerActivityOfCustomerVO}
     */
    List<CustomerActivityOfCustomerVO> getCustomerActivityOfUserDetail(CustomerActivityUserDetailDTO dto);

    /**
     *  获取有进行对话的员工
     * @param dto {@link UserServiceDTO}
     * @return {@link UserServiceTimeDTO}
     */
    List<UserServiceTimeDTO> getFilterOfUser(UserServiceDTO dto);
}

