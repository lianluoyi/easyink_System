package com.easyink.wecom.domain.model.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 手机号和地址权限model
 *
 * @author tigger
 * 2025/8/5 18:04
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneAndAddressPermissionModel {
    private boolean hasPhonePermission;
    private boolean hasAddressPermission;

    public PhoneAndAddressPermissionModel(Set<String> menuSet, boolean superAdmin) {
        if(superAdmin){
            this.hasPhonePermission = true;
            this.hasAddressPermission = true;
        }else{
            this.hasPhonePermission = menuSet.contains("securityConfig:view:phone");
            this.hasAddressPermission = menuSet.contains("securityConfig:view:address");
        }
    }
}
