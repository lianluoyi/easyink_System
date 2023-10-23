package com.easyink.common.core.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.common.constant.Constants;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Supplier;

/**
 * Entity基类
 *
 * @author admin
 */
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity extends RootEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 搜索值
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String searchValue;

    /**
     * 创建者
     */
    @ApiModelProperty(hidden = true)
    @TableField(whereStrategy = FieldStrategy.NOT_EMPTY)
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(hidden = true)
    @TableField(whereStrategy = FieldStrategy.NOT_EMPTY)
    private Date createTime = new Date();

    /**
     * 更新者
     */
    @ApiModelProperty(hidden = true)
    @TableField(whereStrategy = FieldStrategy.NOT_EMPTY)
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(hidden = true)
    @TableField(whereStrategy = FieldStrategy.NOT_EMPTY)
    private Date updateTime = new Date();

    /**
     * 备注
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String remark;

    /**
     * 开始时间
     */
    @JsonIgnore
    @TableField(exist = false)
    private String beginTime;

    /**
     * 结束时间
     */
    @JsonIgnore
    @TableField(exist = false)
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "做游标分页时使用,查找userId > lastId的数据")
    private String lastId;

    /**
     * 是否是管理员 ,如果是管理员则部分查询可以简化
     */
    @JsonIgnore
    @TableField(exist = false)
    private Boolean isAdmin;


    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * 根据登录用户设置创建人,如果是超级管理员则为admin,其他用户则为userId
     *
     * @param loginUser 登录用户
     */
    public void setCreateBy(LoginUser loginUser) {
        if (loginUser.isSuperAdmin()) {
            this.createBy = Constants.SUPER_ADMIN;
        } else if (loginUser.getWeUser() != null && StringUtils.isNotBlank(loginUser.getWeUser().getUserId())) {
            this.createBy = loginUser.getWeUser().getUserId();
        }
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        if (createTime == null) {
            this.createTime = DateUtils.getNowDate();
        } else {
            this.createTime = createTime;
        }

    }

    public void setNullCreateTime() {
        this.createTime = null;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
    /**
     * 根据登录用户设置更新人,如果是超级管理员则为admin,其他用户则为userId
     *
     * @param loginUser 登录用户
     */
    public void setUpdateBy(LoginUser loginUser) {
        if (loginUser.isSuperAdmin()) {
            this.updateBy = Constants.SUPER_ADMIN;
        } else if (loginUser.getWeUser() != null && StringUtils.isNotBlank(loginUser.getWeUser().getUserId())) {
            this.updateBy = loginUser.getWeUser().getUserId();
        }
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBeginTime() {
        return beginTime;
    }

    /**
     * 根据日期的格式 补全其开始时间传参，补全后格式：yyyy-MM-dd 00:00:00
     *
     * @param beginTime 开始时间字符串
     */
    public void setBeginTime(String beginTime) {
        this.beginTime = DateUtils.parseBeginDay(beginTime);
    }

    public String getEndTime() {
        return endTime;
    }

    /**
     * 根据日期的格式 补全其开始时间传参，补全后格式：yyyy-MM-dd 23:59:59
     *
     * @param endTime 结束时间字符串
     */
    public void setEndTime(String endTime) {
        this.endTime = DateUtils.parseEndDay(endTime);
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
