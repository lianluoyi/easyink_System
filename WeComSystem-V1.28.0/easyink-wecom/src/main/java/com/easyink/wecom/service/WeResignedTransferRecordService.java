package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.dto.transfer.GetResignedTransferDetailDTO;
import com.easyink.wecom.domain.dto.transfer.GetUnassignedListResp;
import com.easyink.wecom.domain.dto.transfer.TransferResignedUserDTO;
import com.easyink.wecom.domain.dto.transfer.TransferResignedUserListDTO;
import com.easyink.wecom.domain.entity.transfer.WeResignedTransferRecord;
import com.easyink.wecom.domain.vo.transfer.GetResignedTransferCustomerDetailVO;
import com.easyink.wecom.domain.vo.transfer.GetResignedTransferGroupDetailVO;
import com.easyink.wecom.domain.vo.transfer.TransferResignedUserVO;

import java.util.Date;
import java.util.List;

/**
 * 类名: 离职继承总记录业务层接口
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:33
 */
public interface WeResignedTransferRecordService extends IService<WeResignedTransferRecord> {
    /**
     * 分配离职员工的客户和客户群 (批量)
     *
     * @param corpId           企业id
     * @param handoverUserList 待分配的员工列表
     * @param takeoverUserid   接替成员id
     * @param chatId           指定分配的群聊id (不传则默认 分配员工所有可分配的群聊)
     * @param externalUserid   指定的分配的外部联系人id (不传则默认 分配员工所有可分配的客户)
     * @return
     */
    void transfer(String corpId, List<TransferResignedUserDTO.LeaveUserDetail> handoverUserList, String takeoverUserid, String chatId, String externalUserid);

    /**
     * 分配离职员工的客户和客户群
     *
     * @param corpId         企业id
     * @param handoverUser 待分配的员工id
     * @param takeoverUserId 接替成员id
     * @param unassignedList 待分配员工列表 (从企微API获取)
     * @param chatId         指定分配的群聊id (不传则默认 分配员工所有可分配的群聊)
     * @param externalUserid 指定的分配的外部联系人id (不传则默认 分配员工所有可分配的客户)
     * @return
     */
    void transfer(String corpId, TransferResignedUserDTO.LeaveUserDetail handoverUser, String takeoverUserId, List<GetUnassignedListResp.UnassignedInfo> unassignedList, String chatId, String externalUserid);

    /**
     * 离职继承群聊
     *
     * @param corpId         企业id
     * @param handoverUserId 待分配的员工id
     * @param takeoverUserId 接替成员id
     * @param chatId         指定分配的群聊Id
     * @param recordId       离职继承主记录id
     */
    void transferGroup(String corpId, String handoverUserId, String takeoverUserId, String chatId, Long recordId);

    /**
     * 分配离职员工客户
     *
     * @param corpId             企业ID
     * @param handoverUserId     原跟进人
     * @param takeoverUserId     接替人userID
     * @param externalUserIdList 待分配客户列表
     * @param recordId
     */
    void transferCustomer(String corpId, String handoverUserId, String takeoverUserId, List<String> externalUserIdList, Long recordId);

    /**
     * 获取可分配员工的客户列表
     *
     * @param corpId 企业ID
     * @return {@link List<GetUnassignedListResp.UnassignedInfo> }
     * @throws com.easyink.common.exception.CustomException 不存在可分配客户异常
     */
    List<GetUnassignedListResp.UnassignedInfo> getUnassignedList(String corpId);

    /**
     * 获取离职待分配列表
     *
     * @param dto {@link TransferResignedUserListDTO}
     * @return
     */
    List<TransferResignedUserVO> listOfRecord(TransferResignedUserListDTO dto);

    /**
     * 处理继承失败
     *
     * @param corpId         企业id
     * @param userId         成员id
     * @param externalUserId 外部联系人id
     * @param failReason     失败原因
     */
    void handleTransferFail(String corpId, String userId, String externalUserId, String failReason);

    /**
     * 获取已分配客户详情记录
     *
     * @param dto {@link GetResignedTransferDetailDTO}
     * @return {@link  List< GetResignedTransferCustomerDetailVO >}
     */
    List<GetResignedTransferCustomerDetailVO> listOfCustomerRecord(GetResignedTransferDetailDTO dto);

    /**
     * 获取历史已分配的客户群
     *
     * @param dto {@link GetResignedTransferDetailDTO}
     * @return {@link  List<GetResignedTransferGroupDetailVO>}
     */
    List<GetResignedTransferGroupDetailVO> listOfGroupRecord(GetResignedTransferDetailDTO dto);

    /**
     * 获取记录
     *
     * @param corpId         企业id
     * @param handoverUserId 跟进人userid
     * @param takeoverUserId 接替人userid
     * @param dimissionTime  离职时间
     * @return {@link WeResignedTransferRecord}
     */
    WeResignedTransferRecord getRecord(String corpId, String handoverUserId, String takeoverUserId, Date dimissionTime);
}
