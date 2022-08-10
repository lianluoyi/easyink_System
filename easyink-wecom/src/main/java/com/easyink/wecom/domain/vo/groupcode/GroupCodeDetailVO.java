package com.easyink.wecom.domain.vo.groupcode;

import com.easyink.wecom.domain.WeGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 群活码详情VO
 *
 * @author tigger
 * 2022/2/10 17:01
 **/
@Data
public class GroupCodeDetailVO {

    @ApiModelProperty("创建类型 创建类型 1:群二维码 2: 企微活码")
    private Integer createType;
    @ApiModelProperty("客户群码id")
    private Long groupCodeId;

    @ApiModelProperty("群二维码方式群id")
    private String chatId;
    @ApiModelProperty("企微活码方式群id")
    private String chatIds;
    @ApiModelProperty("群聊名称")
    private String chatGroupName;
    @ApiModelProperty("群名称")
    private String groupName;
    @ApiModelProperty("实际码id")
    private Long id;
    @ApiModelProperty("实际二维码")
    private String actualGroupQrCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("有效期时间")
    private Date effectTime;
    @ApiModelProperty("是否即将过期 0：否 1：是")
    private Integer isExpire;
    @ApiModelProperty("场景。1 - 群的小程序插件 2 - 群的二维码插件")
    private Integer scene;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty(value = "扫码次数")
    private Integer scanCodeTimes;
    @ApiModelProperty(value = "扫码次数限制")
    private Integer scanCodeTimesLimit;
    @ApiModelProperty(value = "起始序号")
    private Integer roomBaseId;
    @ApiModelProperty(value = "群名前缀")
    private String roomBaseName;
    @ApiModelProperty(value = "使用状态 0:使用中 1:已停用")
    private Integer status;
    @ApiModelProperty(value = "群二维码方式删除状态 0:正常使用;1:删除")
    private Integer delFlag;
    @ApiModelProperty("配置id")
    private String configId;
    @ApiModelProperty("排序号")
    private Integer sortNo;
    @ApiModelProperty("选择的群详情列表")
    private List<WeGroup> groupDetailVOList;

}
