package com.easyink.wecom.domain.dto.app;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 类名: ToOpenCorpIdResp
 *
 * @author: 1*+
 * @date: 2021-11-29 18:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToOpenCorpIdResp extends WeResultDTO implements Serializable {


    @SerializedName("open_corpid")
    private String openCorpId;


}
