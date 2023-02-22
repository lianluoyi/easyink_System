package com.easyink.wecom.domain.vo.form;

import com.easyink.wecom.domain.enums.form.PromotionalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 推广VO
 *
 * @author tigger
 * 2023/1/11 9:51
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionalVO {

    /**
     * 推广方式列表
     */
    private List<PromotionalType.BasePromotional> wayList;

}
