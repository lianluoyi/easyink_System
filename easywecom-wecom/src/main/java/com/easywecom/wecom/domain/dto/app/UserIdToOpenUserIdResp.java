package com.easywecom.wecom.domain.dto.app;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 类名: UserIdToOpenUserIdResp
 *
 * @author: 1*+
 * @date: 2021-11-29 18:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIdToOpenUserIdResp extends WeResultDTO implements Serializable {


    @SerializedName("invalid_userid_list")
    private List<String> invalidUserIdList;

    @SerializedName("open_userid_list")
    private List<OpenUserId> openUserIdList;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class OpenUserId implements Serializable {

        @SerializedName("userid")
        private String userId;
        @SerializedName("open_userid")
        private String openUserId;

    }

}
