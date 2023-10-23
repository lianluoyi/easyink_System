package com.easyink.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easyink.common.exception.RetryException;
import com.easyink.wecom.client.retry.EnableRetry;
import com.easyink.wecom.domain.dto.WeDepartMentDTO;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 企业微信部门
 *
 * @author: 1*+
 * @date: 2021-08-18 17:05
 */
@Component
@EnableRetry(retryExceptionClass = RetryException.class)
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeDepartMentClient {


    /**
     * 创建部门
     *
     * @param deartMentDto
     * @return
     */
    @Post(url = "/department/create")
    WeResultDTO createWeDepartMent(@Body WeDepartMentDTO.DeartMentDto deartMentDto, @Header("corpid") String corpId);


    /**
     * 更新部门
     *
     * @param deartMentDto
     * @return
     */
    @Post(url = "/department/update")
    WeResultDTO updateWeDepartMent(@Body WeDepartMentDTO.DeartMentDto deartMentDto, @Header("corpid") String corpId);


    /**
     * 通过部门id删除部门 （注：不能删除根部门；不能删除含有子部门、成员的部门）
     *
     * @param departMnentId
     * @return
     */
    @Get(url = "/department/delete")
    WeResultDTO deleteWeDepartMent(@Query("id") String departMnentId, @Header("corpid") String corpId);


    /**
     * 获取部门列表
     *
     * @return @Query("id") Long id
     */
    @Get(url = "/department/list")
    WeDepartMentDTO weDepartMents(@Query("id") Long id, @Header("corpid") String corpId);


    /**
     * 获取所有部门列表
     *
     * @return
     */
    @Get(url = "/department/list")
    WeDepartMentDTO weAllDepartMents(@Header("corpid") String corpId);


}