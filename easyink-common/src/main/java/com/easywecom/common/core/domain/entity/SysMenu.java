package com.easywecom.common.core.domain.entity;

import com.easywecom.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表 sys_menu
 *
 * @author admin
 */
@ApiModel("菜单权限表")
public class SysMenu extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("菜单ID")
    private Long menuId;

    @ApiModelProperty("菜单名称")
    private String menuName;

    @ApiModelProperty("父菜单名称")
    private String parentName;

    @ApiModelProperty("父菜单ID")
    private Long parentId;

    @ApiModelProperty("显示顺序")
    private String orderNum;


    @ApiModelProperty("路由地址")
    private String path;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("是否为外链（0是 1否）")
    private String isFrame;

    @ApiModelProperty("类型（M目录 C菜单 F按钮）")
    private String menuType;

    @ApiModelProperty("显示状态（0显示 1隐藏）")
    private String visible;

    @ApiModelProperty("菜单状态（0显示 1隐藏）")
    private String status;

    @ApiModelProperty("权限字符串")
    private String perms;

    @ApiModelProperty("菜单图标")
    private String icon;

    @ApiModelProperty("子菜单")
    private List<SysMenu> children = new ArrayList<>();

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    @ApiModelProperty("菜单名称")
    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @NotBlank(message = "显示顺序不能为空")
    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    @Size(max = 200, message = "路由地址不能超过200个字符")
    @ApiModelProperty("路由地址")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Size(max = 200, message = "组件路径不能超过255个字符")
    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getIsFrame() {
        return isFrame;
    }

    public void setIsFrame(String isFrame) {
        this.isFrame = isFrame;
    }

    @NotBlank(message = "菜单类型不能为空")
    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<SysMenu> getChildren() {
        return children;
    }

    public void setChildren(List<SysMenu> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("menuId", getMenuId())
                .append("menuName", getMenuName())
                .append("parentId", getParentId())
                .append("orderNum", getOrderNum())
                .append("path", getPath())
                .append("component", getComponent())
                .append("isFrame", getIsFrame())
                .append("menuType", getMenuType())
                .append("visible", getVisible())
                .append("status ", getStatus())
                .append("perms", getPerms())
                .append("icon", getIcon())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
