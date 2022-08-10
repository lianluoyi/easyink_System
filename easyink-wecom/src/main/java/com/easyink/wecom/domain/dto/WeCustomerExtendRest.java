package com.easyink.wecom.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeCustomerExtendRest extends WeResultDTO{
    private List<Customer> customer;
    @Data
    public class Customer{
        private String external_userid;
        private Integer errcode;
    }

}
