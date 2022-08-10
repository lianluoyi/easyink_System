package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeEmpleCode;
import com.easywecom.wecom.domain.dto.WeEmpleCodeDTO;
import com.easywecom.wecom.domain.dto.WeExternalContactDTO;
import com.easywecom.wecom.domain.dto.emplecode.AddWeEmpleCodeDTO;
import com.easywecom.wecom.domain.dto.emplecode.FindWeEmpleCodeDTO;
import com.easywecom.wecom.domain.dto.emplecode.UpdateWeEmplyCodeDTO;
import com.easywecom.wecom.domain.vo.*;

import java.util.List;

/**
 * 员工活码Service接口
 *
 * @author admin
 * @date 2020-10-04
 */
public interface WeEmpleCodeService extends IService<WeEmpleCode> {
    /**
     * 查询员工活码
     *
     * @param id     员工活码ID
     * @param corpId 企业Id
     * @return {@link WeEmpleCode}
     */
    WeEmpleCodeVO selectWeEmpleCodeById(Long id, String corpId);

    /**
     * 查询员工活码列表
     *
     * @param weEmpleCode 员工活码
     * @return {@link List<GetWeEmployCodeVO>}
     */
    List<WeEmpleCodeVO> selectWeEmpleCodeList(FindWeEmpleCodeDTO weEmpleCode);

    /**
     * 新增员工活码
     *
     * @param weEmpleCode 员工活码
     * @return 结果
     */
    void insertWeEmpleCode(AddWeEmpleCodeDTO weEmpleCode);

    /**
     * 修改员工活码
     *
     * @param weEmpleCode 员工活码
     * @return 结果
     */
    void updateWeEmpleCode(AddWeEmpleCodeDTO weEmpleCode);

    /**
     * 批量逻辑删除员工活码
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int batchRemoveWeEmpleCodeIds(String corpId, List<Long> ids);

    /**
     * 通过活动场景获取客户欢迎语
     *
     * @param scenario 活动场景
     * @param userId   成员id
     * @param corpId   企业ID
     * @return
     */
    WeEmpleCodeDTO selectWelcomeMsgByScenario(String scenario, String userId, String corpId);

    /**
     * 通过state定位员工活码
     *
     * @param state
     * @param corpId
     * @param externalUserId
     * @return
     */
    SelectWeEmplyCodeWelcomeMsgVO selectWelcomeMsgByState(String state, String corpId, String externalUserId);

    /**
     * 获取员工二维码
     *
     * @param userIds       员工id
     * @param departmentIds 部门id
     * @param corpId        企业ID
     */
    WeExternalContactDTO getQrcode(String userIds, String departmentIds, String corpId);

    /**
     * 获取员工二维码
     *
     * @param userIdArr       员工id
     * @param departmentIdArr 部门id
     * @param corpId          企业ID
     */
    WeExternalContactDTO getQrcode(String[] userIdArr, Long[] departmentIdArr, String corpId);

    /**
     * 查询开始时间或结束时间为HHm的数据
     *
     * @param HHmm HHmm
     * @return List<WeEmpleCode>
     */
    List<WeEmpleCode> getWeEmpleCodeByEffectTime(String HHmm);

    /**
     * 查询下载员工活码需要的数据
     *
     * @param corpId 企业ID
     * @param idList 活码主键列表
     * @return List<WeEmplyCodeDownloadVO>
     */
    List<WeEmplyCodeDownloadVO> downloadWeEmplyCodeData(String corpId, List<Long> idList);


    /**
     * 获取员工活码下的使用员工+以前有新增/删除客户的员工
     *
     * @param corpId 企业ID
     * @param id     活码ID
     */
    List<WeEmplyCodeScopeUserVO> getUserByEmplyCode(String corpId, Long id);

    /**
     * 构建普通欢迎语及附件
     *
     * @param messageMap
     * @param corpId
     * @param externalUserId
     */
    void buildCommonWelcomeMsg(SelectWeEmplyCodeWelcomeMsgVO messageMap, String corpId, String externalUserId);

    /**
     * 构建兑换码活动欢迎语及附件
     *
     * @param messageMap
     * @param corpId
     * @param externalUserId
     */
    void buildRedeemCodeActivityWelcomeMsg(SelectWeEmplyCodeWelcomeMsgVO messageMap, String corpId, String externalUserId);
}
