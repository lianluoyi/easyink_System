package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.wecom.domain.WeCustomerAddUser;
import com.easywecom.wecom.domain.dto.QueryUserDTO;
import com.easywecom.wecom.domain.dto.transfer.TransferResignedUserListDTO;
import com.easywecom.wecom.domain.vo.WeUserVO;
import com.easywecom.wecom.domain.vo.transfer.TransferResignedUserVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通讯录相关客户Mapper接口
 *
 * @author admin
 * @date 2020-08-31
 */
@Repository
public interface WeUserMapper extends BaseMapper<WeUser> {
    /**
     * 查询员工信息
     * @param queryUserDTO 条件
     * @return vo
     */
    List<WeUserVO> listOfUser(QueryUserDTO queryUserDTO);

    /**
     * 查询员工信息
     * @param corpId 企业ID
     * @param userIdList 查询指定的员工ID
     * @return {@link List<WeUserVO>}
     */
    List<WeUserVO> listOfUser1(@Param("corpId")String corpId,@Param("userIdList")List<String> userIdList);

    /**
     * 查出员工信息（包含离职员工）
     *
     * @param corpId 企业ID
     * @param userId 员工Id
     * @return vo
     */
    WeUserVO getUser(@Param("corpId") String corpId, @Param("userId") String userId);

    /**
     * 查询通讯录相关客户
     *
     * @param corpId 企业ID
     * @param userId 通讯录相关客户ID
     * @return 通讯录相关客户
     */
    WeUser selectWeUserById(@Param("corpId") String corpId, @Param("userId") String userId);

    /**
     * 查询通讯录相关客户和其角色信息
     *
     * @param userId 通讯录相关客户ID
     * @return 通讯录相关客户
     */
    WeUser selectWeUserWithRoleInfoById(@Param("userId") String userId);

    /**
     * 查询通讯录相关客户列表
     *
     * @param weUser 通讯录相关客户
     * @return 通讯录相关客户集合
     */
    List<WeUser> selectWeUserList(WeUser weUser);

    /**
     * 新增通讯录相关客户
     *
     * @param weUser 通讯录相关客户
     * @return 结果
     */
    int insertWeUser(WeUser weUser);

    /**
     * 修改通讯录相关客户
     *
     * @param weUser 通讯录相关客户
     * @return 结果
     */
    int updateWeUser(WeUser weUser);

    /**
     * 删除当前表的员工
     *
     * @return
     */
    int deleteWeUser();


    /**
     * 批量插入员工
     *
     * @param weUsers
     * @return
     */
    int batchInsertWeUser(@Param("weUsers") List<WeUser> weUsers);

    /**
     * 批量修改员工
     * @param weUsers 条件
     * @return 受影响行
     */
    int batchUpdateWeUser(@Param("weUsers") List<WeUser> weUsers);

    /**
     * 根据客户id获取客户添加人
     *
     *
     * @param corpId
     * @param externalUserid
     * @return
     */
    List<WeCustomerAddUser> findWeUserByCutomerId(@Param("corpId") String corpId, @Param("externalUserid") String externalUserid);

    /**
     * 获取数据权限下员工id
     * @param corpId 公司id
     * @param departments 权限下部门
     * @return 员工id
     */
    List<String> listOfUserId(@Param("corpId") String corpId, @Param("array") String[] departments);

    /**
     * 通过部门id列表和公司Id获取成员
     *
     * @param corpId 公司id
     * @param array  部门id集合
     * @return
     */
    List<WeUser> getUserByDepartmentList(@Param("corpId") String corpId, @Param("array") String[] array);

    /**
     * 批量修改员工职位或 部门
     *
     * @param corpId     企业ID
     * @param userIdList 员工ID列表
     * @param position   职位
     * @param department 部门
     * @return int
     */
    int batchUpdateWeUserPositionOrDepartment(@Param("corpId") String corpId, @Param("userIdList") List<String> userIdList, @Param("position") String position, @Param("department") Long department);

    /**
     * 获取成员详情
     *
     * @param corpId 企业ID
     * @param userId 成员ID
     * @return {@link WeUser}
     */
    WeUser getUserDetail(@Param("corpId") String corpId, @Param("userId") String userId);

    /**
     * 查看离职员工列表
     *
     * @param dto {@link TransferResignedUserListDTO}
     * @return {@link List<TransferResignedUserVO>}
     */
    List<TransferResignedUserVO> leaveUserListV3(TransferResignedUserListDTO dto);

    /**
     * 批量插入 更新员工状态和离职时间
     *
     * @param updateUserList {@link List<WeUser>}
     */
    Integer batchInsertUpdateUserStatus(@Param("list") List<WeUser> updateUserList);
}
