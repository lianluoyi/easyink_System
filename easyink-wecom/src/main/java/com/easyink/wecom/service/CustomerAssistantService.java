package com.easyink.wecom.service;

import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.dto.emplecode.AddWeEmpleCodeDTO;
import com.easyink.wecom.domain.dto.emplecode.FindAssistantDetailStatisticCustomerDTO;
import com.easyink.wecom.domain.dto.emplecode.FindChannelRangeChartDTO;
import com.easyink.wecom.domain.vo.emple.*;
import com.easyink.wecom.domain.vo.WeEmpleCodeVO;

import java.util.List;

/**
 * 获客链接Service层
 *
 * @author lichaoyu
 * @date 2023/8/23 10:35
 */
public interface CustomerAssistantService {

    /**
     * 新增获客链接
     *
     * @param addWeEmpleCodeDTO {@link AddWeEmpleCodeDTO}
     * @return 结果
     */
    Integer insertCustomerAssistant(AddWeEmpleCodeDTO addWeEmpleCodeDTO);

    /**
     * 修改获客链接
     *
     * @param weEmpleCode  {@link AddWeEmpleCodeDTO}
     * @return 结果
     */
    Integer updateCustomerAssistant(AddWeEmpleCodeDTO weEmpleCode);

    /**
     * 批量逻辑删除获客链接
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    Integer batchRemoveCustomerAssistant(String corpId, String ids);

    /**
     * 查询获客链接
     *
     * @param id     员工活码ID
     * @param corpId 企业Id
     * @return {@link WeEmpleCode}
     */
    WeEmpleCodeVO selectCustomerAssistantById(Long id, String corpId);

    /**
     * 获取主页获客情况信息
     *
     * @param corpId 企业ID
     * @return {@link WeEmpleCodeSituationVO}
     */
    WeEmpleCodeSituationVO listSituation(String corpId);

    /**
     * 同步主页获客情况信息
     *
     * @param corpId 企业ID
     * @return 结果
     */
    Integer syncSituation(String corpId);

    /**
     * 获客链接详情-数据总览
     *
     * @param empleCodeId 获客链接id
     * @param corpId 企业ID
     * @return {@link CustomerAssistantDetailTotalVO}
     */
    CustomerAssistantDetailTotalVO detailTotal(String empleCodeId, String corpId);

    /**
     * 获客链接详情-趋势图、渠道新增客户数排行
     *
     * @param findChannelRangeChartDTO {@link FindChannelRangeChartDTO}
     * @return {@link ChannelDetailChartVO}
     */
    List<ChannelDetailChartVO> detailChart(FindChannelRangeChartDTO findChannelRangeChartDTO);

    /**
     * 获客链接详情-渠道新增客户数排行
     *
     * @param findChannelRangeChartDTO {@link FindChannelRangeChartDTO}
     * @return {@link ChannelDetailRangeVO}
     */
    List<ChannelDetailRangeVO> detailRange(FindChannelRangeChartDTO findChannelRangeChartDTO);

    /**
     * 获客链接详情-数据统计-客户维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return {@link AssistantDetailStatisticCustomerVO}
     */
    List<AssistantDetailStatisticCustomerVO> detailStatisticByCustomer(FindAssistantDetailStatisticCustomerDTO dto);

    /**
     * 获客链接详情-数据统计-渠道维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return {@link AssistantDetailStatisticChannelVO}
     */
    List<AssistantDetailStatisticChannelVO> detailStatisticByChannel(FindAssistantDetailStatisticCustomerDTO dto);

    /**
     * 获客链接详情-数据统计-日期维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return {@link AssistantDetailStatisticDateVO}
     */
    List<AssistantDetailStatisticDateVO> detailStatisticByDate(FindAssistantDetailStatisticCustomerDTO dto);

    /**
     * 导出获客链接详情-数据统计-客户维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return 结果
     */
    AjaxResult exportDetailStatisticByCustomer(FindAssistantDetailStatisticCustomerDTO dto);

    /**
     * 导出获客链接详情-数据统计-渠道维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return 结果
     */
    AjaxResult exportDetailStatisticByChannel(FindAssistantDetailStatisticCustomerDTO dto);

    /**
     * 导出获客链接详情-数据统计-日期维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return 结果
     */
    AjaxResult exportDetailStatisticByDate(FindAssistantDetailStatisticCustomerDTO dto);

    /**
     * 获客链接新增回调处理
     *
     * @param state          来源state
     * @param userId         员工id
     * @param externalUserId 客户id
     * @param corpId         企业id
     */
    void callBackAddAssistantHandle(String state, String userId, String externalUserId, String corpId);

    /**
     * 获客链接删除回调处理
     *
     * @param state          来源state
     * @param corpId         企业id
     * @param externalUserId 客户id
     * @param userId         员工id
     */
    void callBackDelAssistantHandle(String state, String corpId, String externalUserId, String userId);

    /**
     * 发送应用通知
     *
     * @param corpId   企业ID
     * @param sendUser 通知员工，多个用"|"隔开
     * @param content  发送内容
     */
    void sendToUser(String corpId, String sendUser, String content);
}
