package com.easyink.wecom.service.form;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.form.WeFormUseRecord;
import com.easyink.wecom.domain.enums.form.FormChannelEnum;
import com.easyink.wecom.domain.vo.form.FormSimpleInfoVO;

import java.util.List;

/**
 * 表单使用记录表(WeFormUseRecord)表服务接口
 *
 * @author tigger
 * @since 2023-01-13 10:12:32
 */
public interface WeFormUseRecordService extends IService<WeFormUseRecord> {

    /**
     * 表单使用记录分页
     *
     * @param sourceType 表单所属分类
     * @param corpId     企业id
     * @return 表单简单分页VO列表
     */
    List<FormSimpleInfoVO> pageRecord(Integer sourceType, String corpId);

    /**
     * 保存员工使用表单记录
     *
     * @param formId         表单id
     * @param userId         员工id
     * @param externalUserId 客户id
     * @param corpId         企业id
     */
    void saveRecord(Long formId, String userId, String externalUserId, String corpId);

    /**
     * 保存员工使用表单记录
     *
     * @param formId         表单id
     * @param userId         员工id
     * @param externalUserId 客户id
     */
    void saveRecord(Long formId, String userId, String externalUserId);
}

