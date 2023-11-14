package com.mcxgroup.member.controller;

import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.member.req.TicketQueryReq;
import com.mcxgroup.member.resp.TicketQueryResp;
import com.mcxgroup.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author MCXEN
* @project Train
*/

@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Resource
    private TicketService ticketService;


    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryResps(@Valid TicketQueryReq req) {
        CommonResp<PageResp<TicketQueryResp>> commonResp = new CommonResp<>();
        req.setMemberId(LoginMemberContext.getId());
        PageResp<TicketQueryResp> list = ticketService.queryList(req);
        commonResp.setContent(list);
        return commonResp;
    }

}
