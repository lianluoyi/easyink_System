package com.easyink.wecom.mapper.form;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.form.WeFormOperRecord;
import com.easyink.wecom.domain.vo.form.FormCustomerOperRecordExportVO;
import com.easyink.wecom.domain.vo.form.FormCustomerOperRecordVO;
import com.easyink.wecom.domain.vo.form.FormOperRecordDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.easyink.wecom.domain.vo.form.FormUserSendRecordVO;

import java.util.Date;
import java.util.List;

/**
 * 智能表单操作记录表(WeFormOperRecord)表数据库访问层
 *
 * @author wx
 * @since 2023-01-13 11:49:45
 */
@Mapper
public interface WeFormOperRecordMapper extends BaseMapper<WeFormOperRecord> {

    /**
     * 获取客户操作记录
     *
     * @param formId        表单id
     * @param clickTimeFlag 是否为点击时间，true是点击时间 false为提交时间
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param customerName  客户名称
     * @param channelType   渠道类型 {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return {@link FormCustomerOperRecordVO}
     */
    List<FormCustomerOperRecordVO> getCustomerOperRecord(@Param("formId") Long formId,
                                                   @Param("clickTimeFlag") Boolean clickTimeFlag,
                                                   @Param("beginTime") Date beginTime,
                                                   @Param("endTime") Date endTime,
                                                   @Param("customerName") String customerName,
                                                   @Param("channelType") Integer channelType);

    /**
     * 获取员工发送记录
     *
     * @param formId        表单id
     * @param corpId        企业id
     * @param clickTimeFlag 是否为点击时间，true是点击时间 false为提交时间
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param userName      员工名称
     * @return  {@link FormUserSendRecordVO}
     */
    List<FormUserSendRecordVO> getUserSendRecord(@Param("formId") Long formId,
                                                 @Param("corpId") String corpId,
                                                 @Param("clickTimeFlag") Boolean clickTimeFlag,
                                                 @Param("beginTime") Date beginTime,
                                                 @Param("endTime") Date endTime,
                                                 @Param("userName") String userName);

    /**
     * 获取客户提交表单详情
     *
     * @param formId 表单id
     * @param corpId 企业id
     * @return
     */
    List<FormOperRecordDetailVO> getFormResult(@Param("formId") Long formId, @Param("corpId") String corpId);



    /**
     * 导出客户操作记录
     *
     * @param formId         表单id
     * @param corpId         企业id
     * @param channelType   渠道类型 {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @param clickTimeFlag 是否为点击时间，true是点击时间 false为提交时间
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param customerName  客户名称
     * @return
     */
    List<FormCustomerOperRecordExportVO> exportCustomerOperRecord(@Param("formId") Long formId,
                                                                  @Param("corpId") String corpId,
                                                                  @Param("channelType")Integer channelType,
                                                                  @Param("clickTimeFlag") Boolean clickTimeFlag,
                                                                  @Param("beginTime") Date beginTime,
                                                                  @Param("endTime") Date endTime,
                                                                  @Param("customerName") String customerName);
}

