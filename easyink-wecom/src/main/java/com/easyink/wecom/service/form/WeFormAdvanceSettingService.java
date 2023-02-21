package com.easyink.wecom.service.form;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.dto.form.FormSettingAddDTO;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;

/**
 * 表单设置表(WeFormAdvanceSetting)表服务接口
 *
 * @author tigger
 * @since 2023-01-09 15:00:47
 */
public interface WeFormAdvanceSettingService extends IService<WeFormAdvanceSetting> {

    /**
     * 保存或更新表单设置
     *
     * @param formSetting 表单设置dto
     * @param corpId      企业id
     */
    void saveOrUpdateFormSetting(WeFormAdvanceSetting formSetting, String corpId);
}

