package com.easyink.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
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

    /**
     * 数据权限下的员工id列表
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<String> dataScopeUserIds;

    /**
     * 用于判断员工自定义角色的所有权限设置项
     * superadmin: needToCheckDataScope=fasle, 无需过滤权限
     * 其他账号:
     * 1) needToCheckDataScope = true, 需要按照dataSope列表过滤数据
     * 2) needToCheckDataScope = false, 无需过滤权限, 目前只有自定义角色的所有权限设置项需要
     */
    @TableField(exist = false)
    private Boolean needToCheckDataScope = false;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<String> getDataScopeUserIds() {
        return dataScopeUserIds;
    }

    public void setDataScopeUserIds(List<String> dataScopeUserIds) {
        this.dataScopeUserIds = dataScopeUserIds;
    }

    public Boolean getNeedToCheckDataScope() {
        return needToCheckDataScope;
    }

    public void setNeedToCheckDataScope(Boolean needToCheckDataScope) {
        this.needToCheckDataScope = needToCheckDataScope;
    }
}
