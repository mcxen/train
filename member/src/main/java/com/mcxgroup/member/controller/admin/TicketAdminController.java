package com.mcxgroup.member.controller.admin;

import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.member.req.TicketQueryReq;
import com.mcxgroup.member.req.TicketSaveReq;
import com.mcxgroup.member.resp.TicketQueryResp;
import com.mcxgroup.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @author MCXEN
* @project Train
*/

@RestController
@RequestMapping("/admin/ticket")
public class TicketAdminController {
    @Resource
    private TicketService ticketService;


    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryResps(@Valid TicketQueryReq req) {
        PageResp<TicketQueryResp> list = ticketService.queryList(req);
        return new CommonResp<>(list);
    }

}
