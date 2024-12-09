package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeCustomerPortrait;
import com.easyink.wecom.domain.WeCustomerSocialConn;
import com.easyink.wecom.domain.dto.WeCustomerPushMessageDTO;
import com.easyink.wecom.domain.entity.WeCustomerExportDTO;
import com.easyink.wecom.domain.model.customer.UserIdAndExternalUserIdModel;
import com.easyink.wecom.domain.model.moment.MomentCustomerQueryModel;
import com.easyink.wecom.domain.query.customer.CustomerQueryContext;
import com.easyink.wecom.domain.vo.WeCustomerNameAndUserIdVO;
import com.easyink.wecom.domain.vo.customer.SessionArchiveCustomerVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerUserListVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.easyink.wecom.domain.vo.sop.CustomerSopVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 企业微信客户Mapper接口
 *
 * @author admin
 * @date 2020-09-13
 */
@Repository
public interface WeCustomerMapper extends BaseMapper<WeCustomer> {
    /**
     * 查询企业微信客户
     *
     * @param externalUserId 企业微信客户ID
     * @return 企业微信客户
     */
    WeCustomer selectWeCustomerById(@Param("externalUserId") String externalUserId, @Param("corpId") String corpId);

    /**
     * 查询外部客户name
     *
     * @param externalUserIdList 外部客户userId列表
     * @return List<WeCustomerNameAndUserIdVO>
     */
    List<WeCustomerNameAndUserIdVO> selectWeCustomerByUserIdList(@Param("externalUserIdList") List<String> externalUserIdList, @Param("userIdList") List<String> userIdList, @Param("corpId") String corpId);

    /**
     * 查询sop详情客户信息
     *
     * @param corpId      企业id
     * @param userIds     员工id
     * @param customerIds 客户id
     * @return {@link List<CustomerSopVO>}
     */
    List<CustomerSopVO> listOfCustomerIdAndUserId(@Param("corpId") String corpId, @Param("userIds") String userIds, @NotBlank @Param("list") List<String> customerIds);

    /**
     * 新增企业微信客户
     *
     * @param weCustomer 企业微信客户
     * @return 结果
     */
    int insertWeCustomer(WeCustomer weCustomer);

    /**
     * 修改企业微信客户
     *
     * @param weCustomer 企业微信客户
     * @return 结果
     */
    int updateWeCustomer(WeCustomer weCustomer);

    /**
     * 根据员工ID获取客户客户id列表
     *
     * @param externalUserid 客户id
     * @param userId         员工id
     * @param corpId         企业id
     * @return 客户列表
     */
    List<WeCustomer> getCustomersByUserId(@Param("externalUserid") String externalUserid, @Param("userId") String userId, @Param("corpId") String corpId);


    /**
     * 根据员工ID获取客户客户id列表V2
     *
     * @param externalUserid 客户id
     * @param userId         员工id
     * @param corpId         企业id
     * @return 客户列表
     */
    List<WeCustomerVO> getCustomersByUserIdV2(@Param("externalUserid") String externalUserid, @Param("userId") String userId, @Param("corpId") String corpId);


    /**
     * 根据外部联系人ID和企业员工ID获取当前客户信息
     *
     * @param externalUserid
     * @param userid
     * @return
     */
    WeCustomerPortrait findCustomerByOperUseridAndCustomerId(@Param("externalUserid") String externalUserid, @Param("userid") String userid, @Param("corpId") String corpId);


    /**
     * 统计客户社交关系
     *
     * @param externalUserid 客户id
     * @param userid         员工id
     * @return
     */
    WeCustomerSocialConn countSocialConn(@Param("externalUserid") String externalUserid, @Param("userid") String userid, @Param("corpId") String corpId);


    /**
     * 查询企业微信客户列表,不查询一对多关系相关数据
     *
     * @param weCustomer 企业微信客户
     * @return 企业微信客户集合
     */
    List<WeCustomer> selectWeCustomerListNoRel(WeCustomerPushMessageDTO weCustomer);


    /**
     * 根据客户状态和公司id获取去重客户数量
     *
     * @param corpId 公司id
     * @return 去重客户数量
     */
    Integer countCustomerNum(@Param("corpId") String corpId);

    /**
     * 根据客户状态企业id和时间获取去重客户数量
     *
     * @param corpId 企业ID
     * @param time 时间，格式为YYYY-MM-DD 23:59:59
     * @return 去重客户数量
     */
    Integer countCustomerNumByTime(@Param("corpId") String corpId, @Param("time") String time);

    /**
     * 批量更新/插入 客户基本信息
     *
     * @param list 客户基本信息集合 {@link List<WeCustomer>}
     * @return 成功数量
     */
    Integer batchInsert(@Param("list") List<WeCustomer> list);

    /**
     * 修改生日
     *
     * @param weCustomer {@link WeCustomer}
     * @return
     */
    Integer updateBirthday(WeCustomer weCustomer);


    /**
     * 筛选获取类型为单行文本与多行文本的用户自定义属性
     *
     * @param list
     * @return
     */
    List<BaseExtendPropertyRel> selectTypeOneLine(List<BaseExtendPropertyRel> list);

    /**
     * 筛选获取类型为单选与多选的用户自定义属性
     *
     * @param list
     * @return
     */
    List<BaseExtendPropertyRel> selectTypeMultiple(List<BaseExtendPropertyRel> list);

    /**
     * 筛选获取类型为时间的用户自定义属性
     *
     * @param list
     * @return
     */
    List<BaseExtendPropertyRel> selectTypeTime(List<BaseExtendPropertyRel> list);

    /**
     * 查询去重客户去重后企业微信客户列表
     * @update V1.34.0版本之后弃用，该SQL会导致慢查询响应超时，请使用com.easyink.wecom.mapper.WeCustomerMapper.selectWeCustomerListDistinctV2()
     *
     * @param weCustomer
     * @return
     */
    @Deprecated
    List<WeCustomerVO> selectWeCustomerListDistinct(WeCustomer weCustomer);

    /**
     * 查询客户列表第二版（根据客户ID查询）
     *
     * @param corpId             企业ID
     * @param externalUseridList 客户ID列表
     * @return {@link List<SessionArchiveCustomerVO>}
     */
    List<SessionArchiveCustomerVO> selectWeCustomerListDistinctV2(@Param("corpId") String corpId, @Param("list") List<String> externalUseridList);

    /**
     * 根据客户id查询所属的员工列表
     *
     * @param customerId 客户id
     * @param corpId     企业id
     * @return
     */
    List<WeCustomerUserListVO> selectUserListByCustomerId(@Param("customerId") String customerId, @Param("corpId") String corpId);

    /**
     * 模糊搜索客户
     *
     * @param customerName
     * @param corpId
     * @return
     */
    List<WeCustomerVO> listCustomers(@Param("customerName") String customerName, @Param("corpId") String corpId);

    /**
     * 客户列表查询V3
     * 修复了 v2 版本慢查询的问题
     *
     * @param weCustomer 查询客户条件
     * @return {@link WeCustomerVO }
     */

    List<WeCustomerVO> selectWeCustomerV3(WeCustomer weCustomer);

    /**
     * 客户列表查询V4
     * 修复了 v3 版本慢查询的问题
     *
     * @param weCustomer 查询客户条件
     * @param userIdList 员工id列表
     * @param offset
     * @param limit
     * @return {@link WeCustomerVO }
     */
    List<WeCustomerVO> selectWeCustomerV4(@Param("weCustomer") WeCustomer weCustomer, @Param("userIdList") List<String> userIdList, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * selectWeCustomerV4 的countsql
     * @param weCustomer 查询客户条件
     * @param userIdList 员工id列表
     * @return 统计count数量
     */
    Integer selectWeCustomerV4Count(@Param("weCustomer") WeCustomer weCustomer, @Param("userIdList") List<String> userIdList);

    /**
     * 查看客户去重后的总数
     *
     * @param weCustomer 查询客户条件
     * @return 去重后的总数
     */
    Integer ignoreDuplicateCustomerCnt(WeCustomer weCustomer);

    /**
     * 查询导出的客户
     *
     * @param weCustomer 查询条件
     * @return 客户
     */
    List<WeCustomerVO> selectExportCustomer(WeCustomer weCustomer);

    /**
     * 查询客户总数
     *
     * @param dto {@link WeCustomerExportDTO}
     * @return 客户总数
     */
    Integer selectWeCustomerCount(WeCustomer dto);

    /**
     * 根据客户id列表获取客户信息
     * 注意：客户id列表不能为空
     *
     * @param externalUserIdList 客户id列表
     * @param corpId             企业id
     * @return {@link List<WeCustomer>}
     */
    List<WeCustomer> selectCustomerInfoByExternalIdList(@NotNull @Param("externalUserIdList") List<String> externalUserIdList, @Param("corpId") String corpId);

    /**
     * 查询员工id 通过标签
     * @param tagIdList 标签id列表
     * @return 员工id列表
     */
    List<String> selectUserIdByTag(@Param("tagIds") List<String> tagIdList);

    /**
     * 查询员工id与客户id 去重集合
     * @param query 查询条件实体
     * @return Set列表
     */
    @DataScope(userAlias = "wfcr")
    List<UserIdAndExternalUserIdModel> selectUserExternalByNormalUserId(MomentCustomerQueryModel query);

    /**
     * 根据传入的员工id筛选过滤出正常的员工id
     * @param query 查询model
     * @return
     */
    @DataScope(userAlias = "wfcr")
    List<String> selectUserIdListByNormalUserId(MomentCustomerQueryModel query);


    Integer ignoreDuplicateCustomerCntV2(CustomerQueryContext customerQueryContext);

    /**
     * 根据传入得关键词,查询客户名称或者员工给客户的备注
     *
     * @param name   关键词name
     * @param corpId 企业id
     * @return
     */
    List<String> selectExternalUserIdLikeName(@Param("name") String name, @Param("corpId") String corpId);

    /**
     * 客户生日过滤客户
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param corpId 企业id
     * @return
     */
    List<String> selectExternalUserIdByBirthday(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("corpId") String corpId);

    /**
     * 根据企业id查询外部联系人id列表
     * @param corpId 企业id
     * @return 外部联系人id列表
     */
    List<String> selectExternalUserIdByCorpId(@Param("corpId") String corpId);

}
