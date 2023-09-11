package com.easyink.wecom.domain.dto.emplecode;

import com.easyink.wecom.domain.dto.WeResultDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 获客链接响应结果实体
 *
 * @author lichaoyu
 * @date 2023/8/22 15:58
 */
@Data
public class CustomerAssistantResp extends WeResultDTO {

    @ApiModelProperty("获客链接响应结果实体")
    private Link link;

    @ApiModelProperty("获客助手-链接使用详情响应结果实体")
    private Quota quota;

    @Data
    @ApiModel("获客链接响应结果实体")
    public static class Link extends WeResultDTO {
        @ApiModelProperty("获客链接id")
        private String link_id;
        @ApiModelProperty("获客链接的名称")
        private String link_name;
        @ApiModelProperty("获客链接")
        private String url;
        @ApiModelProperty("获客链接创建时间")
        private Long create_time;
    }

    @Data
    @ApiModel("获客助手-链接使用详情响应结果实体")
    public static class Quota extends WeResultDTO {

        @ApiModelProperty("历史累计使用量")
        private Integer total;
        @ApiModelProperty("剩余使用量")
        private Integer balance;
        @ApiModelProperty("获客助手-链接额度响应结果实体")
        private List<QuoTaList> quota_list;

        @Data
        @ApiModel("获客助手-链接额度响应结果实体")
        public static class QuoTaList extends WeResultDTO {

            @ApiModelProperty("额度过期时间戳，为过期日的零点，实际过期时间取决于额度的购买时间")
            private Long expire_date;
            @ApiModelProperty("即将过期额度数量")
            private Integer balance;
        }
    }
}
