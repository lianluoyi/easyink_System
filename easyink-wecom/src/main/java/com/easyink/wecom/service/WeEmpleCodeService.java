package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.WeEmpleCodeUseScop;
import com.easyink.wecom.domain.dto.WeEmpleCodeDTO;
import com.easyink.wecom.domain.dto.WeExternalContactDTO;
import com.easyink.wecom.domain.dto.emplecode.AddWeEmpleCodeDTO;
import com.easyink.wecom.domain.dto.emplecode.FindAssistantDTO;
import com.easyink.wecom.domain.dto.emplecode.FindWeEmpleCodeDTO;
import com.easyink.wecom.domain.vo.*;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeByNameVO;

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
     * 查询新客进群列表
     *
     * @param weEmployCode {@link FindWeEmpleCodeDTO}
     * @return {@link List<WeEmpleCodeVO>}
     */
    List<WeEmpleCodeVO> selectGroupWeEmpleCodeList(FindWeEmpleCodeDTO weEmployCode);


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
    void insertWeEmpleCode(AddWeEmpleCodeDTO weEmpleCode);

    /**
     * 处理活码统计表数据
     *
     * @param userIdList 员工ID列表
     * @param corpId 企业ID
     * @param empleCodeId 活码ID
     */
    void handleEmpleStatisticData(List<String > userIdList, String corpId, Long empleCodeId);

    /**
     * 根据使用范围获取userId列表
     *
     * @param useScops 活码使用范围
     * @param corpId 企业ID
     * @return userId列表
     */
    List<String> getUserIdByScope(List<WeEmpleCodeUseScop> useScops, String corpId);

    /**
     * 保存附件顺序
     *
     * @param weEmpleCode
     */
    void buildMaterialSort(AddWeEmpleCodeDTO weEmpleCode);

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
     * @return
     */
    SelectWeEmplyCodeWelcomeMsgVO selectWelcomeMsgByState(String state, String corpId);

    /**
     * 通过id定位员工活码/获客链接
     *
     * @param id     员工活码ID/获客链接ID
     * @param corpId 企业ID
     * @return {@link SelectWeEmplyCodeWelcomeMsgVO}
     */
    SelectWeEmplyCodeWelcomeMsgVO selectWelcomeMsgById(String id, String corpId);

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
     * 构建获客链接普通欢迎语及附件
     *
     * @param messageMap {@link SelectWeEmplyCodeWelcomeMsgVO}
     * @param corpId     企业ID
     */
    void buildCustomerAssistantWelcomeMsg(SelectWeEmplyCodeWelcomeMsgVO messageMap, String corpId);

    /**
     * 构建兑换码活动欢迎语及附件
     *
     * @param messageMap
     * @param corpId
     * @param externalUserId
     */
    void buildRedeemCodeActivityWelcomeMsg(SelectWeEmplyCodeWelcomeMsgVO messageMap, String corpId, String externalUserId);

    /**
     * 获取活码小程序短链接
     *
     * @param id 活码id
     * @return short mini app link
     */
    String getCodeAppLink(Long id);

    /**
     * 活码统计-根据活动场景模糊查询活码信息
     *
     * @param dto {@link FindWeEmpleCodeDTO}
     * @return {@link EmpleCodeByNameVO}
     */
    List<EmpleCodeByNameVO> listByName(FindWeEmpleCodeDTO dto);

    /**
     * 获取有效的活码ID
     *
     * @param corpId 企业ID
     * @param date 日期，格式为YYYY-MM-DD
     * @return 活码ID列表
     */
    List<Long> getEffectEmpleCodeId(String corpId, String date);

    /**
     * 根据附件排序查找添加素材
     *
     * @param employCode
     * @param corpId
     */
    void buildEmployCodeMaterial(WeEmpleCodeVO employCode, String corpId);

    /**
     * 刷新活码
     */
    void refreshCode(List<Long> ids);
}
