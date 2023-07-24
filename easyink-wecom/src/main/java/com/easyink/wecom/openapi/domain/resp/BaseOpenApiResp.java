package com.easyink.wecom.openapi.domain.resp;

import com.easyink.wecom.openapi.constant.AppInfoConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: 开放api基础响应
 *
 * @author : silver_chariot
 * @date : 2022/3/14 21:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseOpenApiResp<T> {
    /**
     * 状态码成功200,失败0 {@see AppInfoConst.SUCCESS_CODE}
     */
    private int code;
    /**
     * 信息
     */
    private String msg;
    /**
     * 数据
     */
    private T data;

    /**
     * 成功响应
     *
     * @param <T> date type
     * @return success response
     */
    public static <T> BaseOpenApiResp<T> success() {
        return success(null);
    }

    /**
     * 成功响应
     *
     * @param data date
     * @param <T>  date type
     * @return success response
     */
    public static <T> BaseOpenApiResp<T> success(T data) {
        BaseOpenApiResp<T> resp = new BaseOpenApiResp<>();
        resp.setCode(AppInfoConst.SUCCESS_CODE);
        resp.setMsg(AppInfoConst.SUCCESS_MSG);
        resp.setData(data);
        return resp;
    }

    /**
     * 失败响应
     *
     * @param <T> data type
     * @return fail response
     */
    public static <T> BaseOpenApiResp<T> fail() {
        return fail("");
    }

    /**
     * 失败响应
     *
     * @param msg error msg
     * @param <T> data type
     * @return fail response
     */
    public static <T> BaseOpenApiResp<T> fail(String msg) {
        BaseOpenApiResp<T> resp = new BaseOpenApiResp<>();
        resp.setCode(AppInfoConst.FAIL_CODE);
        resp.setMsg(msg);
        return resp;
    }


}
