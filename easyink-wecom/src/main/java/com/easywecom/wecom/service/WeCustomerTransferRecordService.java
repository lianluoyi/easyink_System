package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.wecom.domain.WeCustomer;
import com.easywecom.wecom.domain.dto.transfer.TransferCustomerDTO;
import com.easywecom.wecom.domain.dto.transfer.TransferRecordPageDTO;
import com.easywecom.wecom.domain.entity.transfer.WeCustomerTransferRecord;
import com.easywecom.wecom.domain.vo.customer.WeCustomerVO;
import com.easywecom.wecom.domain.vo.transfer.WeCustomerTransferRecordVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类名: 在职继承分配记录表业务层接口
 *
 * @author : silver_chariot
 * @date : 2021/11/29 17:54
 */
@Service
public interface WeCustomerTransferRecordService extends IService<WeCustomerTransferRecord> {

    /**
     * 在职继承: 分配客户
     *
     * @param corpId             企业ID
     * @param transferList       {@link TransferCustomerDTO.HandoverCustomer}
     * @param takeoverUserid     接替人userId
     * @param transferSuccessMsg 转移成功后发给客户的消息，最多200个字符，不填则使用默认文案
     */
    void transfer(String corpId, TransferCustomerDTO.HandoverCustomer[] transferList, String takeoverUserid, String transferSuccessMsg);

    /**
     * 在职继承：分配客户
     *
     * @param corpId             企业ID
     * @param handoverUser       原跟进成员信息
     * @param takeoverUser       接替成员信息
     * @param externalUserIds    客户userId集合
     * @param transferSuccessMsg 转移成功后发给客户的消息，最多200个字符，不填则使用默认文案
     */
    void doTransfer(String corpId, WeUser handoverUser, WeUser takeoverUser, List<String> externalUserIds, String transferSuccessMsg);

    /**
     * 批量更新
     *
     * @param totalList {@link List<WeCustomerTransferRecord>}
     */
    void batchUpdate(List<WeCustomerTransferRecord> totalList);

    /**
     * 获取在职继承的客户列表
     *
     * @param weCustomer {@link WeCustomer}
     * @return 客户列表
     */
    List<WeCustomerVO> transferCustomerList(WeCustomer weCustomer);

    /**
     * 获取客户分配记录
     *
     * @param dto {@Link TransferRecordPageDTO}
     * @return {@link List<WeCustomerTransferRecord>}
     */
    List<WeCustomerTransferRecordVO> getList(TransferRecordPageDTO dto);

    /**
     * 处理在职继承失败事件
     *
     * @param corpId         企业ID
     * @param userId         成员ID
     * @param externalUserId 外部联系人ID
     * @param failReason     失败原因
     */
    void handleTransferFail(String corpId, String userId, String externalUserId, String failReason);

    /**
     * 处理在职继承成功事件
     *
     * @param corpId         企业ID
     * @param userId         成员ID
     * @param externalUserId 外部联系人ID
     */
    void handleTransferSuccess(String corpId, String userId, String externalUserId);
}
