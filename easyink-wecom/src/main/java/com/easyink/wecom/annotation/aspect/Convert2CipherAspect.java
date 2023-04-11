package com.easyink.wecom.annotation.aspect;

import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.enums.MethodParamType;
import com.easyink.common.exception.user.NoLoginTokenException;
import com.easyink.wecom.annotation.Cipher;
import com.easyink.wecom.annotation.Convert2Cipher;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.WeUserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 转化为密文切片
 *
 * @author wx
 * 2023/3/13 18:30
 **/
@Aspect
@Component
@RequiredArgsConstructor
public class Convert2CipherAspect {

    /**
     * 员工userId参数名
     */
    private final String USER_ID_PARAM_NAME = "userId";

    /**
     * 外部联系人externalUserId参数名
     */
    private final String EXTERNAL_USER_ID_PARAM_NAME = "externalUserId";

    /**
     * 索引第一个
     */
    private final int FIRST_INDEX = 0;

    private final RuoYiConfig ruoYiConfig;

    private final WeUserService weUserService;

    private final WeCustomerService weCustomerService;


    @Pointcut("@annotation(com.easyink.wecom.annotation.Convert2Cipher)")
    public void convert2CipherPointCut() {
        // 配置织入点

    }

    @Around(value = "convert2CipherPointCut()")
    public Object convert(ProceedingJoinPoint pc) throws Throwable {
        // 如果是内部应用则不需要处理
        if (ruoYiConfig.isInternalServer()) {
            return pc.proceed();
        }
        // 获取当前的用户 (若非页面的内部请求则会抛出无登录token异常,不进行数据范围过滤处理)
        LoginUser loginUser;
        try {
            loginUser = LoginTokenService.getLoginUser();
        } catch (NoLoginTokenException e) {
            return pc.proceed();
        }
        // 如果不是第三方系统调用则不需处理
        if (Boolean.FALSE.equals(loginUser.getIsOtherSysUse())) {
            return pc.proceed();
        }
        Convert2Cipher annotation = getAnnotation(pc);
        if (annotation == null) {
            return pc.proceed();
        }
        // 处理实体类
        if (handleStructData(pc, annotation, loginUser.getCorpId())){
            return pc.proceed();
        }

        MethodSignature signature = (MethodSignature) pc.getSignature();
        //获取切入方法的对象
        Method method = signature.getMethod();
        //获取参数值
        Object[] args = pc.getArgs();
        //获取参数名
        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        if (parameterNames == null) {
            return pc.proceed();
        }
        String corpId = loginUser.getCorpId();
        //对这些参数名进行遍历，找到我们要修改的参数，获取到这个参数名在数组中的下标，并将我们需要赋的值放到参数值数据的相应位置，进行参数替换。
        for (int i = 0; i < parameterNames.length; i++) {
            if (null == args[i]){
                continue;
            }
            if (USER_ID_PARAM_NAME.equalsIgnoreCase(parameterNames[i]) && ! args[i].toString().startsWith(WeConstans.USER_ID_PREFIX)) {
                args[i] = getUserId(corpId, args[i].toString());
            }
            // WeConstans.CHAT_ID_PREFIX 侧边栏群聊代办事项 传的是chatId 不需要加密
            if (EXTERNAL_USER_ID_PARAM_NAME.equalsIgnoreCase(parameterNames[i]) && ! args[i].toString().startsWith(WeConstans.EXTERNAL_USER_ID_PREFIX) && ! args[i].toString().startsWith(WeConstans.CHAT_ID_PREFIX)) {
                args[i] = getExUserId(corpId, args[i].toString());
            }
        }
        //执行程序，带上修改过的参数
        return pc.proceed(args);

    }

    /**
     * 通过明文获取密文userId
     *
     * @param corpId    企业id
     * @param userId    明文员工userId
     * @return
     */
    protected String getUserId(String corpId, String userId) {
        return weUserService.getOpenUserId(corpId, userId);
    }


    /**
     * 通过明文获取密文externalUserId
     *
     * @param corpId            企业id
     * @param externalUserId    明文外部联系人externalUserId
     * @return
     */
    protected String getExUserId(String corpId, String externalUserId){
        return weCustomerService.getOpenExUserId(corpId, externalUserId);
    }

    /**
     * 处理实体内的需要加密的数据
     *
     * @param joinPoint             切点
     * @param convert2Cipher        {@link Convert2Cipher}
     * @param corpId                企业id
     * @return
     */
    public boolean handleStructData(ProceedingJoinPoint joinPoint, Convert2Cipher convert2Cipher, String corpId) throws IllegalAccessException {
        if (MethodParamType.BASE.equals(convert2Cipher.paramType())) {
            return false;
        }
        Object dto = joinPoint.getArgs()[FIRST_INDEX];
        for (Field declaredField : dto.getClass().getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Cipher.class)){
                Cipher cipher = declaredField.getAnnotation(Cipher.class);
                declaredField.setAccessible(true);
                if (cipher.isUserId()) {
                    declaredField.set(dto, getUserId(corpId, declaredField.get(dto).toString()));
                }
                if (cipher.isExUserId()) {
                    declaredField.set(dto, getExUserId(corpId, declaredField.get(dto).toString()));
                }
            }
        }
        return true;
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private Convert2Cipher getAnnotation(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(Convert2Cipher.class);
        }
        return null;
    }

}
