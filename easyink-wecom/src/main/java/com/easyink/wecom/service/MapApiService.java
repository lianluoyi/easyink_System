package com.easyink.wecom.service;

import com.alibaba.fastjson.JSONObject;
import com.easyink.wecom.domain.dto.map.DistrictChildrenDTO;
import com.easyink.wecom.domain.dto.map.DistrictListDTO;

/**
 * 地图服务接口
 *
 * @author wx
 * @date 2023/8/8
 */
public interface MapApiService {

    /**
     * 获取省市区列表
     *
     * @param districtListDTO 请求参数
     * @return 省市区列表
     */
    JSONObject getDistrictList(DistrictListDTO districtListDTO);

    /**
     * 获取下级行政区划
     *
     * @param districtChildrenDTO 请求参数
     * @return 下级行政区划列表
     */
    JSONObject getDistrictChildren(DistrictChildrenDTO districtChildrenDTO);

    /**
     * 地址解析（地址转坐标），不指定地址所在城市
     *
     * @param address 地址，标准地址，如：北京市海淀区彩和坊路海淀西大街74号
     * @param corpId  企业ID
     * @return 腾讯地图地址解析API响应
     */
    JSONObject geocoder(String address, String corpId);

    /**
     * 逆地址解析（坐标转地址）
     *
     * @param location 位置坐标，格式：lat,lng
     * @param corpId   企业ID
     * @return 腾讯地图逆地址解析API响应
     */
    JSONObject reGeocode(String location, String corpId);

    /**
     * 关键词输入提示
     *
     * @param keyword   关键词，如：北京大学
     * @param region    限制城市范围，如：北京
     * @param corpId    企业ID
     * @param pageIndex
     * @param pageSize
     * @return 腾讯地图关键词输入提示API响应
     */
    JSONObject suggestion(String keyword, String region, String corpId, String pageIndex, String pageSize);

    /**
     * 获取省市区列表数据
     *  有缓存则取缓存, 缓存24小时
     * @param districtListDTO 请求data数据
     * @return
     */
    JSONObject getDistrictListData(DistrictListDTO districtListDTO);
}