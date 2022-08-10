package com.tencent.wework;

import lombok.Getter;

/**
 * Class Description
 *
 * @author :1*+
 * Date: 2021-07-29
 */
public enum RSAKeyEnum {

    //.
    VERSION_0(0, "", ""),
    VERSION_1(1, "",
            "");


    private Integer publicKeyVer;
    @Getter
    private String privateKey;
    private String publicKey;

    RSAKeyEnum(Integer publicKeyVer, String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.publicKeyVer = publicKeyVer;
    }

}
