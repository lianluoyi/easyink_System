package com.easywecom.wecom.domain.entity.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 类名： 朋友圈指定的发表范围
 * <p>
 * visible_range，分以下几种情况：
 * 若只指定sender_list，则可见的客户范围为该部分执行者的客户，目前执行者支持传userid与部门id列表，注意不在应用可见范围内的执行者会被忽略。
 * 若只指定external_contact_list，即指定了可见该朋友圈的目标客户，此时会将该发表任务推给这些目标客户的应用可见范围内的跟进人。
 * 若同时指定sender_list以及external_contact_list，会将该发表任务推送给sender_list指定的且在应用可见范围内的执行者，执行者发表后仅external_contact_list指定的客户可见。
 * 若未指定visible_range，则可见客户的范围为该应用可见范围内执行者的客户，执行者为应用可见范围内所有成员。
 * 注：若指定external_contact_list列表，则该条朋友圈为部分可见；否则为公开
 *
 * @author 佚名
 * @date 2022/1/6 16:32
 */
@ApiModel("朋友圈指定的发表范围")
@Data
@NoArgsConstructor
public class VisibleRange {
    @ApiModelProperty("发表任务的执行者列表")
    private SenderList sender_list;

    @ApiModelProperty("可见到该朋友圈的客户列表")
    private ExternalContactList external_contact_list;


    public VisibleRange(List<String> users, List<String> tags) {
        this.sender_list = new SenderList(users);
        this.external_contact_list = new ExternalContactList(tags);
    }

    @Data
    @ApiModel("发表任务的执行者列表")
    @NoArgsConstructor
    public static class SenderList {
        @ApiModelProperty("发表任务的执行者用户列表，最多支持10万个")
        private List<String> user_list;
        @ApiModelProperty("发表任务的执行者部门列表")
        private List<Integer> department_list;

        SenderList(List<String> users) {
            if (CollectionUtils.isNotEmpty(users)){
                this.user_list = users;
            }
        }
    }

    @Data
    @ApiModel("可见到该朋友圈的客户列表")
    public static class ExternalContactList {
        @ApiModelProperty("可见到该朋友圈的客户标签列表")
        private List<String> tag_list;

        ExternalContactList(List<String> tags) {
            if (CollectionUtils.isNotEmpty(tags)){
                this.tag_list = tags;
            }
        }
    }
}
