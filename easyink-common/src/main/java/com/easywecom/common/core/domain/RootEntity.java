package com.easywecom.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * 类名： RootEntity
 *
 * @author 佚名
 * @date 2021/8/27 16:50
 */
public class RootEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求参数
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
