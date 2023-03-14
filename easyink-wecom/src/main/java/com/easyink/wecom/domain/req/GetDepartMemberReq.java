package com.easyink.wecom.domain.req;

import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WeAdminClient;
import com.easyink.wecom.domain.dto.autoconfig.BaseAdminResult;
import com.easyink.wecom.domain.resp.GetDepartMemberResp;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类名: 获取企业部门成员请求参数
 * 由企微官方后台接口https://work.weixin.qq.com/wework_admin/contacts/getDepartmentMember返回的数据
 * 由于企微官方现在没有返回员工数据，
 * 需要员工授权才能获取，所以easyink的解决方案是让管理员扫码登录后台后，
 * easyink 基于其登录session调用以上获取成员信息的接口 , 自动并保存这些消息
 * 因为不是openapi开放平台的接口
 * 所以字段如果没有明确的值， 大多数都是靠猜测其含义
 *
 * @author : silver_chariot
 * @date : 2023/2/27 17:57
 **/
@Data
public class GetDepartMemberReq {

    /**
     * 以下为固定参数
     */
    private static final String GET_CONTACTS_ACTION = "getpartycontacts";
    private static final int PAGE_LIMIT = 20 ;
    private String action = GET_CONTACTS_ACTION;
    private int fetchchild = 1;
    private boolean preFetch = true;
    private int joinstatus = 0;
    private int user_corp_cache = 0;

    /**
     * 以下为每次需要传的参数(必传）
     */
    /**
     * 页数 ,从0开始
     */
    private Integer page;
    /**
     * 页码 (企微后台最大limit 只能是20)
     */
    private Integer limit = PAGE_LIMIT;
    /**
     * 跟部门id,由扫码时获得
     */
    private String partyid;

    /**
     * 从企微后台拉取所有员工的私密信息
     *
     * @param qrcodeKey        扫码成功后的qrcodeKey
     * @param rootDepartmentId 根部门id
     * @return list of users
     */
    public static List<GetDepartMemberResp.MemberInfo> getAllMember(String qrcodeKey, String rootDepartmentId) {
        if (StringUtils.isAnyBlank(qrcodeKey, rootDepartmentId)) {
            return Collections.emptyList();
        }
        // 起始页数
        int page = 0;
        // 是否有下一页
        boolean hasNextPage ;
        // 是否有下下页
        boolean hasNextNextPage ;
        List<GetDepartMemberResp.MemberInfo> allMemberList = new ArrayList<>();
        GetDepartMemberReq req = new GetDepartMemberReq()
                .page(page)
                .partyid(rootDepartmentId);
        WeAdminClient weAdminClient = SpringUtils.getBean(WeAdminClient.class);
        BaseAdminResult<GetDepartMemberResp> resp  ;
        // 分页获取企业员工详细信息,每次获取2页数据,减少请求次数（企微限制一次最多limit =20 ,但是会返回下一页数据,所以一次最多可以获取到40条数据）
        do {
            resp  = weAdminClient.getDepartMember(qrcodeKey, req);
            if(resp == null || resp.getData() == null || resp.getData().getContact_list() == null ||
                     CollectionUtils.isEmpty(resp.getData().getContact_list().getList())) {
                break;
            }
            allMemberList.addAll(resp.getData().getContact_list().getList()) ;
            // 判断是否有下一页
            hasNextPage = resp.getData().getNext_page_contact_list() != null &&CollectionUtils.isNotEmpty(resp.getData().getNext_page_contact_list().getList());
            if(hasNextPage) {
                allMemberList.addAll(resp.getData().getNext_page_contact_list().getList());
            }
            // 判断是否有下下页( 下一页页数 == 最大页数)
            hasNextNextPage = hasNextPage && resp.getData().getNext_page_contact_list().getList().size() == PAGE_LIMIT ;
            // 增加页数继续获取,每次翻两页
            page = page +2 ;
            req.setPage(page);
        }while(hasNextNextPage ) ;
        return allMemberList;
    }


    public GetDepartMemberReq page(Integer page) {
        setPage(page);
        return this;
    }

    public GetDepartMemberReq limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    public GetDepartMemberReq partyid(String partyid) {
        setPartyid(partyid);
        return this;
    }


}
