package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeFormSendRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 满意度表单发送记录Mapper接口
 *
 * @author easyink
 * @date 2024-01-01
 */
@Mapper
public interface WeFormSendRecordMapper extends BaseMapper<WeFormSendRecord> {

    /**
     * 根据客户和表单查询记录
     *
     * @param corpId 企业ID
     * @param externalUserid 客户ID
     * @param formId 表单ID
     * @return 记录
     */
    WeFormSendRecord selectByCustomerAndForm(@Param("corpId") String corpId,
                                             @Param("userId") String userId,
                                             @Param("externalUserid") String externalUserid,
                                             @Param("formId") Long formId);

    /**
     * 查询超时未提交的记录
     *
     * @param corpId 企业ID
     * @param timeoutDate 超时时间点
     * @return 超时记录列表
     */
    List<WeFormSendRecord> selectTimeoutRecords(@Param("corpId") String corpId,
                                                @Param("timeoutDate") Date timeoutDate);

    /**
     * 批量更新推送状态
     *
     * @param ids 记录ID列表
     * @param pushStatus 推送状态
     * @return 更新结果
     */
    int batchUpdatePushStatus(@Param("ids") List<Long> ids, @Param("pushStatus") Integer pushStatus);

    /**
     * 批量更新超时推送状态
     *
     * @param ids 记录ID列表
     * @param timeoutPushStatus 超时推送状态
     * @param timeoutPushTime 超时推送时间
     * @return 更新结果
     */
    int batchUpdateTimeoutPushStatus(@Param("ids") List<Long> ids, 
                                    @Param("timeoutPushStatus") Integer timeoutPushStatus,
                                    @Param("timeoutPushTime") Date timeoutPushTime);

    /**
     * 查询超时未提交的记录
     *
     * @param corpId      企业ID
     * @param timeoutHour 超时时间(小时前)
     * @return 超时记录列表
     */
    List<WeFormSendRecord> selectTimeoutUnsubmitted(@Param("corpId") String corpId,
                                                    @Param("timeoutHour") Integer timeoutHour);

    /**
     * 查询需要推送的记录(已提交但未推送)
     *
     * @param corpId 企业ID
     * @return 待推送记录列表
     */
    List<WeFormSendRecord> selectNeedPush(@Param("corpId") String corpId);

    /**
     * 批量更新推送状态
     *
     * @param ids        记录ID列表
     * @param pushStatus 推送状态
     * @param pushTime   推送时间
     * @return 更新结果
     */
    int batchUpdatePushStatus(@Param("ids") List<Long> ids,
                              @Param("pushStatus") Integer pushStatus,
                              @Param("pushTime") Date pushTime);


    String selectStateByFormId(@Param("corpId") String corpId, @Param("userId") String userId, @Param("externalUserid") String externalUserid, @Param("formId") Long formId);
}
