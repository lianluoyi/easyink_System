package com.easyink.wecom.client;

import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;
import com.easyink.wecom.domain.dto.map.DistrictChildrenDTO;
import com.easyink.wecom.domain.dto.map.DistrictListDTO;
import com.easyink.wecom.interceptor.MapKeyInterceptor;
import org.springframework.stereotype.Component;

/**
 * 地图API接口
 *
 * @author wx
 * @date 2023/8/1
 */
@Component
@BaseRequest(interceptor = {MapKeyInterceptor.class})
public interface MapClient {

    /**
     * 获取省市区列表
     * 
     * @param districtListDTO 请求参数
     * @return 省市区列表
     */
    @Get("https://apis.map.qq.com/ws/district/v1/list")
    JSONObject getDistrictList(@Query DistrictListDTO districtListDTO);

    /**
     * 获取下级行政区划
     * 
     * @param districtChildrenDTO 请求参数
     * @return 下级行政区划列表
     */
    @Get("https://apis.map.qq.com/ws/district/v1/getchildren")
    JSONObject getDistrictChildren(@Query DistrictChildrenDTO districtChildrenDTO);

    /**
     * 地址解析（地址转坐标）
     * API文档：https://lbs.qq.com/service/webService/webServiceGuide/webServiceGeocoder
     *
     * @param address   地址，标准地址，如：北京市海淀区彩和坊路海淀西大街74号
     * @param corpId    企业ID
     * @return          腾讯地图地址解析API响应
     */
    @Get("https://apis.map.qq.com/ws/geocoder/v1/?address=${address}")
    JSONObject geocoder(@Query("address") String address, @Query("corpId") String corpId);

    /**
     * 逆地址解析（坐标转地址）
     * API文档：https://lbs.qq.com/service/webService/webServiceGuide/webServiceGcoder
     *
     * @param location  位置坐标，格式：lat,lng
     * @param corpId    企业ID
     * @return          腾讯地图逆地址解析API响应
     */
    @Get("https://apis.map.qq.com/ws/geocoder/v1/?location=${location}")
    JSONObject reGeocode(@Query("location") String location, @Query("corpId") String corpId);
    
    /**
     * 关键词输入提示
     * API文档：https://lbs.qq.com/service/webService/webServiceGuide/webServiceSuggestion
     *
     * @param keyword   关键词，如：北京大学
     * @param location  坐标
     * @param corpId    企业ID
     * @param pageIndex 分页页码，从1开始，最大页码需通过count进行计算，必须与page_size同时使用
     * @param pageSize  每页条数，取值范围1-20，必须与page_index 同时使用
     * @return 腾讯地图关键词输入提示API响应
     */
    @Get("https://apis.map.qq.com/ws/place/v1/suggestion/?keyword=${keyword}&location=${location}&page_index=${pageIndex}&page_size=${pageSize}")
    JSONObject suggestion(@Query("keyword") String keyword, @Query("location") String location, @Query("corpId") String corpId, @Query("pageIndex") String pageIndex, @Query("pageSize") String pageSize);
    
}