package com.easyink.web.controller.openapi;

import com.alibaba.fastjson.JSONObject;
import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.dto.map.DistrictChildrenDTO;
import com.easyink.wecom.domain.dto.map.DistrictListDTO;
import com.easyink.wecom.domain.vo.WeMapConfigVO;
import com.easyink.wecom.service.MapApiService;
import com.easyink.wecom.service.WeMapConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地图API控制器
 *
 * @author wx
 * @date 2023/8/8
 */
@Api(tags = "地图API")
@RestController
@RequestMapping("/wecom/map/req")
@Slf4j
@AllArgsConstructor
public class MapApiController extends BaseController {

    private final MapApiService mapApiService;
    private final WeMapConfigService weMapConfigService;

    /**
     * 获取地图API配置
     */
    @ApiOperation("获取地图API配置")
    @GetMapping("/getConfig")
    public AjaxResult<WeMapConfigVO> getConfig(
            @ApiParam(value = "企业id") @RequestParam String corpId,
            @ApiParam(value = "地图类型：1-腾讯地图, 2-高德地图, 3-百度地图", required = false, defaultValue = "1") @RequestParam(required = false, defaultValue = "1") Integer mapType
    ) {

        WeMapConfigVO configVO = weMapConfigService.getConfigVO(corpId, mapType);
        return AjaxResult.success(configVO);
    }

    /**
     * 获取省市区列表
     *
     * @param districtListDTO 请求参数
     * @return 省市区列表
     */
    @ApiOperation("获取省市区列表")
    @GetMapping("/district/list")
    public AjaxResult<JSONObject> getDistrictList(@Validated DistrictListDTO districtListDTO) {
        districtListDTO.setStruct_type(1);
        return AjaxResult.success(mapApiService.getDistrictList(districtListDTO));
    }

    /**
     * 获取下级行政区划
     *
     * @param districtChildrenDTO 请求参数
     * @return 下级行政区划列表
     */
    @ApiOperation("获取下级行政区划")
    @GetMapping("/district/children")
    public AjaxResult<JSONObject> getDistrictChildren(@Validated DistrictChildrenDTO districtChildrenDTO) {
        return AjaxResult.success(mapApiService.getDistrictChildren(districtChildrenDTO));
    }

    /**
     * 地址解析（地址转坐标）
     */
    @ApiOperation("地址解析（地址转坐标）")
    @GetMapping("/geocoder")
    @Log(title = "地址解析", businessType = BusinessType.OTHER)
    public AjaxResult geocoder(
            @ApiParam(value = "企业id", required = true) @RequestParam String corpId,
            @ApiParam(value = "地址", required = true) @RequestParam String address,
            @ApiParam(value = "地址所在城市") @RequestParam(required = false) String region) {
        return AjaxResult.success(mapApiService.geocoder(address, corpId));
    }

    /**
     * 逆地址解析（坐标转地址）
     */
    @ApiOperation("逆地址解析（坐标转地址）")
    @GetMapping("/regeocode")
    @Log(title = "逆地址解析", businessType = BusinessType.OTHER)
    public AjaxResult reGeocode(
            @ApiParam(value = "企业id", required = true) @RequestParam String corpId,
            @ApiParam(value = "位置坐标，格式：lat,lng", required = true) @RequestParam String location) {

        JSONObject response = mapApiService.reGeocode(location, corpId);
        return AjaxResult.success(response);
    }

    /**
     * 关键词输入提示
     */
    @ApiOperation("关键词输入提示")
    @GetMapping("/suggestion")
    @Log(title = "关键词输入提示", businessType = BusinessType.OTHER)
    public AjaxResult suggestion(
            @ApiParam(value = "企业id", required = true) @RequestParam String corpId,
            @ApiParam(value = "关键词", required = true) @RequestParam String keyword,
            @ApiParam(value = "坐标范围限制") @RequestParam(required = false) String location,
            @ApiParam(value = "页码，从1开始，最大页码需通过count进行计算，必须与page_size同时使用") @RequestParam(name = "pageIndex") String pageIndex,
            @ApiParam(value = "每页条数，取值范围1-20，必须与page_index 同时使用") @RequestParam(name = "pageSize") String pageSize
    ) {
        return AjaxResult.success(mapApiService.suggestion(keyword, location, corpId, pageIndex, pageSize));
    }
} 