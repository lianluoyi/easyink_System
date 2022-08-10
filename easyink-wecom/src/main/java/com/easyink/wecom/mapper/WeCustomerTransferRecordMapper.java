package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.dto.transfer.TransferRecordPageDTO;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferRecord;
import com.easyink.wecom.domain.vo.transfer.WeCustomerTransferRecordVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名: 在职继承分配记录表持久层映射
 *
 * @author : silver_chariot
 * @date : 2021/11/29 17:31
 */
@Repository
public interface WeCustomerTransferRecordMapper extends BaseMapper<WeCustomerTransferRecord> {
    /**
     * 批量更新
     *
     * @param totalList {@link List<WeCustomerTransferRecord>}
     */
    void batchUpdate(@Param("list") List<WeCustomerTransferRecord> totalList);

    /**
     * 获取分配记录列表
     *
     * @param dto {@link TransferRecordPageDTO}
     * @return {@link List<WeCustomerTransferRecord>} 分配记录列表
     */
    List<WeCustomerTransferRecordVO> getList(TransferRecordPageDTO dto);
}
