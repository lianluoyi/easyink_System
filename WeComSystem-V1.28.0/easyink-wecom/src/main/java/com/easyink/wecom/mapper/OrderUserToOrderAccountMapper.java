package com.easyink.wecom.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.OrderUserToOrderAccountEntity;
import com.easyink.wecom.domain.dto.BindDetailDTO;
import com.easyink.wecom.domain.vo.BindDetailVO;
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
