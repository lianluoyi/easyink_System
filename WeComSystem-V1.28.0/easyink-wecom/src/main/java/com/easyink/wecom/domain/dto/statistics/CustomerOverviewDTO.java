package com.easyink.wecom.domain.dto.statistics;

import lombok.Data;

/**
 * 客户概览员工维度查询DTO
 *
 * @author wx
 * 2023/2/14 17:28
 **/
@Data
public class CustomerOverviewDTO extends StatisticsDTO{

    public String getTotalContactCntSort() {
        if(totalContactCntSort == null) {
            return null;
        }
        if ("ASC".equalsIgnoreCase(totalContactCntSort)) {
            return "ASC";
        }else {
            return "DESC";
        }
    }

    public String getContactLossCntSort() {
        if(contactLossCntSort == null) {
            return null;
        }
        if ("ASC".equalsIgnoreCase(contactLossCntSort)) {
            return "ASC";
        }else {
            return "DESC";
        }
    }

    public String getNewContactCntSort() {
        if(newContactCntSort == null) {
            return null;
        }
        if ("ASC".equalsIgnoreCase(newContactCntSort)) {
            return "ASC";
        }else {
            return "DESC";
        }
    }

    public String getNewContactRetentionRateSort() {
        if(newContactRetentionRateSort == null) {
            return null;
        }
        if ("ASC".equalsIgnoreCase(newContactRetentionRateSort)) {
            return "ASC";
        }else {
            return "DESC";
        }
    }

    public String getNewContactStartTalkRateSort() {
        if(newContactStartTalkRateSort == null) {
            return null;
        }
        if ("ASC".equalsIgnoreCase(newContactStartTalkRateSort)) {
            return "ASC";
        }else {
            return "DESC";
        }
    }

    public String getServiceResponseRateSort() {
        if(serviceResponseRateSort == null) {
            return null;
        }
        if ("ASC".equalsIgnoreCase(serviceResponseRateSort)) {
            return "ASC";
        }else {
            return "DESC";
        }
    }

    /**
     * 客户总数排序 正序asc 倒叙desc
     */
    private String totalContactCntSort;

    /**
     * 流失客户数排序 正序asc 倒叙desc
     */
    private String contactLossCntSort;

    /**
     * 新增客户数排序 正序asc 倒叙desc
     */
    private String newContactCntSort;

    /**
     * 新客留存率排序 正序asc 倒叙desc
     */
    private String newContactRetentionRateSort;

    /**
     * 新客开口率排序 正序asc 倒叙desc
     */
    private String newContactStartTalkRateSort;

    /**
     * 服务响应率排序 正序asc 倒叙desc
     */
    private String serviceResponseRateSort;

    /**
     * 排序类型名称
     */
    private String sortName;

    /**
     * 排序方式 正序ASC 倒叙DESC
     */
    private String sortType;
}
