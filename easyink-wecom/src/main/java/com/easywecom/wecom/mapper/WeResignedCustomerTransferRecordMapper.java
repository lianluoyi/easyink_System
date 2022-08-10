package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.dto.transfer.GetResignedTransferDetailDTO;
import com.easywecom.wecom.domain.entity.transfer.WeResignedCustomerTransferRecord;
import com.easywecom.wecom.domain.vo.transfer.GetResignedTransferCustomerDetailVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名: 离职继承客户分配记录持久层映射
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:30
 */
@Repository
public interface WeResignedCustomerTransferRecordMapper extends BaseMapper<WeResignedCustomerTransferRecord> {
    /**
     * 根据跟进人和客户id获取分配记录详情
     *
     * @param corpId         企业id
     * @param userId         跟进人id
     * @param externalUserId 外部联系人id
     * @return  {@link WeResignedCustomerTransferRecord} 对应的分配记录
     */
    WeResignedCustomerTransferRecord getByHandoverUserAndExternalUser(@Param("corpId") String corpId, @Param("handoverUserid") String userId,
                                                                      @Param("externalUserid") String externalUserId);

    /**
     * 更新分配客户状态
     *
     * @param record {@link WeResignedCustomerTransferRecord}
     */
    void updateRecord(WeResignedCustomerTransferRecord record);

    /**
     * 批量插入/更新客户接替记录的状态
     *
     * @param list {@link List<WeResignedCustomerTransferRecord}
     */
    void batchUpdateRecordStatus(@Param("list") List<WeResignedCustomerTransferRecord> list);

    /**
     * 获取所有待接替的客户列表
     *
     * @return {@link  List<WeResignedCustomerTransferRecord>}
     */
    List<WeResignedCustomerTransferRecord> getToBeTransferList();

    /**
     * 获取分配客户详情接口
     *
     * @param dto {@link GetResignedTransferDetailDTO}
     * @return {@link  List< GetResignedTransferCustomerDetailVO > }
     */
    List<GetResignedTransferCustomerDetailVO> listOfCustomerRecord(GetResignedTransferDetailDTO dto);
}
