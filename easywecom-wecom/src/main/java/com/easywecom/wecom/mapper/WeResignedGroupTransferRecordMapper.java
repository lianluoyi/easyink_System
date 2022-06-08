package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.dto.transfer.GetResignedTransferDetailDTO;
import com.easywecom.wecom.domain.entity.transfer.WeResignedGroupTransferRecord;
import com.easywecom.wecom.domain.vo.transfer.GetResignedTransferGroupDetailVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名: 离职继承群聊分配记录持久层映射
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:28
 */
@Repository
public interface WeResignedGroupTransferRecordMapper extends BaseMapper<WeResignedGroupTransferRecord> {
    /**
     * 批量插入/更新
     *
     * @param recordList
     * @return
     */
    void batchInsert(@Param("list") List<WeResignedGroupTransferRecord> recordList);

    /**
     * 获取历史分配的群聊详情记录
     *
     * @param dto {@link GetResignedTransferDetailDTO}
     * @return {@link List<GetResignedTransferGroupDetailVO>}
     */
    List<GetResignedTransferGroupDetailVO> listOfGroupRecord(GetResignedTransferDetailDTO dto);
}
