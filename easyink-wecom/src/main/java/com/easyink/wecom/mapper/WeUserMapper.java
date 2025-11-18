package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.wecom.domain.WeCustomerAddUser;
import com.easyink.wecom.domain.dto.QueryUserDTO;
import com.easyink.wecom.domain.dto.transfer.TransferResignedUserListDTO;
import com.easyink.wecom.domain.vo.WeEmpleCodeVO;
import com.easyink.wecom.domain.vo.WeUserVO;
import com.easyink.wecom.domain.vo.transfer.TransferResignedUserVO;
import com.easyink.wecom.service.impl.WeUserDataMigrationService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
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
     *
     * @param queryUserDTO 条件
     * @return vo
     */
    List<WeUserVO> listOfUser(QueryUserDTO queryUserDTO);

    /**
     * 查询员工信息
     *
     * @param corpId     企业ID
     * @param userIdList 查询指定的员工ID
     * @return {@link List<WeUserVO>}
     */
    List<WeUserVO> listOfUser1(@Param("corpId") String corpId, @Param("userIdList") List<String> userIdList);

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
     * 批量修改员工
     *
     * @param weUsers 条件
     * @return 受影响行
     */
    int batchUpdateWeUser(@Param("weUsers") List<WeUser> weUsers);

    /**
     * 根据客户id获取客户添加人
     *
     * @param corpId
     * @param externalUserid
     * @return
     */
    List<WeCustomerAddUser> findWeUserByCutomerId(@Param("corpId") String corpId, @Param("externalUserid") String externalUserid);

    /**
     * 获取数据权限下员工id
     *
     * @param corpId      公司id
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
     * 批量更新员工的隐私信息
     *
     * @param list 待更新列表
     * @return affected rows
     */
    Integer batchUpdateUserPrivacy(@Param("list") List<WeUser> list);

    /**
     * 根据创建人名称列表和企业ID获取创建人对应的主部门名称
     *
     * @param userNameList 创建人名称列表
     * @param corpId       企业ID
     * @return 主部门名称信息列表
     */
    List<WeEmpleCodeVO> selectUserMainDepartmentByUsername(@Param("userNameList") List<String> userNameList, @Param("corpId") String corpId);

    /**
     * 根据userId列表获取员工名称和部门信息
     * 说明：员工id列表不能为空
     *
     * @param userIdList 员工id列表
     * @param corpId     企业id
     * @return {@link List<WeUserVO>}
     */
    List<WeUserVO> selectWeUserInfoByUserIdList(@NotNull @Param("userIdList") List<String> userIdList, @Param("corpId") String corpId);

    /**
     * 根据企业id查询员工id列表
     * @param corpId 企业id
     * @return 员工id列表
     */
    List<String> selectByCorpId(@Param("corpId") String corpId);

    // =====================================================
    // 数据迁移相关方法
    // =====================================================

    /**
     * 分批查询需要迁移的用户数据
     * 查询有明文敏感字段但缺少对应加密字段的用户
     *
     * @param offset 偏移量
     * @param limit  限制数量
     * @return 需要迁移的用户列表
     */
    List<WeUser> selectUsersForMigration(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 批量更新用户的加密字段
     *
     * @param users 用户列表
     * @return 更新的记录数
     */
    int batchUpdateEncryptFields(@Param("users") List<WeUser> users);

    /**
     * 获取迁移统计信息
     *
     * @return 迁移统计信息
     */
    WeUserDataMigrationService.MigrationStatistics getMigrationStatistics();

    /**
     * 查询遗漏加密的记录
     * 有明文数据但缺少加密数据的记录
     *
     * @return 遗漏的记录列表
     */
    List<WeUser> selectMissedEncryptionRecords();
}
