package com.easyink.wecom.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.annotation.Excel;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.common.utils.DictUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.dto.customer.ExternalContact;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.util.*;

/**
 * 企业微信客户对象 we_customer
 *
 * @author 佚名
 * @date 2021-7-28
 */
@ApiModel("企业微信客户对象")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_customer")
@Slf4j
public class WeCustomer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "外部联系人的userid")
    @TableId
    @NotBlank(message = "外部联系人的id不可为空")
    @TableField("external_userid")
    private String externalUserid;

    @ApiModelProperty(value = "外部联系人名称")
    @TableField("name")
    @Excel(name = "客户")
    @ExcelProperty(value = "客户",index = 1)
    private String name;

    /**
     * 企业id
     */
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "外部联系人头像")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "外部联系人性别 0-未知 1-男性 2-女性")
    @TableField("gender")
    private String gender;

    @ApiModelProperty(value = "外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。")
    @TableField("unionid")
    private String unionid;

    @ApiModelProperty(value = "生日")
    @TableField("birthday")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", dateFormat = "yyyy-MM-dd")
    @ExcelProperty(value = "出生日期",index = 9)
    @DateTimeFormat("yyyy-MM-dd")
    private Date birthday;

    /**
     * 用于高级筛选进行生日搜索
     */
    @TableField(exist = false)
    @ApiModelProperty("高级筛选生日")
    private String birthdayStr;

    @ApiModelProperty(value = "客户企业简称")
    @TableField("corp_name")
//    @Excel(name = "公司",sort = 3)
//    @ExcelProperty(value = "公司",index = 3)
    private String corpName;

    @ApiModelProperty(value = "客户企业全称")
    @TableField("corp_full_name")
    private String corpFullName;

    @ApiModelProperty(value = "客户职位")
    @TableField("position")
    private String position;

    @ApiModelProperty(value = "是否开启会话存档 0：关闭 1：开启")
    @TableField("is_open_chat")
    private Integer isOpenChat;

    /**
     * 添加人员
     */
    @TableField(exist = false)
    private List<WeFlowerCustomerRel> weFlowerCustomerRels;

    /**
     * 添加人id
     */
    @TableField(exist = false)
    private String userIds;

    /**
     * 标签
     */
    @TableField(exist = false)
    private String tagIds;

    @TableField(exist = false)
    @JSONField(defaultValue = "0")
    private String status;

    /**
     * 部门
     */
    @TableField(exist = false)
    private String departmentIds;

    /**
     * 添加人名称
     */
    @TableField(exist = false)
    @Excel(name = "所属员工")
    @ExcelProperty(value = "所属员工",index = 5)
    private String userName;

    /**
     * 员工id
     */
    @TableField(exist = false)
    private String userId;


    /**
     * 创建者
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private String createBy;

    /**
     * 更新者
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private String updateBy;

    /**
     * 备注
     */
    @TableField(exist = false)
    @Excel(name = "备注")
    @ExcelProperty(value = "备注",index = 2)
    private String remark;
    /**
     * 手机号
     */
    @TableField(exist = false)
    @Excel(name = "电话")
    @ExcelProperty(value = "电话",index = 10)
    private String phone;
    /**
     * 描述
     */
    @TableField(exist = false)
    private String desc;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询过滤类型：1：根据客户id：external_userid过滤，2：根据员工id：user_id和客户id：external_userid")
    private String queryType;


    @ApiModelProperty(value = "部门")
    @Excel(name = "所属部门")
    @TableField(exist = false)
    @ExcelProperty(value = "所属部门",index = 6)
    private String departmentName;

    @ApiModelProperty(value = "跟进人离职时间")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dimissionTime;

    @ApiModelProperty(value = "客户自定义字段")
    @TableField(exist = false)
    private List<BaseExtendPropertyRel> extendProperties = new ArrayList<>();

    @ApiModelProperty(value = "接替状态,1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限 5-无接替记录")
    @TableField(exist = false)
    private Integer transferStatus;

    @ApiModelProperty(value = "扩展属性筛选结果")
    @TableField(exist = false)
    private List<WeCustomerRel> extendList;

    @ApiModelProperty(value = "标签关系筛选结果的客户关系id")
    @TableField(exist = false)
    private List<Long> relIds;

    /**
     * 匹配的标签，使用","分隔。
     */
    @ApiModelProperty(value = "匹配的标签")
    @TableField(exist = false)
    private String markTagIds;

    @ApiModelProperty(value = "过滤查询条件的客户id列表")
    @TableField(exist = false)
    private List<String> filterExternalUseridList;
    /**
     * 是否对客户去重员工, 默认false, 兼容原来的
     */
    @TableField(exist = false)
    private boolean duplicate = false;


    /**
     * 根据API返回的客户详情实体 构建数据交互的企微客户实体
     *
     * @param externalContact {@link ExternalContact}
     * @param corpId          企业ID
     */
    public WeCustomer(ExternalContact externalContact, String corpId) {
        this.externalUserid=externalContact.getExternalUserid();
        this.name=externalContact.getName();
        this.position=externalContact.getPosition();
        this.avatar=externalContact.getAvatar();
        this.corpName=externalContact.getCorpName();
        this.corpFullName=externalContact.getCorpFullName();
        this.type=externalContact.getType();
        this.gender= String.valueOf(externalContact.getGender());
        this.unionid=externalContact.getUnionid();
        this.corpId = corpId;
        if (StringUtils.isBlank(externalContact.getUnionid())) {
            this.unionid = StringUtils.EMPTY;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeCustomer weCustomer = (WeCustomer) o;
        return Objects.equals(corpId, weCustomer.getCorpId())
                && Objects.equals(externalUserid, weCustomer.externalUserid);

    }

    @Override
    public int hashCode() {
        return Objects.hash(corpId, externalUserid);
    }

    /**
     * 将以","分隔的部门id字符串，转换为部门id列表
     *
     * @return 部门id列表
     */
    public List<String> convertDepartmentList() {
        if (StringUtils.isBlank(this.getDepartmentIds())) {
            return Collections.emptyList();
        }
        return Arrays.asList(this.departmentIds.split(DictUtils.SEPARATOR));
    }

    /**
     * 将以","分隔的员工id字符串，转换为员工id列表
     *
     * @return 员工id列表
     */
    public List<String> convertUserIdList() {
        if (StringUtils.isBlank(this.userIds)) {
            return Collections.emptyList();
        }
        return Arrays.asList(this.userIds.split(DictUtils.SEPARATOR));
    }

    /**
     * 添加过滤查询条件的客户id列表
     *
     * @param externalUseridList 客户id列表
     */
    public void addFilterExternalUserIdList(List<String> externalUseridList) {
        // 不存在，就初始化
        if (CollectionUtils.isEmpty(this.filterExternalUseridList)) {
            this.filterExternalUseridList = new ArrayList<>();
        }
        this.filterExternalUseridList.addAll(externalUseridList);
        // 过滤，去重
        HashSet<String> hashSet = new HashSet<>(this.filterExternalUseridList);
        List<String> distinctList = new ArrayList<>(hashSet);
        this.setFilterExternalUseridList(distinctList);
    }

}
