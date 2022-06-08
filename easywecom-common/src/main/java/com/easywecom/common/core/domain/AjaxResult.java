package com.easywecom.common.core.domain;

import com.alibaba.fastjson.JSONObject;
import com.easywecom.common.config.RuoYiConfig;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.utils.spring.SpringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

/**
 * 操作消息提醒
 *
 * @author admin
 */
@ApiModel
@Data
public class AjaxResult<T> implements Serializable {

    private static final long serialVersionUID = 7337293201809451832L;
    /**
     * 系统默认异常提醒
     */
    private static final String DEFAULT_NOTICE = "服务异常";

    @JsonIgnore
    private HashMap<String, Object> map;

    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    @ApiModelProperty("状态码")
    private int code;

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "msg";

    @ApiModelProperty("返回内容")
    private String msg;

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    @ApiModelProperty("数据对象")
    private T data;

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult() {
        this.map = new HashMap<>();
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public AjaxResult(int code, String msg) {
        this();
        this.code = code;
        this.msg = msg;
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, T data) {
        this();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static AjaxResult success() {
        return AjaxResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <T> AjaxResult success(T data) {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static AjaxResult success(String msg) {
        return AjaxResult.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success(String msg, T data) {
        return new AjaxResult(ResultTip.TIP_GENERAL_SUCCESS.getCode(), msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static AjaxResult error() {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult error(String msg) {
        return AjaxResult.error(msg, null);
    }

    /**
     * 返回异常消息
     *
     * @param msg 异常详情
     * @return 如果系统配置可返回异常详情, ruoyi.enableExceptionDetailResp(默认为false)
     * 则返回异常详情,否则返回系统默认异常提示
     */
    public static AjaxResult exception(String msg) {
        RuoYiConfig ruoYiConfig = SpringUtils.getBean(RuoYiConfig.class);
        return ruoYiConfig.isEnableExceptionDetailResp() ? error(msg) : error(DEFAULT_NOTICE);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult error(String msg, Object data) {
        return new AjaxResult(ResultTip.TIP_GENERAL_ERROR.getCode(), msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, msg, null);
    }

    public static AjaxResult error(ResultTip resultTip) {
        return new AjaxResult(resultTip.getCode(), resultTip.getTipMsg(), null);
    }

    public static AjaxResult error(ResultTip resultTip, String msg) {
        return new AjaxResult(resultTip.getCode(), msg, null);
    }

    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    public JSONObject build() {
        JSONObject json = new JSONObject();
        json.put("code", getCode());
        json.put("msg", getMsg());
        json.put("data", getData());
        Optional.ofNullable(getMap()).ifPresent(m -> m.forEach(json::put));
        return json;
    }
}
