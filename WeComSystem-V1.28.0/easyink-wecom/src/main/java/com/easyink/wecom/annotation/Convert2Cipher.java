package com.easyink.wecom.annotation;

import com.easyink.common.enums.MethodParamType;
import com.easyink.wecom.domain.dto.customer.EditCustomerDTO;

import java.lang.annotation.*;

/**
 * 将明文转化成密文 userId, externalUserId
 * 传入实体需要联合{@link Cipher} 使用
 * eg: {@link com.easyink.wecom.service.impl.WeCustomerServiceImpl#editCustomer(EditCustomerDTO)}
 *
 * 直接传入需要加密数据直接使用该注解 传入字段名称需要为 userid, externalUserId （不区分大小写）
 * eg: {@link com.easyink.wecom.service.impl.WeCustomerTrajectoryServiceImpl#listOfTrajectory}
 *
 * @author wx
 * 2023/3/13 18:26
 **/
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Convert2Cipher {
    /**
     * 传入方法中的参数
     * 如果是需要加密的数据在实体中则使用MethodParamType.Struct
     * 如果加密的数据在方法参数上则使用Base
     *
     * @return
     */
    MethodParamType paramType() default MethodParamType.BASE;

}
