package com.mcxgroup.business.feign;

import com.mcxgroup.common.req.MemberTicketReq;
import com.mcxgroup.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName MemberFeign
 * @Description
 * @Author McXen@2023/11/13
 **/
@FeignClient(name = "member",url = "127.0.0.1:8080")
public interface MemberFeign {
    @GetMapping("/member/feign/ticket/save")
    CommonResp<Object> save(@RequestBody MemberTicketReq req);
}
