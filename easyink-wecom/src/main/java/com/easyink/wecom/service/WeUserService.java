package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.wecom.domain.WeCustomerAddUser;
import com.easyink.wecom.domain.WeUserRole;
import com.easyink.wecom.domain.dto.BatchUpdateUserInfoDTO;
import com.easyink.wecom.domain.dto.QueryUserDTO;
import com.easyink.wecom.domain.dto.transfer.TransferResignedUserListDTO;
import com.easyink.wecom.domain.vo.*;
import com.easyink.wecom.domain.vo.transfer.TransferResignedUserVO;

import java.util.List;
import java.util.Map;

/**
 * 通讯录相关客户Service接口
 *
 * @author admin
 * @date 2020-08-31
 */
public interface WeUserService extends IService<WeUser> {
    /**
     * 更新员工角色关系，不存在则插入
     *
     * @param weUserRole 员工角色
     */
    void updateUserRole(WeUserRole weUserRole);

    /**
     * 查询员工信息
     *
     * @param queryUserDTO 查询条件
     * @return vo
     */
    List<WeUserVO> listOfUser(QueryUserDTO queryUserDTO);

    /**
     * 查询员工信息
     *
     * @param corpId          企业ID
     * @param queryUserIdList 查询指定员工信息
     * @return {@link List<WeUserVO>}
     */
    List<WeUserVO> listOfUser(String corpId, List<String> queryUserIdList);

    /**
     * 获取员工信息
     *
     * @param corpId 企业ID
     * @param userId 员工id
     * @return vo
     */
    WeUserVO getUser(String corpId, String userId);

    /**
     * 查询权限下的员工id
     *
     * @param corpId
     * @param departments 部门id
     * @return 员工id
     */
    List<String> listOfUserId(String corpId, String[] departments);

    /**
     * 查询权限下的员工，重载上述方法
     *
     * @param corpId
     * @param departments
     * @return
     */
    public List<String> listOfUserId(String corpId, String departments);

    /**
     * 查询权限下的员工，重载上述方法
     *
     * @param corpId
     * @param departments
     * @return
     */
    public List<String> listOfUserId(String corpId, List<Long> departments);

    /**
     * 查询通讯录相关客户
     *
     * @param corpId 企业ID
     * @param userId 通讯录相关客户ID
     * @return 通讯录相关客户
     */
    WeUser selectWeUserById(String corpId, String userId);

    /**
     * 获取成员详情
     *
     * @param corpId 企业ID
     * @param userId 成员ID
     * @return {@link WeUser}
     */
    WeUser getUserDetail(String corpId, String userId);

    /**
     * 查询通讯录相关客户列表  （校验数据范围)
     *
     * @param weUser 通讯录相关客户
     * @return 通讯录相关客户集合
     */
    List<WeUser> selectWeUserList(WeUser weUser);

    /**
     * 查询员工简略信息(名字和id)列表 (不校验数据范围)
     *
     * @param weUser
     * @return
     */
    List<WeUserBriefInfoVO> selectWeUserBriefInfo(WeUser weUser);

    /**
     * 新增通讯录相关客户
     *
     * @param weUser 通讯录相关客户
     */
    void insertWeUser(WeUser weUser);

    /**
     * 新增通讯录相关客户(不同步企微)
     *
     * @param weUser 通讯录相关客户
     * @return
     */
    int insertWeUserNoToWeCom(WeUser weUser);

    /**
     * 修改通讯录相关客户
     *
     * @param weUser 通讯录相关客户
     */
    void updateWeUser(WeUser weUser);

    /**
     * 修改通讯录相关客户(不同步企微)
     *
     * @param weUser 通讯录相关客户
     * @return 受影响行
     */
    int updateWeUserNoToWeCom(WeUser weUser);

    /**
     * 从企业微信获取员工数据更新到本地
     *
     * @param userId 用户ID
     * @param corpId 企业ID
     * @return 受影响行
     */
    int updateWeUserDataFromWeCom(String userId, String corpId);


    /**
     * 启用或禁用用户
     *
     * @param weUser 员工
     */
    void startOrStop(WeUser weUser);


    /**
     * 同步成员
     *
     * @param corpId 公司ID
     */
    void syncWeUser(String corpId, LoginUser loginUser);

    /**
     * 同步成员
     *
     * @param corpId 公司ID
     */
    void syncWeUser(String corpId);

    /**
     * 获取可见的员工详情
     *
     * @param corpId 企业ID
     * @return 可见员工列表
     */
    List<WeUser> getVisibleUser(String corpId);

    /**
     * 同步离职员工 第二版
     *
     * @param corpId            企业id
     * @param userIdInDbMap    数据中存在的员工userId
     */
    void syncWeLeaveUserV2(String corpId, Map<String, String> userIdInDbMap);

    /**
     * 删除用户
     *
     * @param corpId 企业ID
     * @param ids    待删除的用户di
     */
    void deleteUser(String corpId, String[] ids);

    /**
     * 删除成员
     *
     * @param userId 成员id
     * @param corpId 企业id
     * @return 修改成功数量，如果大于0则成功
     */
    int deleteUserNoToWeCom(String userId, String corpId);

    /**
     * 删除本地未删的离职员工
     *
     * @param corpId 公司ID
     * @param ids    员工id列表
     */
    void deleteUsersNoToWeCom(String corpId, List<String> ids);

    /**
     * 获取访问用户身份(内部应用)
     *
     * @param code    code
     * @param agentId 应用id
     * @param corpId  企业id
     * @return WeUserInfoVO
     */
    WeUserInfoVO getUserInfo(String code, String agentId, String corpId);


    /**
     * 根据客户id获取客户添加人
     *
     * @param corpId         公司Id
     * @param externalUserid 客户id
     * @return List<WeCustomerAddUser>
     */
    List<WeCustomerAddUser> findWeUserByCutomerId(String corpId, String externalUserid);

    /**
     * 为员工初始化角色 （批量）
     *
     * @param userList 企微员工集合
     */
    void batchInitRoleByDepartmentAndLeader(List<WeUser> userList);

    /**
     * 为员工初始化角色
     *
     * @param weUser 企微员工
     */
    void initRoleByDepartmentAndLeader(WeUser weUser);

    /**
     * 获取当前登录用户可操作的所有企业成员
     *
     * @param loginUser 登录用户实体
     * @return 可操作成员 列表
     */
    List<WeUser> getUserInDataScope(LoginUser loginUser);

    /**
     * 获取进入企业二维码
     *
     * @param corpId 企业ID
     * @return 二维码url
     */
    String getJoinQrCode(String corpId);

    /**
     * 批量修改员工信息
     *
     * @param batchUpdateUserInfoDTO batchUpdateUserInfoDTO
     * @return BatchUpdateUserInfoVO
     */
    BatchUpdateUserInfoVO batchUpdateUserInfo(BatchUpdateUserInfoDTO batchUpdateUserInfoDTO);

    /**
     * 校验成员是否存在并激活
     *
     * @param corpId 企业id
     * @param userId 成员id
     * @throws com.easyink.common.exception.CustomException
     */
    void validateActiveUser(String corpId, String userId);

    /**
     * 查看离职员工列表
     *
     * @param dto {@link TransferResignedUserListDTO }
     * @return {@link List<TransferResignedUserVO> }
     */
    List<TransferResignedUserVO> leaveUserListV3(TransferResignedUserListDTO dto);

    /**
     * 通过员工映射关系获得员工
     *
     * @param externalUserId 外部员工id
     * @param externalCorpId 外部企业id
     * @return {@link WeUser}
     */
    WeUser getWeUserByExternalMapping(String externalUserId, String externalCorpId);

    /**
     * 联系客户统计
     *
     * @param corpId    企业id
     */
    void getUserBehaviorDataByCorpId(String corpId, String time);

    /**
     * 获取加密后的userId
     *
     * @param corpId 企业id
     * @param userId 员工id
     * @return
     */
    String getOpenUserId(String corpId, String userId);

    /**
     * 获取用户信息
     *
     * @param users 员工id，用逗号分隔
     * @param corpId 企业id
     * @return 员工信息列表
     */
    List<UserVO> getUserInfo(String users, String corpId);

}
