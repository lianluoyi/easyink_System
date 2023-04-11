package com.easyink.wecom.domain.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 删除表单DTO
 *
 * @author tigger
 * 2023/1/11 17:32
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFormDTO {

    /**
     * 删除的表单id列表
     */
    @NotEmpty(message = "请选择要删除的表单id")
    private List<Long> idList;
}
