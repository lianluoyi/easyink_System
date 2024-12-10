package com.easyink.wecom.openapi.res;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author tigger
 * 2024/11/19 16:41
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DecryptUserIdSelfBuildRes extends BaseSelfBuildRes<DecryptUserIdSelfBuildRes.GetUserIdResData> {


    /**
     * 解密员工id res
     * @author tigger
     * 2024/11/25 13:51
     **/
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetUserIdResData {

        private List<OpenUserIdAndUserId> userIdList;

        private List<String> invalidOpenUseridList;


        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OpenUserIdAndUserId {

            /**
             * 员工id服务商密文
             */
            private String openUserId;
            /**
             * 员工id明文
             */
            private String userId;
        }
    }


}
