package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeCustomerMessgaeResult;
import com.easywecom.wecom.domain.dto.WeCustomerMessagePushResultDTO;
import com.easywecom.wecom.domain.dto.message.AsyncResultDTO;
import com.easywecom.wecom.domain.vo.WeCustomerMessageResultVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 群发消息  微信消息发送结果表 Mapper接口
 *
 * @author admin
 * @date 2020-12-08
 */
@Repository
public interface WeCustomerMessgaeResultMapper extends BaseMapper<WeCustomerMessgaeResult> {


    /**
     * 更新群发消息结果
     *
     * @param messageId      微信消息表id
     * @param chatId         外部客户群id
     * @param externalUserid 外部联系人userid
     * @param status         发送状态 0-未发送 1-已发送 2-因客户不是好友导致发送失败 3-因客户已经收到其他群发消息导致发送失败
     * @param sendTime       发送时间，发送状态为1时返回(为时间戳的形式)
     * @param userId         员工id
     * @return int
     */
    int updateWeCustomerMessgaeResult(@Param("messageId") Long messageId, @Param("chatId") String chatId, @Param("externalUserid") String externalUserid
            , @Param("status") String status, @Param("sendTime") String sendTime, @Param("userId") String userId, @Param("remark") String remark);

    /**
     * 查询微信消息发送情况(未执行)
     *
     * @param weCustomerMessagePushResultDTO 查询条件
     * @return {@link WeCustomerMessageResultVO}s
     */
    List<WeCustomerMessageResultVO> customerMessagePushs(WeCustomerMessagePushResultDTO weCustomerMessagePushResultDTO);

    /**
     * 查询微信消息发送情况(已执行)
     *
     * @param weCustomerMessagePushResultDTO 查询条件
     * @return {@link WeCustomerMessageResultVO}s
     */
    List<WeCustomerMessageResultVO> listOfMessageResult(WeCustomerMessagePushResultDTO weCustomerMessagePushResultDTO);

    /**
     * 查询30天内未发送的消息
     *
     * @param corpId 企业id
     * @return {@link List<AsyncResultDTO>}
     */
    List<AsyncResultDTO> listOfNotSend(@Param("corpId") String corpId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    /**
     * 检查是否已经同步群发结果
     *
     * @param messageId 微信消息表id
     * @return 已发送消息数
     */
    int checkSendStatus(@Param("messageId") Long messageId);

    /**
     * 批量保存映射关系
     *
     * @param weCustomerMessgaeResults 映射关系列表信息
     * @return int 结果
     */
    int batchInsert(@Param("customers") List<WeCustomerMessgaeResult> weCustomerMessgaeResults);

    /**
     * 删除
     *
     * @param messageId 消息id
     * @param corpId
     * @return 受影响行数
     */
    int deleteByMessageId(@Param("messageId") Long messageId, @Param("corpId") String corpId);

}
