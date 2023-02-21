package com.easyink.wecom.domain.dto.form;

import com.easyink.common.domain.ChangeSortModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 修改表单分组排序DTO
 *
 * @author tigger
 * 2023/1/10 15:46
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeFormGroupSortDTO {

    /**
     * 排序列表
     */
    private List<ChangeSortModel> sortList;
}
