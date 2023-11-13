package com.mcxgroup.member.controller.feign;

import com.mcxgroup.common.req.MemberTicketReq;
import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.member.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName FeignTicketController
 * @Description Feign调用Service
 * @Author McXen@2023/11/13
 **/
@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {
    @Autowired
    private TicketService ticketService;
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketReq memberTicketReq){
        ticketService.save(memberTicketReq);
        return new CommonResp<>();
    }
}
