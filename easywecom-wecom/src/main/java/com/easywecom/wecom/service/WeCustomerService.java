package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.wecom.domain.WeCustomer;
import com.easywecom.wecom.domain.WeCustomerPortrait;
import com.easywecom.wecom.domain.WeOperationsCenterCustomerSopFilterEntity;
import com.easywecom.wecom.domain.dto.LeaveWeUserListsDTO;
import com.easywecom.wecom.domain.dto.WeCustomerPushMessageDTO;
import com.easywecom.wecom.domain.dto.WeWelcomeMsg;
import com.easywecom.wecom.domain.dto.customer.EditCustomerDTO;
import com.easywecom.wecom.domain.dto.pro.EditCustomerFromPlusDTO;
import com.easywecom.wecom.domain.dto.tag.RemoveWeCustomerTagDTO;
import com.easywecom.wecom.domain.entity.WeCustomerExportDTO;
import com.easywecom.wecom.domain.vo.QueryCustomerFromPlusVO;
import com.easywecom.wecom.domain.vo.WeMakeCustomerTagVO;
import com.easywecom.wecom.domain.vo.customer.WeCustomerSumVO;
import com.easywecom.wecom.domain.vo.customer.WeCustomerUserListVO;
import com.easywecom.wecom.domain.vo.customer.WeCustomerVO;
import com.easywecom.wecom.domain.vo.sop.CustomerSopVO;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 企业微信客户Service接口
 *
 * @author admin
 * @date 2020-09-13
 */
@Validated
public interface WeCustomerService extends IService<WeCustomer> {

    /**
     * 查询企业微信客户
     *
     * @param externalUserId 企业微信客户ID
     * @param corpId         企业id
     * @return 企业微信客户
     */
    WeCustomer selectWeCustomerById(String externalUserId, String corpId);

    /**
     * 修改客户信息
     *
     * @param weCustomer
     */
    void updateWeCustomerRemark(WeCustomer weCustomer);

    /**
     * 新增/修改企业微信客户
     *
     * @param weCustomer 企业微信客户
     * @return 修改结果
     */
    @Override
    boolean saveOrUpdate(WeCustomer weCustomer);


    /**
     * 获取客户统计信息
     *
     * @param weCustomer {@link WeCustomer}
     * @return {@link WeCustomerSumVO}
     */
    WeCustomerSumVO weCustomerCount(WeCustomer weCustomer);

    /**
     * 同步客户 (重构第二版V2)
     *
     * @param corpId 企业ID
     */
    void syncWeCustomerV2(String corpId);


    /**
     * 调用企微根据corpId获取离职员工列表
     *
     * @param corpId 企业id
     * @return 离职员工列表
     */
    List<LeaveWeUserListsDTO.LeaveWeUser> getLeaveWeUsers(String corpId);

    /**
     * 将离职客户的List数据转变为map
     *
     * @param leaveWeUsers 离职员工列表
     * @return map（离职员工id+","+"离职时间"，客户集合）
     */
    Map<String, List<String>> replaceCustomerListToMap(List<LeaveWeUserListsDTO.LeaveWeUser> leaveWeUsers);

    /**
     * 客户打标签
     *
     * @param weMakeCustomerTag
     */
    void makeLabel(WeMakeCustomerTagVO weMakeCustomerTag);

    /**
     * 客户批量打标签
     *
     * @param weMakeCustomerTagVOS
     * @param updateBy             操作人
     */
    void makeLabelbatch(List<WeMakeCustomerTagVO> weMakeCustomerTagVOS, String updateBy);

    /**
     * 客户批量打标签
     *
     * @param weMakeCustomerTagVOS
     */
    void makeLabelbatch(List<WeMakeCustomerTagVO> weMakeCustomerTagVOS);

    /**
     * 移除客户标签(单个客户)
     *
     * @param corpId         企业id
     * @param externalUserid 客户id
     * @param userid         成员id
     * @param delIdList      需要删除的标签id
     */
    void removeLabel(String corpId, String externalUserid, String userid, List<String> delIdList);

    /**
     * 移除客户标签
     *
     * @param removeWeCustomerTagDTO
     */
    void removeLabel(RemoveWeCustomerTagDTO removeWeCustomerTagDTO);

    /**
     * 根据员工id和客户id获取客户
     *
     * @param externalUserid 客户id
     * @param userId         员工id
     * @param corpId         企业id
     * @return {@link WeCustomer}
     */
    WeCustomerVO getCustomerByUserId(String externalUserid, String userId, String corpId);

    /**
     * 根据与员工和客户id获取客户列表 V2
     *
     * @param externalUserid 客户id
     * @param userId         员工id
     * @param corpId         企业id
     * @return
     */
    List<WeCustomerVO> getCustomersByUserIdV2(String externalUserid, String userId, String corpId);

    /**
     * 根据跟进人id和外部联系人id去调用企微API详情并更新本地数据 (添加、修改客户回调处理第二版)
     *
     * @param corpId         企业ID
     * @param userId         跟进人ID
     * @param externalUserid 外部联系人ID
     */
    void updateExternalContactV2(String corpId, String userId, String externalUserid);

    /**
     * 向客户发送欢迎语
     *
     * @param weWelcomeMsg 消息
     * @param corpId       企业id
     */
    void sendWelcomeMsg(WeWelcomeMsg weWelcomeMsg, String corpId);


    /**
     * 根据外部联系人ID和企业员工ID获取当前客户信息
     *
     * @param externalUserid
     * @param userid
     * @return
     */
    WeCustomerPortrait findCustomerByOperUseridAndCustomerId(String externalUserid, String userid, String corpId);


    /**
     * 查询企业微信客户列表,不查询一对多关系相关数据
     *
     * @param weCustomer 企业微信客户
     * @return 企业微信客户集合
     */
    List<WeCustomer> selectWeCustomerListNoRel(WeCustomerPushMessageDTO weCustomer);

    /**
     * 查询sop详情客户信息
     *
     * @param corpId      企业id
     * @param userIds     员工id
     * @param customerIds 客户id
     * @return {@link List<  CustomerSopVO  >}
     */
    List<CustomerSopVO> listOfCustomerIdAndUserId(String corpId, String userIds, @NotBlank List<String> customerIds);

    /**
     * 获取去重后的客户数量
     *
     * @param corpId 企业id
     * @return 客户数量
     */
    Integer customerCount(String corpId);

    /**
     * 根据成员ID和客户头像 查询客户详情
     *
     * @param corpId 企业ID
     * @param userId 成员Id
     * @param avatar 客户头像
     * @return 客户详情信息 {@link QueryCustomerFromPlusVO}
     */
    QueryCustomerFromPlusVO getDetailByUserIdAndCustomerAvatar(String corpId, String userId, String avatar);

    /**
     * 根据成员id和用户头像修改客户信息
     *
     * @param dto {@link EditCustomerFromPlusDTO}
     */
    void editByUserIdAndCustomerAvatar(EditCustomerFromPlusDTO dto);

    /**
     * 批量插入
     *
     * @param customerList 客户集合
     */
    void batchInsert(List<WeCustomer> customerList);

    /**
     * 插入/更新企业客户
     *
     * @param weCustomer {@link WeCustomer}
     */
    void insert(WeCustomer weCustomer);

    /**
     * 导出客户
     *
     * @param dto {@link WeCustomerExportDTO}
     * @param <T> 导出类型
     * @return
     */
    <T> AjaxResult<T> export(WeCustomerExportDTO dto);

    /**
     * 修改客户信息 (备注 、自定义字段、 标签 )
     *
     * @param dto {@link EditCustomerDTO}
     */
    void editCustomer(EditCustomerDTO dto);

    /**
     * 查询企业微信客户列表
     *
     * @param weCustomer {@link WeCustomer}
     * @return 客户列表
     */
    List<WeCustomerVO> selectWeCustomerListV2(WeCustomer weCustomer);

    /**
     * 查询客户sop使用客户
     *
     * @param corpId    企业id
     * @param sopFilter sop过滤条件
     * @return {@link List<WeCustomer>}
     */
    List<WeCustomer> listOfUseCustomer(String corpId, WeOperationsCenterCustomerSopFilterEntity sopFilter);

    /**
     * 查询去重客户去重后企业微信客户列表
     *
     * @param weCustomer {@link WeCustomer}
     * @return 客户列表
     */
    List<WeCustomerVO> selectWeCustomerListDistinct(WeCustomer weCustomer);

    /**
     * 根据客户id查询所属的员工列表
     *
     * @param customerId 客户id
     * @param corpId     企业id
     * @return
     */
    List<WeCustomerUserListVO> listUserListByCustomerId(String customerId, String corpId);
}
