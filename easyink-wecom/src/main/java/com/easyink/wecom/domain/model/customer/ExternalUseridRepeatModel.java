package com.easyink.wecom.domain.model.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 员工客户去重model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUseridRepeatModel {
    /**
     * 客户externalUserid
     */
    private String externalUserid;
    /**
     * 员工userId
     */
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ExternalUseridRepeatModel))
            return false;
        ExternalUseridRepeatModel that = (ExternalUseridRepeatModel) o;
        return Objects.equals(externalUserid, that.externalUserid) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalUserid, userId);
    }
}