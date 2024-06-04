package com.easyink.framework.web.exception;

import cn.hutool.json.JSONUtil;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeExceptionTip;
import com.easyink.common.exception.BaseException;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.file.NoFileException;
import com.easyink.common.exception.openapi.OpenApiException;
import com.easyink.common.utils.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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


    @ExceptionHandler(OpenApiException.class)
    public AjaxResult<T> businessException(OpenApiException e, HttpServletResponse response) {
        String errTipMsg = e.getMessage();
        Integer errCode = ResultTip.TIP_GENERAL_BAD_REQUEST.getCode();
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
        log.error("全局捕获异常:{}", ExceptionUtils.getStackTrace(e));
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
    @ExceptionHandler(NoFileException.class)
    public void noFile (NoFileException e , HttpServletResponse response) {
        try {
            response.sendError(500);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
//        return AjaxResult.error("未获取到文件资源，请重新导出");
    }


}
