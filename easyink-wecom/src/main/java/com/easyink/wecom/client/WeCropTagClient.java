package com.easyink.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Post;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.domain.dto.tag.*;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 企业微信标签相关
 *
 * @author: 1*+
 * @date: 2021-08-18 17:03
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeCropTagClient {


    /**
     * 保存标签
     *
     * @param weCropGroupTag dataType = "json"
     * @return
     */
    @Post(url = "/externalcontact/add_corp_tag")
    WeCropGropTagDtlDTO addCorpTag(@Body WeCropGroupTagDTO weCropGroupTag, @Header("corpid") String corpId);


    /**
     * 获取所有标签 WeCropGroupTagDTO
     *
     * @return
     */
    @Post(url = "/externalcontact/get_corp_tag_list")
    WeCropGroupTagListDTO getAllCorpTagList(@Header("corpid") String corpId);


    /**
     * 根据指定标签的id,获取标签详情
     *
     * @return
     */
    @Post(url = "/externalcontact/get_corp_tag_list")
    WeCropGroupTagListDTO getCorpTagListByTagIds(@Body WeFindCropTagParam weFindCropTagParam, @Header("corpid") String corpId);


    /**
     * 删除企业客户标签
     *
     * @param weCropDelDto
     * @return
     */
    @Post(url = "/externalcontact/del_corp_tag")
    WeResultDTO delCorpTag(@Body WeCropDelDTO weCropDelDto, @Header("corpid") String corpId);


    /**
     * 编辑企业微信标签
     *
     * @param weCropTagDTO
     * @return
     */
    @Post(url = "/externalcontact/edit_corp_tag")
    WeResultDTO editCorpTag(@Body WeCropTagDTO weCropTagDTO, @Header("corpid") String corpId);
}