package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeCustomerPortrait;
import com.easyink.wecom.domain.WeOperationsCenterCustomerSopFilterEntity;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.dto.LeaveWeUserListsDTO;
import com.easyink.wecom.domain.dto.WeCustomerPushMessageDTO;
import com.easyink.wecom.domain.dto.WeCustomerSearchDTO;
import com.easyink.wecom.domain.dto.WeWelcomeMsg;
import com.easyink.wecom.domain.dto.customer.EditCustomerDTO;
import com.easyink.wecom.domain.dto.pro.EditCustomerFromPlusDTO;
import com.easyink.wecom.domain.dto.tag.RemoveWeCustomerTagDTO;
import com.easyink.wecom.domain.dto.unionid.GetUnionIdDTO;
import com.easyink.wecom.domain.entity.WeCustomerExportDTO;
import com.easyink.wecom.domain.model.customer.UserIdAndExternalUserIdModel;
import com.easyink.wecom.domain.vo.QueryCustomerFromPlusVO;
import com.easyink.wecom.domain.vo.WeMakeCustomerTagVO;
import com.easyink.wecom.domain.vo.customer.SessionArchiveCustomerVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerSumVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerUserListVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.easyink.wecom.domain.vo.sop.CustomerSopVO;
import com.easyink.wecom.domain.vo.unionid.GetUnionIdVO;
import com.easyink.wecom.openapi.dto.GetWeCustomerByUnionIdDTO;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * 客户批量打标签
     *
     * @param weMakeCustomerTagVOS
     * @param updateBy             操作人
     */
    void batchMakeLabel(List<WeMakeCustomerTagVO> weMakeCustomerTagVOS, String updateBy);

    /**
     * 单独打标签
     *
     * @param corpId            企业id
     * @param userId            员工id
     * @param externalUserId    外部联系人id
     * @param addTagIds         增加的标签
     * @param oprUserId         操作人userId 用于记录员工操作
     */
    void singleMarkLabel(String corpId, String userId, String externalUserId, List<String> addTagIds, String oprUserId);

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
     * 根据员工和部门获取员工列表id
     * @param users 员工ids
     * @param departments 部门ids
     * @param corpId 企业id
     * @return 员工id列表
     */
    List<String> getUserIdList(String users, String departments, String corpId);

    /**
     * 根据员工和部门获取员工列表id
     * @param users 员工ids
     * @param departments 部门ids
     * @param tags 标签id
     * @param corpId 企业id
     * @return 员工id列表
     */
    List<String> getUserIdList(String users, String departments, String tags, String corpId);

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
     * 修改客户信息 (备注 、自定义字段、 标签 )
     *
     * @param dto {@link EditCustomerDTO}
     */
    void editCustomer(EditCustomerDTO dto);

    /**
     * 使用tagIds批量打标签
     *
     * @param corpId            企业id
     * @param userId            员工id
     * @param externalUserId    外部联系人id
     * @param addTagIds         新增的tags
     * @param removeTagIds      移除的tags
     */
    void batchMarkCustomTagWithTagIds(String corpId, String userId, String externalUserId, List<String> addTagIds, List<String> removeTagIds);

    /**
     * 批量打标签
     *
     * @param corpId            企业id
     * @param userId            员工id
     * @param externalUserId    外部联系人id
     * @param addTags           新增的tags
     * @param removeTags        移除的tags
     */
    void batchMarkCustomTag(String corpId, String userId, String externalUserId, List<WeTag> addTags, List<WeTag> removeTags);

    /**
     * 查询企业微信客户列表
     *
     * @param weCustomer {@link WeCustomer}
     * @return 客户列表
     */
    @DataScope
    TableDataInfo<WeCustomerVO> selectWeCustomerListV3(WeCustomer weCustomer);

    /**
     * 为客户设置客户信息
     *
     * @param list   客户列表
     * @param corpId 企业id
     */
    void setCustomerInfoByList(List<WeCustomerVO> list, String corpId);

    /**
     * 根据查询条件，构建过滤客户id列表
     *
     * @param corpId     企业id
     * @param weCustomer {@link WeCustomer}
     * @return true 有符合条件的客户id列表，false 无符合条件的客户id列表
     */
    boolean buildFilterExternalUseridList(String corpId, WeCustomer weCustomer);

    /**
     * 将用户DTO转化为实体类
     *
     * @param weCustomerSearchDTO
     * @return 客户实体类
     */
    WeCustomer changeWecustomer(WeCustomerSearchDTO weCustomerSearchDTO);

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
     * @update V1.34.0 由于慢查询废弃,使用V2接口
     */
    @Deprecated
    List<WeCustomerVO> selectWeCustomerListDistinct(WeCustomer weCustomer);

    /**
     * 查询去重客户去重后企业微信客户列表
     *
     * @param weCustomer {@link WeCustomer}
     * @return 客户信息列表
     */
    @Deprecated
    List<SessionArchiveCustomerVO> selectWeCustomerListDistinctV2(WeCustomer weCustomer);

    /**
     * 查询去重客户去重后企业微信客户列表V3
     *
     * @param weCustomer {@link WeCustomer}
     * @return 客户信息列表
     */
    List<SessionArchiveCustomerVO> selectWeCustomerListDistinctV3(WeCustomer weCustomer);

    /**
     * 获取会话存档-客户检索-客户列表-去重后的客户数
     *
     * @param weCustomer {@link WeCustomer}
     * @return 客户总数
     */
    WeCustomerSumVO customerCount(WeCustomer weCustomer);

    /**
     * 根据客户id查询所属的员工列表
     *
     * @param customerId 客户id
     * @param corpId     企业id
     * @return
     */
    List<WeCustomerUserListVO> listUserListByCustomerId(String customerId, String corpId);


    /**
     * 模糊查询客户 (无需登录可用)
     *
     * @param corpId
     * @param customerName
     * @return
     */
    List<WeCustomerVO> getCustomer(String corpId, String customerName);

    /**
     * 根据external_user_id获取用户的unionId
     *
     * @param getUnionIdDTO 客户id和企业secret等参数 {@link GetUnionIdDTO }
     * @return {@link GetUnionIdVO}
     */
    GetUnionIdVO getDetailByExternalUserId(GetUnionIdDTO getUnionIdDTO);

    /**
     * 根据openId获取客户详情
     *
     * @param openId 公众号openid
     * @param corpId 企业id
     * @return 客户详情 {@link WeCustomer}
     */
    WeCustomer getCustomerInfoByOpenId(String openId, String corpId);

    /**
     * 通过unionId和openId获取员工详情
     *
     * @param unionId unionId
     * @param openId  openId
     * @param corpId  企业id
     * @return
     */
    WeCustomer getCustomerByUnionId(String unionId, String openId, String corpId);

    /**
     * 通过unionId获取员工详情
     *
     * @param dto {@link GetWeCustomerByUnionIdDTO}
     * @return {@link WeCustomer}
     */
    WeCustomer getCustomerByUnionId(GetWeCustomerByUnionIdDTO dto);

    /**
     * 通过明文获取密文外部联系人exUserId
     *
     * @param corpId         企业id
     * @param externalUserId 外部联系人exUserId
     * @return
     */
    String getOpenExUserId(String corpId, String externalUserId);

    /**
     * 生活需要导出的客户信息
     *
     * @param dto      导出客户请求
     * @param oprId    操作id
     * @param fileName 导出的文件名
     * @return fileName 导出的文件名
     */
    void genExportData(WeCustomerExportDTO dto, String oprId, String fileName);

    /**
     *   获取导出客户的结果
     *
     * @param oprId 操作id
     * @return  true or false
     */
    Boolean getExportResult(String oprId);

    WeCustomerExportDTO transferData(WeCustomerExportDTO dto);


    /**
     * 客户去重统计
     * @param weCustomer 客户查询过滤信息实体
     * @return 客户去重countVO
     */
    WeCustomerSumVO weCustomerCountV2(WeCustomer weCustomer);

    /**
     * 查询员工和客户关系列表
     * @param users 员工ids
     * @param tags 标签ids
     * @param corpId 企业id
     * @return 关系Set列表
     */
    List<UserIdAndExternalUserIdModel> selectUserIdFromRef(String users,String tags, String corpId);

    /**
     * 过滤查询存在客户关系表的员工id列表
     * @param candidateUserIdList 候选员工id 原数据
     * @param tags 标签列表
     * @param corpId 企业id
     * @return
     */
    List<String> listExistDbUserIdByTagAndUserIdList(List<String> candidateUserIdList, String tags, String corpId);

    /**
     * 补充externalUserId明文
     * @param list 客户数据列表
     * @param corpId 企业id(密文)
     */
    void setOriginExternalUserId(List<WeCustomerVO> list, String corpId);
}
