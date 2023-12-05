package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.wecom.domain.WeUserBehaviorData;
import com.easyink.wecom.domain.dto.statistics.*;
import com.easyink.wecom.domain.entity.WeUserCustomerMessageStatistics;
import com.easyink.wecom.domain.vo.ConversationArchiveVO;
import com.easyink.wecom.domain.vo.statistics.*;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 员工客户发送消息统计数据（每天统计一次，会话存档ES中统计）(WeUserCustomerMessageStatistics)表服务接口
 *
 * @author wx
 * @since 2023-02-13 09:32:50
 */
public interface WeUserCustomerMessageStatisticsService extends IService<WeUserCustomerMessageStatistics> {

    /**
     * 更新历史中员工主动发起的会话数
     *
     */
    void updateUserActiveChatCnt();

    /**
     * 员工会话消息统计
     *
     * @param corpId    企业id
     */
    void getMessageStatistics(String corpId, String time);

    /**
     * 统计员工会话数据
     *
     * @param userMessages     {@link ConversationArchiveVO}
     * @param weUser           {@link WeUser}
     * @param userBehaviorData {@link WeUserBehaviorData}
     * @param time 日期
     */
    void statistics(List<ConversationArchiveVO> userMessages, WeUser weUser, WeUserBehaviorData userBehaviorData, String time);

    /**
     * 获取客户概况-数据概览
     *
     * @param dto   {@link StatisticsDTO}
     * @return  {@link CustomerOverviewVO}
     */
    CustomerOverviewVO getCustomerOverViewOfTotal(StatisticsDTO dto);

    /**
     * 获取客户概况-员工情况
     *
     * @param dto {@link StatisticsDTO}
     * @return {@link CustomerOverviewVO}
     */
    List<CustomerOverviewVO> getCustomerOverViewOfUser(CustomerOverviewDTO dto);

    /**
     * 获取客户概况-日期维度
     *
     * @param dto      {@link CustomerOverviewDTO}
     * @param pageFlag 是否分页，true 是， false 否
     * @return 结果VO {@link CustomerOverviewDateVO}
     */
    List<CustomerOverviewDateVO> getCustomerOverViewOfDate(CustomerOverviewDTO dto, Boolean pageFlag);

    /**
     * 导出客户概况-数据总览-日期维度
     *
     * @param dto   {@link CustomerOverviewDTO}
     * @return 结果
     */
    AjaxResult exportCustomerOverViewOfDate(CustomerOverviewDTO dto);

    /**
     * 获取客户活跃度-日期维度
     *
     * @param dto   {@link CustomerActivityDTO}
     * @param pageFlag  是否分页
     * @return
     */
    List<CustomerActivityOfDateVO> getCustomerActivityOfDate(CustomerActivityDTO dto, Boolean pageFlag);

    /**
     * 获取客户活跃度-日期维度-趋势图
     *
     * @param dto   {@link CustomerActivityDTO}
     * @return
     */
    List<SendMessageCntVO> getCustomerActivityOfDateTrend(CustomerActivityDTO dto);


    /**
     * 获取客户活跃度-员工维度
     *
     * @param dto   {@link CustomerActivityDTO}
     * @param pageFlag  是否分页
     * @return
     */
    List<CustomerActivityOfUserVO> getCustomerActivityOfUser(CustomerActivityDTO dto, Boolean pageFlag);

    /**
     * 获取客户活跃度-客户维度
     *
     * @param dto {@link CustomerActivityDTO}
     * @param pageFlag  是否分页
     * @return
     */
    List<CustomerActivityOfCustomerVO> getCustomerActivityOfCustomer(CustomerActivityDTO dto, boolean pageFlag);

    /**
     * 获取客户活跃度-员工维度-客户详情
     *
     * @param dto       {@link CustomerActivityUserDetailDTO}
     * @return
     */
    List<CustomerActivityOfCustomerVO> getCustomerActivityOfUserDetail(CustomerActivityUserDetailDTO dto);

    /**
     * 员工服务-数据总览
     *
     * @param dto   {@link StatisticsDTO}
     * @return
     */
    UserServiceVO getUserServiceOfTotal(StatisticsDTO dto);

    /**
     * 员工服务-员工维度
     *
     * @param dto {@link StatisticsDTO}
     * @param pageFlag  是否分页
     * @return
     */
    PageInfo<UserServiceVO> getUserServiceOfUser(UserServiceDTO dto);

    /**
     * 导出客户概况-数据总览-员工维度
     *
     * @param dto   {@link CustomerOverviewDTO}
     * @return
     */
    AjaxResult exportCustomerOverViewOfUser(CustomerOverviewDTO dto);

    /**
     * 导出客户活跃度-日期维度
     *
     * @param dto   {@link CustomerActivityDTO}
     * @return
     */
    AjaxResult exportCustomerActivityOfDate(CustomerActivityDTO dto);

    /**
     * 导出客户活跃度-员工维度
     *
     * @param dto   {@link CustomerActivityDTO}
     * @return
     */
    AjaxResult exportCustomerActivityOfUser(CustomerActivityDTO dto);


    /**
     * 导出客户活跃度-客户维度
     *
     * @param dto   {@link CustomerActivityDTO}
     * @return
     */
    AjaxResult exportCustomerActivityOfCustomer(CustomerActivityDTO dto);

    /**
     * 导出员工服务-数据总览-员工维度
     *
     * @param dto   {@link UserServiceDTO}
     * @return
     */
    AjaxResult exportUserServiceOfUser(UserServiceDTO dto);

    /**
     * 获取员工服务-数据详情-日期维度
     *
     * @param dto {@link UserServiceDTO}
     * @return
     */
    List<UserServiceTimeVO> getUserServiceOfTime(UserServiceDTO dto);

    /**
     * 导出员工服务-数据总览-日期维度
     *
     * @param dto {@link UserServiceDTO}
     * @return
     */
    AjaxResult exportUserServiceOfTime(UserServiceDTO dto);
}
