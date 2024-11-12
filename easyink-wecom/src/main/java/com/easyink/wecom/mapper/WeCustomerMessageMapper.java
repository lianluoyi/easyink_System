package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeCustomerMessage;
import com.easyink.wecom.domain.model.message.GroupMessageCountModel;
import com.easyink.wecom.domain.vo.WeCustomerSeedMessageVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群发消息  微信消息表Mapper接口
 *
 * @author admin
 * @date 2020-12-08
 */
@Repository
public interface WeCustomerMessageMapper extends BaseMapper<WeCustomerMessage> {

    int updateWeCustomerMessageMsgIdById(WeCustomerMessage customerMessage);

    /**
     * @param messageId  微信消息表主键id
     * @param actualSend 实际发送消息数（客户对应多少人 客户群对应多个群）
     * @return int
     */
    int updateWeCustomerMessageActualSend(@Param("messageId") Long messageId, @Param("actualSend") Integer actualSend);

    /**
     * 更新消息发送状态
     *
     * @param messageId id
     * @param status    消息发送状态 0 未发送  1 已发送
     * @return int
     */
    int updateWeCustomerMessageCheckStatusById(@Param("messageId") Long messageId, @Param("status") String status);

    /**
     * 删除
     *
     * @param messageId 消息id
     * @param corpId
     * @return 受影响行数
     */
    int deleteByMessageId(@Param("messageId") Long messageId, @Param("corpId") String corpId);

    List<GroupMessageCountModel> countGroupByMessageId(@Param("messageIdList") List<Long> messageIdList);

    /**
     * 查询消息列表根据消息id列表
     * @param messageIdList 消息id列表
     * @return 群发消息seedMessage列表
     */
    List<WeCustomerSeedMessageVO> selectMessageListByMessageIdList(@Param("messageIdList") List<Long> messageIdList);
}
