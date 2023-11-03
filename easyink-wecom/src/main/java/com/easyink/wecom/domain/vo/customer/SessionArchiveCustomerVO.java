package com.easyink.wecom.domain.vo.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 * 会话存档-客户检索VO
 *
 * @author lichaoyu
 * @date 2023/10/20 9:19
 */
@Data
@ApiModel("会话存档-客户检索VO")
public class SessionArchiveCustomerVO {

    @ApiModelProperty("客户头像")
    private String avatar;
    @ApiModelProperty("客户姓名")
    private String name;
    @ApiModelProperty("客户Id")
    private String externalUserid;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 设置客户信息
     *
     * @param avatar 客户头像
     * @param name   客户姓名
     */
    public void setCustomerInfo(String avatar, String name) {
        this.avatar = avatar;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SessionArchiveCustomerVO that = (SessionArchiveCustomerVO) o;
        return externalUserid.equals(that.externalUserid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalUserid);
    }
}
