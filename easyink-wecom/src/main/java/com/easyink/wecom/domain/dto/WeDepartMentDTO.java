package com.easyink.wecom.domain.dto;

import cn.hutool.core.collection.CollectionUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.common.core.domain.wecom.WeDepartment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 * @description: 企业部门
 * @create: 2020-08-27 15:54
 **/
@Data
public class WeDepartMentDTO extends WeResultDTO {


    private List<DeartMentDto> department = new ArrayList<>();

    public WeDepartMentDTO() {

    }


    public WeDepartMentDTO(WeDepartment weDepartment) {

        DeartMentDto deartMentDto = new WeDepartMentDTO().new DeartMentDto();
        BeanUtils.copyPropertiesignoreOther(weDepartment, deartMentDto);
        department.add(deartMentDto);
    }

    /**
     * 企业微信通讯录部门的dto对象转化为系统通讯录部门对象
     *
     * @param deartMentDto 企微API返回参数实体
     * @return 与数据库交互实体
     */
    public static WeDepartment transformWeDepartment(WeDepartMentDTO.DeartMentDto deartMentDto) {
        WeDepartment weDepartment = new WeDepartment();
        BeanUtils.copyPropertiesignoreOther(deartMentDto, weDepartment);
        return weDepartment;
    }

    public List<WeDepartment> findWeDepartments(String corpId) {
        List<WeDepartment> weDepartments = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(this.department)) {
            this.department.stream().forEach(k -> {
                weDepartments.add(
                        WeDepartment.builder()
                                .id(k.getId())
                                .name(StringUtils.isNotBlank(k.getName()) ? k.getName() : StringUtils.EMPTY)
                                .parentId(k.getParentid())
                                .corpId(corpId)
                                .build()
                );
            });

        }
        return weDepartments;
    }


    @Data
    public class DeartMentDto {

        private Long id;

        private String name;

        private Long parentid;

        public DeartMentDto() {
        }

        /**
         * 构建访问企微API实体
         *
         * @param weDepartment 数据库交互实体
         */
        public DeartMentDto(WeDepartment weDepartment) {
            BeanUtils.copyPropertiesignoreOther(weDepartment, this);
        }

    }


}
