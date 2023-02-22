package com.easyink.wecom.domain.dto.form;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量修改分组DTO
 *
 * @author tigger
 * 2023/1/10 17:16
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateGroupDTO {

    /**
     * 分组id
     */
    @NotNull(message = "请选择指定修改的分组")
    private Integer groupId;

    /**
     * 表单id列表
     */
    @NotEmpty(message = "请选择要修改的表单")
    private List<Integer> formIdList;

    /**
     * 校验
     */
    public void valid() {
        if (this.getGroupId() == null) {
            throw new CustomException(ResultTip.TIP_GROUP_FORM_ID_IS_NOT_NULL);
        }

        if (CollectionUtils.isEmpty(this.getFormIdList())) {
            throw new CustomException(ResultTip.TIP_FORM_ID_IS_NOT_NULL);
        }

    }
}
