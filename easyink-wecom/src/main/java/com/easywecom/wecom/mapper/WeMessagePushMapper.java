package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeMessagePush;
import org.springframework.stereotype.Repository;

/**
 * 消息发送的对象 we_message_push
 *
 * @author admin
 * @date 2020-10-28
 */
@Repository
public interface WeMessagePushMapper extends BaseMapper<WeMessagePush> {
}
