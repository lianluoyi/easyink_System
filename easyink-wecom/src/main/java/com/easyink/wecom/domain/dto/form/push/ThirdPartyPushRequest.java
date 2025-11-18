package com.easyink.wecom.domain.dto.form.push;

import com.easyink.wecom.config.ThirdPushFieldMappingConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 第三方推送请求DTO
 *
 * @author easyink
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThirdPartyPushRequest {
    /**
     * 推送内容字段
     */
    private Map<String, Object> content;

    /**
     * 是否快速失败
     */
    @JsonIgnore
    private boolean fastError;

    /**
     * 必填项是否非法
     *
     * @param pushContent        手机号字段名
     * @param fieldMappingConfig 字段映射配置
     * @return 是否存在必填项非法
     */
    public boolean invalidMustParam(Map<String, Object> pushContent, ThirdPushFieldMappingConfig fieldMappingConfig) {
        if (pushContent == null) {
            return true;
        }

        if (pushContent.get(fieldMappingConfig.getFields()) == null) {
            return true;
        }
        if (!(pushContent.get(fieldMappingConfig.getFields()) instanceof Map)) {
            return true;
        }
        Map<String, String> Fields = (Map<String, String>) pushContent.get(fieldMappingConfig.getFields());
        if (MapUtils.isEmpty(Fields)) {
            return true;
        }
        // 昵称或手机号为空则直接失败处理
        return StringUtils.isBlank(Fields.get(fieldMappingConfig.getNickName())) || StringUtils.isBlank(Fields.get(fieldMappingConfig.getPhone()));
    }

}
