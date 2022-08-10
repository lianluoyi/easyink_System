package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeCustomerSeedMessage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 群发消息  群发消息  子消息表(包括 文本消息、图片消息、链接消息、小程序消息)   Mapper接口
 *
 * @author admin
 * @date 2020-12-28
 */
@Repository
public interface WeCustomerSeedMessageMapper extends BaseMapper<WeCustomerSeedMessage> {
    /**
     * 删除
     *
     * @param messageId 消息id
     * @param corpId    企业id
     * @return 受影响行数
     */
    int deleteByMessageId(@Param("messageId") Long messageId, @Param("corpId") String corpId);
}
