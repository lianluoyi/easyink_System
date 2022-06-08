package com.easywecom.wecom.domain.vo;

import com.easywecom.wecom.domain.WeTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @description: 客户打标签实体
 * @author admin
 * @create: 2020-10-24 20:09
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  WeMakeCustomerTagVO {
    private String externalUserid;
    @NotBlank(message = "userId不能为空")
    private String userId;
    private List<WeTag> addTag;

    /**
     * 公司id
     */
    private String corpId;

    private String updateBy;

    public WeMakeCustomerTagVO(String externalUserid, String userId, List<WeTag> addTag, String corpId) {
        this.externalUserid = externalUserid;
        this.userId = userId;
        this.addTag = addTag;
        this.corpId = corpId;
    }
}
