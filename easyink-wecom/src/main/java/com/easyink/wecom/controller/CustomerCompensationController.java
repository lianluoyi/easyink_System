package com.easyink.wecom.controller;

import cn.hutool.core.date.DateUtil;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.service.CustomerCompensationService;
import com.easyink.wecom.service.impl.CustomerCompensationServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 客户补偿控制器
 * 用于处理5月1日至5月5日期间的新增和流失客户补偿逻辑
 *
 * @author 系统
 * @date 2024-05-01
 */
@Slf4j
@RestController
@RequestMapping("/wecom/compensation")
@Api(tags = "客户补偿管理")
public class CustomerCompensationController extends BaseController {

    @Autowired
    private CustomerCompensationService customerCompensationService;

    /**
     * 补偿5月1日至5月5日期间的新增和流失客户
     */
    @PostMapping("/compensate/{corpId}")
    @ApiOperation("补偿指定企业的客户数据")
//    @PreAuthorize("@ss.hasPermi('wecom:compensation:compensate')")
    public AjaxResult compensateCustomers(
            @ApiParam(value = "企业ID", required = true)
            @PathVariable("corpId") String corpId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginTime, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime
            ) {
        
        log.info("开始执行客户补偿，企业ID：{}", corpId);
        beginTime = DateUtil.beginOfDay(beginTime);
        endTime = DateUtil.endOfDay(endTime);
        try {
            Map<String, Object> result = customerCompensationService.compensateCustomers(corpId, beginTime, endTime);
            
            if ("success".equals(result.get("status"))) {
                return AjaxResult.success("客户补偿处理成功", result);
            } else {
                return AjaxResult.error((String) result.get("message"));
            }
            
        } catch (Exception e) {
            log.error("客户补偿处理失败，企业ID：{}，错误：", corpId, e);
            return AjaxResult.error("客户补偿处理失败：" + e.getMessage());
        }
    }

    /**
     * 重新生成统计数据
     */
    @PostMapping("/regenerate/{corpId}")
    @ApiOperation("重新生成指定企业的统计数据")
//    @PreAuthorize("@ss.hasPermi('wecom:compensation:regenerate')")
    public AjaxResult regenerateStatistics(
            @ApiParam(value = "企业ID", required = true)
            @PathVariable("corpId") String corpId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginTime, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime
            ) {
        
        log.info("开始重新生成统计数据，企业ID：{}", corpId);
        beginTime = DateUtil.beginOfDay(beginTime);
        endTime = DateUtil.endOfDay(endTime);
        try {
            Map<String, Object> result = customerCompensationService.regenerateStatistics(corpId, beginTime, endTime);
            return AjaxResult.success("统计数据重新生成任务已提交", result);
            
        } catch (Exception e) {
            log.error("重新生成统计数据失败，企业ID：{}，错误：", corpId, e);
            return AjaxResult.error("重新生成统计数据失败：" + e.getMessage());
        }
    }


    /**
     * 测试查询指定企业的客户关系数据
     */
    @GetMapping("/test/query/{corpId}")
    @ApiOperation("测试查询指定企业的客户关系数据")
//    @PreAuthorize("@ss.hasPermi('wecom:compensation:test')")
    public AjaxResult testQueryCustomerRels(
            @ApiParam(value = "企业ID", required = true)
            @PathVariable("corpId") String corpId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginTime, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime
            ) {
        
        log.info("开始测试查询客户关系数据，企业ID：{}", corpId);
        beginTime = DateUtil.beginOfDay(beginTime);
        endTime = DateUtil.endOfDay(endTime);
        try {
            // 需要将服务实现类转换为具体类型以调用测试方法
            if (customerCompensationService instanceof CustomerCompensationServiceImpl) {
                CustomerCompensationServiceImpl serviceImpl = (CustomerCompensationServiceImpl) customerCompensationService;
                Map<String, Object> result = serviceImpl.testQueryCustomerRels(corpId, beginTime, endTime);
                
                if ("success".equals(result.get("status"))) {
                    return AjaxResult.success("测试查询成功", result);
                } else {
                    return AjaxResult.error((String) result.get("message"));
                }
            } else {
                return AjaxResult.error("服务类型不支持测试方法");
            }
            
        } catch (Exception e) {
            log.error("测试查询失败，企业ID：{}，错误：", corpId, e);
            return AjaxResult.error("测试查询失败：" + e.getMessage());
        }
    }
} 