package com.easyink.wecom.domain.dto.app;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 类名: WeAdminListResp
 *
 * @author: 1*+
 * @date: 2021-09-13 14:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeAdminListResp extends WeResultDTO implements Serializable {


    @SerializedName("admin")
    private List<Admin> admin;


    @Data
    public class Admin implements Serializable {
        private String userid;
        @SerializedName("open_userid")
        private String openUserid;
        @SerializedName("auth_type")
        private Integer authType;
    }


}
