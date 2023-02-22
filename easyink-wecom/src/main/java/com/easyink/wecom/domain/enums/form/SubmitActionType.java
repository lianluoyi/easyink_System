package com.easyink.wecom.domain.enums.form;

import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.dto.form.ActionInfoParamDTO;
import com.easyink.wecom.domain.model.form.JumpLinkActionParam;
import com.easyink.wecom.domain.model.form.JumpResultPageActionParam;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * 提交结果行为类型
 *
 * @author tigger
 * 2023/1/10 10:26
 **/
@Slf4j
public enum SubmitActionType {
    /**
     * 1:不跳转
     */
    NOT_ACTION(1, "不跳转") {
        @Override
        public void validParam(ActionInfoParamDTO param) {
        }
    },
    /**
     * 2:跳转结果页面
     */
    JUMP_RESULT_PAGE(2, "跳转结果页面") {
        @Override
        public void validParam(ActionInfoParamDTO param) {
            param = ActionInfoParamDTO.builder()
                    .iconUrl(param.getIconUrl())
                    .document(param.getDocument())
                    .build();
            JumpResultPageActionParam jumpResultPageActionParam = new JumpResultPageActionParam(param);
            jumpResultPageActionParam.valid();
        }
    },
    /**
     * 3:跳转连接
     */
    JUMP_LINK(3, "跳转连接") {
        @Override
        public void validParam(ActionInfoParamDTO param) {
            param = ActionInfoParamDTO.builder()
                    .url(param.getUrl())
                    .build();
            JumpLinkActionParam jumpLinkActionParam = new JumpLinkActionParam(param);
            jumpLinkActionParam.valid();
        }
    },
    ;
    @Getter
    private final Integer code;
    @Getter
    private final String dict;

    SubmitActionType(Integer code, String dict) {
        this.code = code;
        this.dict = dict;
    }

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<SubmitActionType> getByCode(Integer code) {
        for (SubmitActionType value : values()) {
            if (value.code.equals(code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * 校验or 返回
     *
     * @param code code
     * @return 枚举
     */
    public static SubmitActionType validCode(Integer code) {
        Optional<SubmitActionType> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new CustomException("提交结果行为类型异常"));
    }

    public abstract void validParam(ActionInfoParamDTO paramDTO);
}
