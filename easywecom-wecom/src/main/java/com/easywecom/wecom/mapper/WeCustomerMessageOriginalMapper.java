package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeCustomerMessageOriginal;
import com.easywecom.wecom.domain.dto.WeCustomerMessageDTO;
import com.easywecom.wecom.domain.vo.CustomerMessagePushVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群发消息 原始数据信息表 Mapper接口
 *
 * @author admin
 * @date 2020-12-08
 */
@Repository
public interface WeCustomerMessageOriginalMapper extends BaseMapper<WeCustomerMessageOriginal> {

    /**
     * 群发消息列表
     *
     * @param weCustomerMessageDTO  查询条件
     * @return {@link CustomerMessagePushVO}s
     */
    List<CustomerMessagePushVO> selectCustomerMessagePushs(WeCustomerMessageDTO weCustomerMessageDTO);

    /**
     * 群发详情
     *
     * @param messageId 微信群发id
     * @param corpId 企业id
     * @return {@link CustomerMessagePushVO} 群发详情
     */
    CustomerMessagePushVO findCustomerMessagePushDetail(@Param("messageId") Long messageId, @Param("corpId") String corpId);

}
