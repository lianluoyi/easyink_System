package com.easyink.wecom.domain.enums.statistics;

import com.easyink.common.constant.GenConstants;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.dto.statistics.CustomerOverviewDTO;
import com.easyink.wecom.domain.dto.statistics.WeTagStatisticsDTO;
import com.easyink.wecom.domain.vo.statistics.CustomerOverviewDateVO;
import com.easyink.wecom.domain.vo.statistics.UserServiceTimeVO;
import com.easyink.wecom.domain.vo.statistics.WeTagCustomerStatisticsVO;
import com.easyink.wecom.domain.vo.statistics.WeTagGroupStatisticsVO;
import lombok.Getter;

import org.apache.commons.collections4.CollectionUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 数据统计枚举类
 *
 * @author lichaoyu
 * @date 2023/4/20 20:34
 */
public class StatisticsEnum {

    /**
     * 客户联系-日期维度-排序类型枚举类
     *
     * @author lichaoyu
     * @date 2023/4/20 18:31
     */
    public enum CustomerOverviewSortTypeEnum {

        /**
         * 客户总数排序
         */
        TOTAL_ALL_CONTACT_CNT_SORT("totalAllContactCntSort", Comparator.comparing(CustomerOverviewDateVO::getTotalAllContactCnt)),
        /**
         * 留存客户总数排序
         */
        TOTAL_CONTACT_CNT_SORT("totalContactCntSort", Comparator.comparing(CustomerOverviewDateVO::getTotalContactCnt)),
        /**
         * 流失客户数排序
         */
        CONTACT_LOSS_CNT_SORT("contactLossCntSort", Comparator.comparing(CustomerOverviewDateVO::getContactLossCnt)),
        /**
         * 新增客户数排序
         */
        NEW_CONTACT_CNT_SORT("newContactCntSort", Comparator.comparing(CustomerOverviewDateVO::getNewContactCnt)),
        /**
         * 新客留存率排序
         */
        NEW_CONTACT_RETENTION_RATE_SORT("newContactRetentionRateSort", Comparator.comparing(CustomerOverviewDateVO::getNewContactRetentionRateBySort)),
        /**
         * 新客开口率排序
         */
        NEW_CONTACT_START_TALK_RATE_SORT("newContactStartTalkRateSort", Comparator.comparing(CustomerOverviewDateVO::getNewContactStartTalkRateBySort)),
        /**
         * 服务响应率排序
         */
        SERVICE_RESPONSE_RATE_SORT("serviceResponseRateSort", Comparator.comparing(CustomerOverviewDateVO::getServiceResponseRateBySort)),
        ;
        @Getter
        private final Comparator sortType;
        @Getter
        private final String sortName;

        CustomerOverviewSortTypeEnum(String sortName, Comparator sortType) {
            this.sortName = sortName;
            this.sortType = sortType;
        }

        /**
         * 根据类型获取枚举对象
         *
         * @param sortName 排序类型
         * @return Optional 排序对象
         */
        public static Optional<Comparator> getByCode(String sortName) {
            for (CustomerOverviewSortTypeEnum value : values()) {
                if (value.sortName.equals(sortName)) {
                    return Optional.ofNullable(value.sortType);
                }
            }
            return Optional.empty();
        }

        /**
         * 排序
         *
         * @param dto                    {@link CustomerOverviewDTO}
         * @param customerOverviewVOList 要排序的结果集
         */
        public static void sort(CustomerOverviewDTO dto, List<CustomerOverviewDateVO> customerOverviewVOList) {
            if (dto == null || CollectionUtils.isEmpty(customerOverviewVOList)) {
                throw new CustomException(ResultTip.TIP_PARAM_MISSING);
            }
            if (dto.getSortType() == null || dto.getSortName() == null){
                // 默认时间降序排序
                customerOverviewVOList.sort(Comparator.comparing(CustomerOverviewDateVO::getXTime).reversed());
            } else {
                // 判断排序类型 ASC 正序, DESC 倒叙
                switch (dto.getSortType()) {
                    case GenConstants.ASC:
                        customerOverviewVOList.sort(getByCode(dto.getSortName()).get());
                        break;
                    case GenConstants.DESC:
                        customerOverviewVOList.sort(getByCode(dto.getSortName()).get().reversed());
                        break;
                }
            }
        }

    }

    /**
     * 员工服务-数据总览-时间维度排序类型枚举类
     *
     * @author zhaorui
     * @date 2023/4/21 14:12
     */
    public enum UserServiceSortOfTimeEnums {

        /**
         * 聊天总数排序
         */
        CHAT_TOTAL("chatTotalSort",Comparator.comparing(UserServiceTimeVO::getChatTotal)),
        /**
         * 发送消息数排序
         */
        SEND_CONTACT_CNT("sendContactCntSort",Comparator.comparing(UserServiceTimeVO::getSendContactCnt)),
        /**
         * 平均会话数排序
         */
        AVERAGE_CHAT_TOTAL("averageChatTotalSort",Comparator.comparing(UserServiceTimeVO::getAverageChatTotalTmp)),
        /**
         * 平均首次回复时长排序
         */
        AVERAGE_FIRST_REPLY_DURATION("averageFirstReplyDurationSort",Comparator.comparing(UserServiceTimeVO::getAverageFirstReplyDurationTmp)),
        /**
         * 回复率排序
         */
        REPLY_RATE("replyRateSort",Comparator.comparing(UserServiceTimeVO::getReplayRateTmp)),
        /**
         * 有效沟通客户数排序
         */
        EFFECTIVE_COMMUNICATION_CUSTOMER_CNT("effectiveCommunicationCustomerCntSort",Comparator.comparing(UserServiceTimeVO::getEffectiveCommunicationCustomerCnt)),
        /**
         * 有效沟通率排序
         */
        EFFECTIVE_COMMUNICATION_RATE("effectiveCommunicationRateSort",Comparator.comparing(UserServiceTimeVO::getEffectiveCommunicationRateTmp)),
        /**
         * 客户好评率排序
         */
        CUSTOMER_POSITIVE_COMMENTS_RATE("customerPositiveCommentsRateSort",Comparator.comparing(UserServiceTimeVO::getCustomerPositiveCommentsRateTmp));

        @Getter
        private final String sortName;

        @Getter
        private final Comparator sortWay;

        UserServiceSortOfTimeEnums(String sortName,Comparator sortWay){
            this.sortName=sortName;
            this.sortWay=sortWay;
        }

        /**
         * 进行排序
         *
         * @param sortname 需要排序的字段
         * @param voList 需要排序的列表
         * @param sortType 排序类型，正序还是倒序
         */
        public  static void sortList(String sortname, List<UserServiceTimeVO> voList,String sortType){
            for (UserServiceSortOfTimeEnums enums: UserServiceSortOfTimeEnums.values()){
                if (enums.sortName.equals(sortname)){
                    if (sortType.equals(GenConstants.DESC)) {
                        voList.sort(enums.sortWay.reversed());
                    } else {
                        voList.sort(enums.sortWay);
                    }
                }
            }
        }
    }

    public enum CustomerTagSortEnum {

        /**
         * 标签下客户数排序
         */
        CUSTOMER_CNT_SORT("customerCntSort", Comparator.comparing(WeTagCustomerStatisticsVO::getCustomerCnt)),

        /**
         * 创建时间排序
         */
        CREATE_TIME_SORT("createTimeSort", Comparator.comparing(WeTagCustomerStatisticsVO::getCreateTime));

        @Getter
        private final Comparator sortType;
        @Getter
        private final String sortName;

        CustomerTagSortEnum(String sortName,Comparator sortType) {
            this.sortName = sortName;
            this.sortType = sortType;
        }

        /**
         * 根据类型获取枚举对象
         *
         * @param sortName 排序类型
         * @return Optional 排序对象
         */
        public static Optional<Comparator> getByCode(String sortName) {
            for (CustomerTagSortEnum value : values()) {
                if (value.sortName.equals(sortName)) {
                    return Optional.ofNullable(value.sortType);
                }
            }
            return Optional.empty();
        }


        /**
         * 排序
         *
         * @param dto                    {@link WeTagStatisticsDTO}
         * @param customerOverviewVOList 要排序的结果集
         */
        public static void sort(WeTagStatisticsDTO dto, List<WeTagCustomerStatisticsVO> customerOverviewVOList) {
            if (dto == null || CollectionUtils.isEmpty(customerOverviewVOList)) {
                throw new CustomException(ResultTip.TIP_PARAM_MISSING);
            }
            if (dto.getSortType() == null || dto.getSortName() == null){
                // 默认时间正序
                customerOverviewVOList.sort(Comparator.comparing(WeTagCustomerStatisticsVO::getCreateTime));
            } else {
                // 判断排序类型 ASC 正序, DESC 倒叙
                switch (dto.getSortType()) {
                    case GenConstants.ASC:
                        customerOverviewVOList.sort(getByCode(dto.getSortName()).get());
                        break;
                    case GenConstants.DESC:
                        customerOverviewVOList.sort(getByCode(dto.getSortName()).get().reversed());
                        break;
                }
            }
        }
    }
    public enum GroupTagSortEnum {

        /**
         * 标签下客户数排序
         */
        CUSTOMER_CNT_SORT("customerCntSort", Comparator.comparing(WeTagGroupStatisticsVO::getCustomerCnt)),

        /**
         * 创建时间排序
         */
        CREATE_TIME_SORT("createTimeSort", Comparator.comparing(WeTagGroupStatisticsVO::getCreateTime));

        @Getter
        private final Comparator sortType;
        @Getter
        private final String sortName;

        GroupTagSortEnum(String sortName,Comparator sortType) {
            this.sortName = sortName;
            this.sortType = sortType;
        }

        /**
         * 根据类型获取枚举对象
         *
         * @param sortName 排序类型
         * @return Optional 排序对象
         */
        public static Optional<Comparator> getByCode(String sortName) {
            for (GroupTagSortEnum value : values()) {
                if (value.sortName.equals(sortName)) {
                    return Optional.ofNullable(value.sortType);
                }
            }
            return Optional.empty();
        }


        /**
         * 排序
         *
         * @param dto                    {@link WeTagStatisticsDTO}
         * @param weTagGroupStatisticsVOS 要排序的结果集
         */
        public static void sort(WeTagStatisticsDTO dto, List<WeTagGroupStatisticsVO> weTagGroupStatisticsVOS) {
            if (dto == null || CollectionUtils.isEmpty(weTagGroupStatisticsVOS)) {
                throw new CustomException(ResultTip.TIP_PARAM_MISSING);
            }
            if (dto.getSortType() == null || dto.getSortName() == null){
                // 默认时间正序
                weTagGroupStatisticsVOS.sort(Comparator.comparing(WeTagGroupStatisticsVO::getCreateTime));
            } else {
                // 判断排序类型 ASC 正序, DESC 倒叙
                switch (dto.getSortType()) {
                    case GenConstants.ASC:
                        weTagGroupStatisticsVOS.sort(getByCode(dto.getSortName()).get());
                        break;
                    case GenConstants.DESC:
                        weTagGroupStatisticsVOS.sort(getByCode(dto.getSortName()).get().reversed());
                        break;
                }
            }
        }
    }
}
