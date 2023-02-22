package com.easyink.wecom.domain.vo.form;

import com.easyink.wecom.domain.dto.form.ActionInfoParamDTO;
import com.easyink.wecom.domain.model.form.WeFormModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单内容VO(中间页显示用)
 *
 * @author wx
 * 2023/1/13 14:35
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormContentVO {

    /**
     * 表单操作记录id
     */
    private Long recordId;

    /**
     * 表单内容
     */
    private WeFormModel weFormModel;

    /**
     * 是否已经限制提交
     */
    private Boolean limitSubmitFlag;

    /**
     * 若表单已截止提交，或者当前表单未启用，或已被删除
     */
    private Boolean closeSubmitFlag;

    /**
     * 提交结果行为类型(1:不跳转 2:跳转结果页面 3:跳转连接)
     */
    private Integer submitActionType;

    /**
     * 提交结果行为详情参数(当 submitActionType不为1的时候有用)
     */
    private ActionInfoParamDTO actionInfoParam;

}
