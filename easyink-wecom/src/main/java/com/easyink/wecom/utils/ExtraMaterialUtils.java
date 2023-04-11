package com.easyink.wecom.utils;

import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.domain.dto.common.AttachmentParam;
import com.easyink.wecom.domain.entity.form.WeForm;
import com.easyink.wecom.domain.entity.form.WeFormMaterial;
import com.easyink.wecom.domain.enums.form.FormChannelEnum;
import com.easyink.wecom.domain.vo.radar.WeRadarVO;
import com.easyink.wecom.mapper.form.WeFormMapper;
import com.easyink.wecom.service.form.WeFormService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 处理其他素材工具类（目前只有表单）
 *
 * @author wx
 * 2023/3/6 14:17
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtraMaterialUtils {

    /**
     * 生成表单url
     *
     * @param formId        表单id
     * @param corpId        企业id
     * @param userId        员工id
     * @param channelType   {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return
     */
    public static String genFormUrl(Long formId, String corpId, String userId, Integer channelType) {
        if (formId == null || StringUtils.isAnyBlank(corpId, userId)
                || FormChannelEnum.UNKNOWN.equals(FormChannelEnum.getByCode(channelType))) {
            return StringUtils.EMPTY;
        }
        WeFormService weFormService = SpringUtils.getBean(WeFormService.class);
        return weFormService.genFormUrl(formId, corpId, userId, channelType);
    }

    /**
     * 生成表内附件
     *
     * @param formId        表单id
     * @param corpId        企业id
     * @param userId        员工id
     * @param channelType   {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return
     */
    public static AttachmentParam getFormAttachment(Long formId, Integer channelType, String corpId, String userId) {
        if (formId == null || StringUtils.isAnyBlank(corpId, userId)
                || FormChannelEnum.UNKNOWN.equals(FormChannelEnum.getByCode(channelType))) {
            return null;
        }
        WeFormService weFormService = SpringUtils.getBean(WeFormService.class);
        return weFormService.getFormAttachment(formId, corpId, userId, channelType);
    }

    /**
     * 获取表单内容
     *
     * @param formId    表单id
     * @return
     */
    public static WeFormMaterial getForm(Long formId){
        if (formId == null) {
            return null;
        }
        return SpringUtils.getBean(WeFormMapper.class).getFormMaterial(formId);
    }




}
