package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.common.core.domain.RootEntity;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeUserBehaviorData;
import com.easyink.wecom.domain.dto.statistics.CustomerOverviewDTO;
import com.easyink.wecom.domain.dto.statistics.StatisticsDTO;
import com.easyink.wecom.domain.vo.customer.SessionArchiveCustomerVO;
import com.easyink.wecom.domain.vo.emple.WeEmpleCodeChannelRelVO;
import com.easyink.wecom.domain.vo.statistics.CustomerOverviewVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 具有外部联系人功能企业员工也客户的关系Mapper接口
 *
 * @author admin
 * @date 2020-09-19
 */
@Repository
public interface WeFlowerCustomerRelMapper extends BaseMapper<WeFlowerCustomerRel> {

    /**
     * 批量删除具有外部联系人功能企业员工也客户的关系
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteWeFlowerCustomerRelByIds(Long[] ids);


    /**
     * 成员添加客户统计
     *
     * @param weFlowerCustomerRel
     * @return
     */
    List<Map<String, Object>> getUserAddCustomerStat(WeFlowerCustomerRel weFlowerCustomerRel);

    /**
     * 批量插入
     *
     * @param list 集合
     * @return 更新行数
     */
    Integer batchInsert(@Param("list") List<WeFlowerCustomerRel> list);

    /**
     * 批量更新或者修改
     *
     * @param entity {@link WeFlowerCustomerRel}
     * @return 修改行数
     */
    Integer saveOrUpdate(WeFlowerCustomerRel entity);

    /**
     * 转接员工客户关系
     *
     * @param corpId         公司ID
     * @param handoverRelId  原跟进人客户关系id
     * @param takeoverRelId  接替人客户关系id
     * @param takeoverUserId
     */
    void transferRel(@Param("corpId") String corpId, @Param("handoverRelId") Long handoverRelId, @Param("takeoverRelId") Long takeoverRelId, @Param("takeoverUserid") String takeoverUserId);

    /**
     * 批量更新状态
     *
     * @param relList {@link List<WeFlowerCustomerRel>}
     */
    void batchUpdateStatus(@Param("list") List<WeFlowerCustomerRel> relList);

    /**
     * 查询员工当天的客户总数和流失客户数
     *
     * @param userId    员工id
     * @param corpId    企业id
     * @param beginTime 时间范围开始时间
     * @param endTime   时间范围结束时间
     * @return
     */
    WeUserBehaviorData getTotalContactAndLossCnt(@Param("userId") String userId,
                                                 @Param("corpId") String corpId,
                                                 @Param("beginTime") String beginTime,
                                                 @Param("endTime") String endTime);

    /**
     * 更新已流失重新添加回来的客户状态
     *
     * @param corpId          企业ID
     * @param userId          员工ID
     * @param external_userid 外部联系人ID
     * @return 结果
     */
    Integer updateLossExternalUser(@Param("corpId") String corpId, @Param("userId") String userId, @Param("external_userid") String external_userid);

    /**
     * 根据unionId 查询客户
     *
     * @param corpId   企业ID
     * @param unionIds unionId 列表
     * @return 客户关系列表 {@link WeFlowerCustomerRel }
     */
    List<WeFlowerCustomerRel> getByUnionIds(@Param("corpId") String corpId, @Param("unionIds") List<String> unionIds);

    /**
     * 根据备注手机号 查询客户
     *
     * @param corpId  企业ID
     * @param mobiles 手机号列表
     * @return 客户关系列表 {@link WeFlowerCustomerRel }
     */
    List<WeFlowerCustomerRel> getByMobiles(@Param("corpId") String corpId, @Param("mobiles") List<String> mobiles);
    /**
     * 根据开始，结束时间和员工ID，获取员工维度-截止时间下的有效客户数
     *
     * @param corpId    企业ID
     * @param beginTime 开始时间，格式为YYYY-MM-DD 00:00:00
     * @param endTime   结束时间，格式为YYYY-MM-DD 23:59:59
     * @return {@link CustomerOverviewVO}
     */
    List<CustomerOverviewVO> getCurrNewCustomerCntByUser(@Param("corpId") String corpId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("userIds") List<String> userIds);
    /**
     * 根据(数据权限），开始，结束时间和员工ID，获取日期维度-截止时间下的有效客户数
     *
     * @param dto {@link CustomerOverviewDTO}
     * @return {@link CustomerOverviewVO}
     */
    List<CustomerOverviewVO> getCurrNewCustomerCntByTime(CustomerOverviewDTO dto);

    /**
     * 根据(数据权限），开始，结束时间和员工ID，获取单个日期维度-截止时间下的有效客户数
     *
     * @param corpId     企业ID
     * @param userIdList 数据权限下的userid列表
     * @param beginTime  开始时间 格式 YYYY-MM-DD HH:MM:SS
     * @param endTime    结束时间 格式 YYYY-MM-DD HH:MM:SS
     * @return {@link CustomerOverviewVO}
     */
    CustomerOverviewVO getCurrNewCustomerCnt(@Param("corpId") String corpId, @Param("userIds") List<String> userIdList, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     * 根据（数据权限），开始，结束时间，获取数据总览-截止时间下的有效客户数
     *
     * @param dto {@link StatisticsDTO}
     * @return 有效客户数
     */
    Integer getCurrentNewCustomerCntByDataScope(StatisticsDTO dto);

    /**
     * 获客链接详情-渠道维度-渠道累计客户数
     *
     * @param channelIdList 渠道ID列表
     * @param userIds       员工ID，多个用，隔开
     * @param corpId        企业ID
     * @param endTime       查询截止时间，格式为YYYY-MM-DD 23:59:59
     * @return 各个渠道对应的累计客户数
     */
    List<WeEmpleCodeChannelRelVO> getChannelRelAccumulateCnt(@Param("channelIdList") List<Long> channelIdList, @Param("userIds") String userIds, @Param("corpId") String corpId, @Param("endTime") String endTime);

    /**
     * 获客链接详情-渠道维度-渠道未流失的客户数（有效客户数）
     *
     * @param stateList 来源state列表
     * @param userIds 员工ID，多个用，隔开
     * @param corpId 企业ID
     * @param beginTime 查询开始时间，格式为YYYY-MM-DD HH:MM:00
     * @param endTime 查询截止时间，格式为YYYY-MM-DD HH:MM:59
     * @return 各个渠道对应的未流失的客户数（有效客户数）
     */
    List<WeEmpleCodeChannelRelVO> getChannelRelEffectCnt(@Param("stateList") List<String> stateList, @Param("userIds") String userIds, @Param("corpId") String corpId, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     * 获客链接详情-日期维度-渠道累计客户数
     *
     * @param channelIdList channelId列表
     * @param userIds 员工ID，多个用，隔开
     * @param corpId 企业ID
     * @param endTime 查询截止时间，格式为YYYY-MM-DD 23:59:59
     * @return 各个渠道对应的累计客户数
     */
    List<WeEmpleCodeChannelRelVO> getChannelDateRelAccumulateCnt(@Param("channelIdList") List<Long> channelIdList, @Param("userIds") String userIds, @Param("corpId") String corpId, @Param("endTime") String endTime);


    /**
     * 获客链接详情-日期维度-渠道未流失的客户数（有效客户数）
     *
     * @param stateList 来源state列表
     * @param userIds 员工ID，多个用，隔开
     * @param corpId 企业ID
     * @param beginTime 查询开始时间，格式为YYYY-MM-DD HH:MM:00
     * @param endTime 查询截止时间，格式为YYYY-MM-DD HH:MM:59
     * @return 各个渠道对应的未流失的客户数（有效客户数）
     */
    List<WeEmpleCodeChannelRelVO> getChannelDateRelEffectCnt(@Param("stateList") List<String> stateList, @Param("userIds") String userIds, @Param("corpId") String corpId, @Param("beginTime") String beginTime, @Param("endTime") String endTime);


    /**
     * 根据结束时间和已激活(is_active = 1)员工ID列表, 获取首页-数据总览-客户总数
     *
     * @param corpId           企业ID
     * @param beginTime        开始时间，格式为YYYY-MM-DD 00:00:00
     * @param endTime          结束时间，格式为YYYY-MM-DD 23:59:59
     * @param normalUserIdList 过滤员工id列表
     * @return 已激活员工对应的首页-数据总览-客户总数(待继承客户数)
     */
    Integer getNormalTotalAllContactCnt(@Param("corpId") String corpId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("normalUserIdList") List<String> normalUserIdList);

    /**
     * 根据结束时间和已离职员工(is_active = 6)ID列表, 获取首页-数据总览-客户总数
     *
     * @param corpId        企业ID
     * @param beginTime     开始时间，格式为YYYY-MM-DD 00:00:00
     * @param endTime       结束时间，格式为YYYY-MM-DD 23:59:59
     * @param delUserIdList 过滤员工id列表
     * @return 已离职员工对应的首页-数据总览-客户总数(待继承客户数)
     */
    Integer getDelTotalAllContactCnt(@Param("corpId") String corpId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("delUserIdList") List<String> delUserIdList);

    /**
     * 根据员工id获取数据统计-客户联系-客户总数
     *
     * @param corpId  企业ID
     * @param endTime 结束时间，格式为YYYY-MM-DD 23:59:59
     * @param userId  员工ID
     * @return 员工对应的客户总数
     */
    List<WeUserBehaviorData> getTotalAllContactCntByUserId(@Param("corpId") String corpId, @Param("endTime") String endTime, @Param("userIdList") List<String> userId);

    /**
     * 获取数据权限下的externalUserid列表
     *
     * @param corpId             企业ID
     * @param rootEntity         {@link RootEntity}
     * @param externalUseridList 客户ID列表
     * @param pageNum            分页页数
     * @param pageSize           分页大小
     * @return externalUserid列表
     */
    List<SessionArchiveCustomerVO> selectExternalUseridByDataScope(@Param("corpId") String corpId, @Param("rootEntity") RootEntity rootEntity, @Param("externalUseridList") List<String> externalUseridList, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    /**
     * 获取数据权限下的externalUserid列表(去重）
     *
     * @param corpId             企业ID
     * @param rootEntity         {@link RootEntity}
     * @param externalUseridList 客户ID列表
     * @return externalUserid列表
     */
    List<SessionArchiveCustomerVO> selectExternalUseridByDataScopeV2(@Param("corpId") String corpId, @Param("rootEntity") RootEntity rootEntity, @Param("externalUseridList") List<String> externalUseridList);

    /**
     * 获取数据权限下的externalUserid去重后的客户数量
     *
     * @param corpId             企业ID
     * @param rootEntity         {@link RootEntity}
     * @param externalUseridList 客户ID列表
     * @return 去重后的客户总数
     */
    Integer selectExternalUseridByDataScopeCount(@Param("corpId") String corpId, @Param("rootEntity") RootEntity rootEntity, @Param("externalUseridList") List<String> externalUseridList);

    /**
     * 根据员工id列表和标签id列表，查询有客户的员工id列表（去重）
     *
     * @param userIdList 员工id列表
     * @param tagIdList  标签id列表
     * @param corpId     企业id
     * @return 有客户的员工id列表
     */
    List<String> selectHaveCustomerUserIdList(@Param("userIdList") List<String> userIdList, @Param("tagIdList") List<String> tagIdList, @Param("corpId") String corpId);

    /**
     * 保存触达客户关系
     *
     * @param userIdList   员工id列表
     * @param tagIdList    标签id列表
     * @param corpId       企业id
     * @param momentTaskId 朋友圈任务id
     */
    void insertUserCustomerRel(@Param("userIdList") List<String> userIdList, @Param("tagIdList") List<String> tagIdList, @Param("corpId") String corpId, @Param("momentTaskId") Long momentTaskId);

    /**
     * 查询补偿的客户关系数据
     * @param corpId 企业id
     * @param beginTime 数据丢失的开始时间
     * @param endTime 数据丢失的结束时间
     * @return
     */
    List<WeFlowerCustomerRel> selectCompensationData(@Param("corpId") String corpId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);
}
