package com.easyink.wecom.domain.model.customer;

import com.easyink.common.annotation.EncryptField;
import com.easyink.common.annotation.EncryptFields;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 地址详情model
 *
 * @author tigger
 * 2025/4/29 14:34
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地区编码
     */
    private Integer adCode;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String area;

    /**
     * 街道/镇
     */
    private String town;

    /**
     * 详细地址
     */
    @EncryptField(EncryptField.FieldType.ADDRESS)
    private String detailAddress;
    private String detailAddressEncrypt;

    /**
     * 位置坐标
     */
    @EncryptFields
    private Location location;

    /**
     * 转换为导出的字符内容
     *
     * @return 内容
     */
    public String transferToExportString() {
        if (isDeleteLocation()) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(province)) {
            sb.append(province);
        }
        if (StringUtils.isNotBlank(city)) {
            sb
                    .append("-")
                    .append(city);
        }

        if (StringUtils.isNotBlank(this.area)) {
            sb.append("-")
                    .append(area);
        }
        if (StringUtils.isNotBlank(this.town)) {
            sb.append("-")
                    .append(town);
        }
        if (StringUtils.isNotBlank(detailAddress)) {
            sb
                    .append(" ")
                    .append(detailAddress);
        }
        if (location != null) {
            sb.append("\n")
                    .append("(")
                    .append("经纬度:")
                    .append(location.getLat() == null ? StringUtils.EMPTY : location.getLat())
                    .append(",")
                    .append(location.getLng() == null ? StringUtils.EMPTY : location.getLng())
                    .append(")");
        }
        return sb.toString();
    }

    /**
     * 转换为轨迹记录的字符串
     *
     * @return 轨迹记录的字符串
     */
    public String transferToTrajectoryString() {
        if (isDeleteLocation()) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(province)) {
            sb.append(province);
        }
        if (StringUtils.isNotBlank(city)) {
            sb
                    .append("-")
                    .append(city);
        }
        if (StringUtils.isNotBlank(this.area)) {
            sb.append("-")
                    .append(area);
        }
        if (StringUtils.isNotBlank(town)) {
            sb.append("-")
                    .append(town);
        }
        if (StringUtils.isNotBlank(detailAddress)) {
            sb
                    .append(" ")
                    .append(detailAddress);
        }
        return sb.toString();
    }

    /**
     * 是否删除位置信息
     *
     * @return 情况位置, 会都传null
     */
    private boolean isDeleteLocation() {
        return StringUtils.isAllBlank(province, city, area, town, detailAddress);
    }

    @Data
    public static class Location implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 纬度
         */
        @EncryptField(EncryptField.FieldType.COMMON)
        private Double lat;

        /**
         * 经度
         */
        @EncryptField(EncryptField.FieldType.COMMON)
        private Double lng;
    }
}