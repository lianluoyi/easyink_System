package com.easyink.wecom.openapi.res;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author tigger
 * 2024/11/19 16:41
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseSelfBuildRes<T> {
    /**
     * 请求selfbuild成功code
     */
    private static final int SUCCESS_CODE = 200;
    /**
     * 请求响应code
     */
    private Integer code;

    /**
     * 响应msg
     */
    private String msg;

    /**
     * 响应data
     */
    private T data;

    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }
}
