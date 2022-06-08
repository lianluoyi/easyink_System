package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.dto.transfer.TransferResignedUserListDTO;
import com.easywecom.wecom.domain.entity.transfer.WeResignedCustomerTransferRecord;
import com.easywecom.wecom.domain.entity.transfer.WeResignedTransferRecord;
import com.easywecom.wecom.domain.vo.transfer.TransferResignedUserVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 类名: 离职继承总记录数据库映射
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:28
 */
@Repository
public interface WeResignedTransferRecordMapper extends BaseMapper<WeResignedTransferRecord> {
    /**
     * 批量插入、更新
     *
     * @param list @{link List<WeResignedCustomerTransferRecord> }
     */
    void batchInsert(@Param("list") List<WeResignedCustomerTransferRecord> list);

    /**
     * 获取分配总记录
     *
     * @param corpId         企业id
     * @param handoverUserId 原跟进人userId
     * @param takeoverUserId 接替人userId
     * @param dimissionTime  跟进人离职时间
     * @return {@link WeResignedTransferRecord}
     */
    WeResignedTransferRecord get(@Param("corpId") String corpId, @Param("handoverUserid") String handoverUserId,
                                 @Param("takeoverUserid") String takeoverUserId, @Param("dimissionTime") Date dimissionTime);

    /**
     * 分配记录列表
     *
     * @param dto {@link TransferResignedUserListDTO }
     * @return {@link List<TransferResignedUserVO>}
     */
    List<TransferResignedUserVO> listOfRecord(TransferResignedUserListDTO dto);


}
