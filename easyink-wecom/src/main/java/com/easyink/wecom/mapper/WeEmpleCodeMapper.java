package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.dto.WeEmpleCodeDTO;
import com.easyink.wecom.domain.dto.emplecode.FindAssistantDTO;
import com.easyink.wecom.domain.dto.emplecode.FindWeEmpleCodeDTO;
import com.easyink.wecom.domain.vo.*;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeByNameVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 员工活码Mapper接口
 *
 * @author admin
 * @date 2020-10-04
 */
@Repository
@Mapper
public interface WeEmpleCodeMapper extends BaseMapper<WeEmpleCode> {
    /**
     * 查询员工活码
     *
     * @param id     员工活码ID
     * @param corpId 企业id
     * @return {@link WeEmpleCode}
     */
    WeEmpleCodeVO selectWeEmpleCodeById(@Param("id") Long id, @Param("corpId") String corpId);

    /**
     * 查询员工活码列表
     *
     * @param weEmpleCode 员工活码
     * @return {@link List<GetWeEmployCodeVO>}
     */
    List<WeEmpleCodeVO> selectWeEmpleCodeList(FindWeEmpleCodeDTO weEmpleCode);

    /**
     * 查询获客链接列表
     *
     * @param assistantDTO {@link FindAssistantDTO}
     * @return {@link List< GetWeEmployCodeVO >}
     */
    List<WeEmpleCodeVO> selectAssistantList(FindAssistantDTO assistantDTO);

    /**
     * 新增员工活码
     *
     * @param weEmpleCode 员工活码
     * @return 结果
     */
    int insertWeEmpleCode(WeEmpleCode weEmpleCode);

    /**
     * 修改员工活码
     *
     * @param weEmpleCode 员工活码
     * @return 结果
     */
    int updateWeEmpleCode(WeEmpleCode weEmpleCode);

    /**
     * 批量逻辑删除员工活码
     *
     * @param corpId 授权企业ID
     * @param ids    需要删除的数据ID
     * @return 结果
     */
    int batchRemoveWeEmpleCodeIds(@Param("corpId") String corpId, @Param("ids") List<Long> ids);

    /**
     * 批量获取员工活码config_Id
     * @param corpId 授权企业ID
     * @param ids 需要获取的数据ID
     * @return 结果
     */
    List<String> batchGetWeEmpleCodeConfigId(@Param("corpId") String corpId, @Param("ids") List<Long> ids);

    /**
     * 通过活动场景获取客户欢迎语
     *
     * @param scenario 活动场景
     * @param userId   成员ID
     * @param corpId   企业ID
     * @return
     */
    WeEmpleCodeDTO selectWelcomeMsgByScenario(@Param("scenario") String scenario, @Param("userId") String userId, @Param("corpId") String corpId);

    /**
     * 通过state定位员工活码
     *
     * @param state  state
     * @param corpId 企业Id
     * @return {@link WeEmpleCodeDTO}
     */
    EmplyCodeWelcomeMsgInfo selectWelcomeMsgByState(@Param("state") String state, @Param("corpId") String corpId);

    /**
     * 通过id定位员工活码/获客链接
     *
     * @param id     员工活码ID/获客链接ID
     * @param corpId 企业ID
     * @return {@link EmplyCodeWelcomeMsgInfo}
     */
    EmplyCodeWelcomeMsgInfo selectWelcomeMsgById(@Param("id") String id, @Param("corpId") String corpId);

    /**
     * 根据HHmm查询时间段通过好友的员工活码数据
     *
     * @param HHmm
     * @return List<WeEmpleCode>
     */
    List<WeEmpleCode> getWeEmpleCodeByTime(@Param("HHmm") String HHmm);

    /**
     * 查询下载员工活码需要的数据
     *
     * @param corpId 企业ID
     * @param idList 活码主键列表
     * @return List<WeEmplyCodeDownloadVO>
     */
    List<WeEmplyCodeDownloadVO> downloadWeEmplyCodeData(@Param("corpId") String corpId, @Param("idList") List<Long> idList);

    /**
     * 根据corpId和id查询使用人员和之前有新增/删除客户的员工
     *
     * @param corpId 企业ID
     * @param id     活码ID
     */
    List<WeEmplyCodeScopeUserVO> getUserByEmplyCodeId(@Param("corpId") String corpId, @Param("id") Long id);

    /**
     * 物理删除活码
     *
     * @param corpId
     * @param id     活码id
     */
    void deleteWeEmpleCode(@Param("corpId") String corpId, @Param("id") Long id);

    /**
     * 通过活码使用范围查找部门下的员工
     *
     * @param corpId
     * @param id     活码id
     * @return
     */
    List<WeEmplyCodeScopeUserVO> getUserFromDepartmentByEmplyCodeId(String corpId, Long id);

    /**
     * 活码统计-根据活动场景模糊查询活码信息
     *
     * @param dto {@link FindWeEmpleCodeDTO}
     * @return {@link EmpleCodeByNameVO}
     */
    List<EmpleCodeByNameVO> listByName(FindWeEmpleCodeDTO dto);

    /**
     * 根据链接id获取链接名称和创建人信息（已删除的也能获取）
     *
     * @param linkId 获客链接ID
     * @param corpId 企业ID
     * @return 获客链接名称
     */
    WeEmpleCode getAssistantByLinkId(@Param("linkId") String linkId, @Param("corpId") String corpId);

    /**
     * 根据链接id删除获客链接（逻辑删除）
     *
     * @param linkId 获客链接ID
     * @param corpId 企业ID
     * @return 结果
     */
    Integer removeAssistantByLinkId(@Param("linkId") String linkId, @Param("corpId") String corpId);

    /**
     * 查询需要处理的部门活码id列表
     * @return id列表
     */
    List<Long> selectDepartTypeEmpleCodeIdList();


    /**
     * 查询包含逻辑删除的活码
     * @param empleCodeId 活码id
     * @param corpId 企业id
     * @return
     */
    WeEmpleCode selectIgnoreDelFlag(@Param("empleCodeId") Long empleCodeId, @Param("corpId") String corpId);
}
