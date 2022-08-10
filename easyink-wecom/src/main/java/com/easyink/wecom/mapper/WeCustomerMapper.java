package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeCustomerPortrait;
import com.easyink.wecom.domain.WeCustomerSocialConn;
import com.easyink.wecom.domain.dto.WeCustomerPushMessageDTO;
import com.easyink.wecom.domain.vo.WeCustomerNameAndUserIdVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerUserListVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.easyink.wecom.domain.vo.sop.CustomerSopVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
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
     * 获取客户列表第二版
     *
     * @param weCustomer {@link WeCustomer}
     * @return 客户列表
     */
    List<WeCustomerVO> selectWeCustomerListV2(WeCustomer weCustomer);

    /**
     * 查询去重客户去重后企业微信客户列表
     *
     * @param weCustomer
     * @return
     */
    List<WeCustomerVO> selectWeCustomerListDistinct(WeCustomer weCustomer);

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
}
