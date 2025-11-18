package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.WeEmpleCodeUseScop;
import com.easyink.wecom.domain.dto.WeEmpleCodeDTO;
import com.easyink.wecom.domain.dto.WeExternalContactDTO;
import com.easyink.wecom.domain.dto.emplecode.*;
import com.easyink.wecom.domain.model.customer.CustomerId;
import com.easyink.wecom.domain.model.emplecode.State;
import com.easyink.wecom.domain.vo.*;
import com.easyink.wecom.domain.vo.emplecode.SelectTagVO;
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
     * @param userIdList  员工ID列表
     * @param corpId      企业ID
     * @param empleCodeId 活码ID
     */
    void handleEmpleStatisticData(List<String> userIdList, String corpId, Long empleCodeId);

    /**
     * 根据使用范围获取userId列表
     *
     * @param useScops 活码使用范围
     * @param corpId   企业ID
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
    EmplyCodeWelcomeMsgInfo selectWelcomeMsgByState(String state, String corpId);

    /**
     * 通过id定位员工活码/获客链接
     *
     * @param id     员工活码ID/获客链接ID
     * @param corpId 企业ID
     * @return {@link EmplyCodeWelcomeMsgInfo}
     */
    EmplyCodeWelcomeMsgInfo selectWelcomeMsgById(String id, String corpId);

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
    void buildCommonWelcomeMsg(EmplyCodeWelcomeMsgInfo messageMap, String corpId, String externalUserId);

    /**
     * 构建获客链接普通欢迎语及附件
     *
     * @param messageMap {@link EmplyCodeWelcomeMsgInfo}
     * @param corpId     企业ID
     */
    void buildCustomerAssistantWelcomeMsg(EmplyCodeWelcomeMsgInfo messageMap, String corpId);

    /**
     * 构建兑换码活动欢迎语及附件
     *
     * @param messageMap
     * @param corpId
     * @param externalUserId
     */
    void buildRedeemCodeActivityWelcomeMsg(EmplyCodeWelcomeMsgInfo messageMap, String corpId, String externalUserId);

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
     * @param date   日期，格式为YYYY-MM-DD
     * @return 活码ID列表
     */
    List<Long> getEffectEmpleCodeId(String corpId, String date);

    /**
     * 单独更新员工活码的标签组配置
     *
     * @param empleCodeId   员工活码ID
     * @param corpId        企业ID
     * @param tagGroupValid 标签组配置值
     * @return 更新结果
     */
    boolean updateTagGroupValid(Long empleCodeId, String corpId, Integer tagGroupValid);

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

    /**
     * 获取客户专属活码短链
     *
     * @param id 员工活码id
     * @return 短链
     */
    String getCustomerLink(Long id);

    /**
     * 生成客户专属活码二维码
     *
     * @param genCustomerEmployQrcodeDTO 包含生成二维码所需信息的数据传输对象
     * @return 生成的二维码链接
     */
    String genCustomerEmployQrCode(GenCustomerEmployQrcodeDTO genCustomerEmployQrcodeDTO);


    /**
     * 编辑专属活码的可选标签列表
     *
     * @param tagSelectScopeDTO 可选标签DTO
     * @param loginUser
     */
    void editCustomerEmployCodeTagSelectScope(TagSelectScopeDTO tagSelectScopeDTO, LoginUser loginUser);

    /**
     * 获取客户专属活码可选标签列表
     *
     * @param empleCodeId 员工活码id
     * @param loginUser   当前登录用户
     * @return 可选标签列表
     */
    SelectTagVO customerEmployCodeTagSelectScopeDetail(String empleCodeId, LoginUser loginUser);

    /**
     * 发送员工活码欢迎语
     * 包含:1:员工活码 2:客户专属活码
     * @param state 添加好友state值
     * @param welcomeCode 欢迎语code, 企微下发, 不下发则表示不能发送欢迎语,或者已经建立会话
     * @param customerId 客户id
     */
    void sendUserEmpleCodeWelcomeMsg(State state, String welcomeCode, CustomerId customerId, State originState);

    /**
     * 发送客户专属活码欢迎语
     * @param state 添加好友state值
     * @param welcomeCode 欢迎语code, 企微下发, 不下发则表示不能发送欢迎语,或者已经建立会话
     * @param customerId 客户id
     */
    void sendCustomerTempEmpleCodeWelcomeMsg(State state, String welcomeCode, CustomerId customerId);

    /**
     * 发送获客链接欢迎语
     * @param state 添加好友state值
     * @param welcomeCode 欢迎语code, 企微下发, 不下发则表示不能发送欢迎语,或者已经建立会话
     * @param customerId 客户id
     */
    void sendCustomerAssistantWelcomeMsg(State state, String welcomeCode, CustomerId customerId);

    /**
     * 客户专属活码回调处理
     * @param state 添加好友state值
     * @param customerId 客户id
     */
    void customerTempEmpleCodeCallBackHandle(State state, CustomerId customerId);

    /**
     * 员工活码回调处理
     * @param state 添加好友state值
     * @param customerId 客户id
     */
    void empleCodeCallBackHandle(State state, CustomerId customerId);
}
