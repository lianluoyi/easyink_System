package com.easywecom.common.core.domain.system;

import com.easywecom.common.annotation.Excel;
import com.easywecom.common.annotation.Excel.ColumnType;
import com.easywecom.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author admin
 */
@ApiModel(value = "系统访问记录实体")
public class SysLogininfor extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @Excel(name = "序号", cellType = ColumnType.NUMERIC)
    private Long infoId;

    @ApiModelProperty("用户账号")
    @Excel(name = "用户账号")
    private String userName;

    @ApiModelProperty("登录状态 0成功 1失败")
    @Excel(name = "登录状态", readConverterExp = "0=成功,1=失败")
    private String status;

    @ApiModelProperty("登录IP地址")
    @Excel(name = "登录地址")
    private String ipaddr;

    @ApiModelProperty("登录地点")
    @Excel(name = "登录地点")
    private String loginLocation;

    @ApiModelProperty("浏览器类型")
    @Excel(name = "浏览器")
    private String browser;

    @ApiModelProperty("操作系统")
    @Excel(name = "操作系统")
    private String os;

    @ApiModelProperty("提示消息")
    @Excel(name = "提示消息")
    private String msg;

    @ApiModelProperty("访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "访问时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    @ApiModelProperty("登录方式 (1账密登录 2 扫码登录)")
    @Excel(name = "登录方式", readConverterExp = "1=账密登录,2=扫码登录")
    private Integer loginType;

    @ApiModelProperty("企业ID")
    private String corpId;

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }
}
