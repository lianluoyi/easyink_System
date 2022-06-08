package com.easywecom.wecom.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.OrderUserToOrderAccountEntity;
import com.easywecom.wecom.domain.dto.BindDetailDTO;
import com.easywecom.wecom.domain.vo.BindDetailVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 企业员工与工单帐号绑定关系表
 *
 * @author 佚名
 * @date 2021-12-13 18:43:30
 */
@Repository
public interface OrderUserToOrderAccountMapper extends BaseMapper<OrderUserToOrderAccountEntity> {
    /**
     * 获取绑定详情
     *
     * @param bindDetailDTO 查询条件
     * @return {@link List<BindDetailVO>}
     */
    List<BindDetailVO> listOfBindDetail(BindDetailDTO bindDetailDTO);
}
