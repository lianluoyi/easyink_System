package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.WeEmpleCodeAnalyse;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.domain.dto.emplecode.FindWeEmpleCodeAnalyseDTO;
import com.easyink.wecom.domain.vo.WeEmplyCodeAnalyseVO;

import java.util.List;

/**
 * 类名：WeEmpleCodeAnalyseService
 *
 * @author Society my sister Li
 * @date 2021-11-04
 */
public interface WeEmpleCodeAnalyseService extends IService<WeEmpleCodeAnalyse> {

    /**
     * 时间段内新增和流失客户数据统计
     *
     * @param findWeEmpleCodeAnalyseDTO findWeEmpleCodeAnalyseDTO
     * @return List<WeEmpleCodeAnalyse>
     */
    WeEmplyCodeAnalyseVO getTimeRangeAnalyseCount(FindWeEmpleCodeAnalyseDTO findWeEmpleCodeAnalyseDTO);


    /**
     * 导出时间段内新增和流失客户数据
     *
     * @param findWeEmpleCodeAnalyseDTO {@link FindWeEmpleCodeAnalyseDTO}
     * @return
     */
    AjaxResult exportTimeRangeAnalyseCount(FindWeEmpleCodeAnalyseDTO findWeEmpleCodeAnalyseDTO);

    /**
     * 新增
     *
     * @param corpId         企业ID
     * @param userId         员工ID
     * @param externalUserId 客户ID
     * @param state          state
     * @return boolean
     */
    boolean saveWeEmpleCodeAnalyse(String corpId, String userId, String externalUserId, String state, Boolean addFlag);

    /**
     * 保存获客链接统计信息
     *
     * @param corpId         企业ID
     * @param userId         员工ID
     * @param externalUserId 客户ID
     * @param channelId      渠道ID
     * @param assistantId    获客链接ID
     * @return boolean 结果
     */
    boolean saveAssistantAnalyse(String corpId, String userId, String externalUserId, String channelId, String assistantId, Boolean addFlag);

    /**
     * 根据state查询添加活码数
     *
     * @param state state
     * @return int
     */
    int getAddCountByState(String state);

    /**
     * 获取企业下所有活码-员工对应的统计数据
     *
     * @param corpId          企业ID
     * @param date            日期 格式为YYYY-MM-DD
     * @param empleCodeIdList 活码ID列表
     * @return 统计数据
     */
    List<WeEmpleCodeStatistic> getEmpleStatisticData(String corpId, String date, List<Long> empleCodeIdList);

    /**
     * 根据EmpleCodeId获取UserId
     *
     * @param corpId 企业ID
     * @param empleCodeIdList 活码ID列表
     * @return userid列表
     */
    List<String> selectUserIds(String corpId, List<Long> empleCodeIdList);

    /**
     * 初始化企业下所有活码-员工对应的统计数据
     *
     * @param corpId 企业ID
     * @param date 日期 格式为YYYY-MM-DD
     * @return 统计数据
     */
    List<WeEmpleCodeStatistic> initData(String corpId, List<Long> empleCodeIdList, String date);

    /**
     * 根据日期，活码id/获客链接id，获取查询的活码/获客链接下对应的analyse表统计数据
     *
     * @param corpId          企业ID
     * @param beginDate       开始日期，格式为YYYY-MM-DD
     * @param endDate         结束日期，格式为YYYY-MM-DD
     * @param empleCodeIdList 活码id/获客链接id列表
     * @return {@link WeEmpleCodeAnalyse}
     */
    List<WeEmpleCodeAnalyse> getAnalyseDataByEmpleCodeId(String corpId, String beginDate, String endDate, List<Long> empleCodeIdList);
}
