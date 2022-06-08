package com.easywecom.framework.web.exception;

import cn.hutool.json.JSONUtil;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.WeExceptionTip;
import com.easywecom.common.exception.BaseException;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 *
 * @author admin
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public AjaxResult<T> baseException(BaseException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public AjaxResult<T> businessException(CustomException e) {

        ResultTip resultTip = ResultTip.getTip(e.getCode());
        String errTipMsg = e.getMessage();
        //优先使用定义在代码中的msg
        if (org.apache.commons.lang3.StringUtils.isBlank(errTipMsg)) {
            errTipMsg = resultTip.getTipMsg();
        }
        int errCode;
        if (e.getCode() == null) {
            errCode = ResultTip.TIP_GENERAL_BAD_REQUEST.getCode();
        } else {
            errCode = e.getCode();
        }
        //优先使用定义在代码中的code
        if (ObjectUtils.isEmpty(errCode)) {
            errCode = resultTip.getCode();
        }
        return AjaxResult.error(errCode, errTipMsg);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public AjaxResult<T> handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(ResultTip.TIP_GENERAL_NOT_FOUND, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult<T> handleAuthorizationException(AccessDeniedException e) {
        log.error(e.getMessage());
        return AjaxResult.error(ResultTip.TIP_GENERAL_FORBIDDEN);
    }

    @ExceptionHandler(AccountExpiredException.class)
    public AjaxResult<T> handleAccountExpiredException(AccountExpiredException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public AjaxResult<T> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult<T> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.exception(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult<T> validatedBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(message);
    }



    /**
     * 企业微信异常统一处理
     */
    @ExceptionHandler(ForestRuntimeException.class)
    public AjaxResult<T> weComException(ForestRuntimeException forestExcetion) {

        String errorMsg = forestExcetion.getMessage();


        if (StringUtils.isNotEmpty(errorMsg)) {

            Integer errCode = JSONUtil.parseObj(errorMsg).getInt(WeConstans.WE_ERROR_FIELD);

            return AjaxResult.error(errCode, WeExceptionTip.getTipMsg(errCode));
        }


        return AjaxResult.error("企业微信端未知异常,请联系管理员");
    }


}
