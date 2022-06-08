package com.easywecom.wecom.domain.dto;

import com.easywecom.wecom.domain.WeCustomer;
import com.easywecom.wecom.domain.WeFlowerCustomerRel;
import lombok.Data;

/**
 * @description: 客户实体
 * @author admin
 * @create: 2020-09-15 17:43
 **/
@Data
public class WeCustomerDTO extends WeResultDTO {


    /**
     * 客户id集合
     */
    private String[] external_userid;

    /**
     * 客户详情
     */
    private ExternalContact external_contact;

//    /** 客户联系人 */
//    private List<WeFollowUserDto> follow_user;


    @Data
    public class ExternalContact {
        /**
         * 外部联系人userId
         */
        private String external_userid;
        /**
         * 外部联系人名称
         */
        private String name;
        /**
         * 外部联系人职位
         */
        private String position;
        /**
         * 外部联系人头像
         */
        private String avatar;
        /**
         * 外部联系人所在企业简称
         */
        private String corp_name;
        /**
         * 外部联系人所在企业全称
         */
        private String corp_full_name;
        /**
         * 外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户
         */
        private Integer type;
        /**
         * 外部联系人性别 0-未知 1-男性 2-女性
         */
        private Integer gender;
        /**
         * 外部联系人在微信开放平台的唯一身份标识（微信unionid），通过此字段企业可将外部联系人与公众号/小程序用户关联起来。
         */
        private String unionid;

    }

    @Data
    public class WeCustomerRemark {
        private String userid;
        private String external_userid;
        private String remark;
        private String description;
        private String remark_company;
        private String[] remark_mobiles;
        private String remark_pic_mediaid;

        public WeCustomerRemark() {
        }

        public WeCustomerRemark(WeCustomer weCustomer) {
            this.external_userid = weCustomer.getExternalUserid();
            this.userid = weCustomer.getUserId();
            if (weCustomer.getRemark() != null) {
                this.remark = weCustomer.getRemark();
            }
            if (weCustomer.getDesc() != null) {
                this.description = weCustomer.getDesc();
            }
            if (weCustomer.getPhone() != null) {
                this.remark_mobiles = new String[]{weCustomer.getPhone()};
            }
        }

        /**
         * 根据跟进人-客户关系实体构建 修改外部联系人备注请求体
         *
         * @param rel {@link WeFlowerCustomerRel}
         */
        public WeCustomerRemark(WeFlowerCustomerRel rel) {
            this.external_userid = rel.getExternalUserid();
            this.userid = rel.getUserId();
            if (rel.getRemark() != null) {
                this.remark = rel.getRemark();
            }
            if (rel.getDescription() != null) {
                this.description = rel.getDescription();
            }
            if (rel.getRemarkMobiles() != null) {
                this.remark_mobiles = new String[]{rel.getRemarkMobiles()};
            }
        }
    }


//    public WeCustomer transformWeCustomer(){
//        WeCustomer weCustomer=new WeCustomer();
//
//        if(null != external_contact){
//            BeanUtils.copyPropertiesignoreOther(external_contact,weCustomer);
//        }
//
//        if(CollectionUtil.isNotEmpty(follow_user)){
//            List<WeFlowerCustomerRel> weFlowerCustomerRels=new ArrayList<>();
//            List<WeTagDTO> weTagDtos=new ArrayList<>();
//            follow_user.stream().forEach(k->{
//                WeFlowerCustomerRel weFlowerCustomerRel=new WeFlowerCustomerRel();
//                BeanUtils.copyPropertiesignoreOther(k,weFlowerCustomerRel);
//                weFlowerCustomerRels.add(weFlowerCustomerRel);
//                if(CollectionUtil.isNotEmpty(k.getTags())){
//                    k.getTags().stream().forEach(v->v.setFlower_customer_rel_id(k.getId()));
//                    weTagDtos.addAll(k.getTags());
//                }
//            });
//
//        }
//
//        return weCustomer;
//    }


}
