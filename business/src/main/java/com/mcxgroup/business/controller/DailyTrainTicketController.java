package com.mcxgroup.business.controller;

import com.mcxgroup.business.req.DailyTrainTicketQueryReq;
import com.mcxgroup.business.req.DailyTrainTicketSaveReq;
import com.mcxgroup.business.resp.DailyTrainTicketQueryResp;
import com.mcxgroup.business.service.DailyTrainTicketService;
import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {
    @Resource
    private DailyTrainTicketService dailyTrainTicketService;


    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryResps(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }

}
