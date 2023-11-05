package com.mcxgroup.business.controller;

import com.mcxgroup.business.req.TrainQueryReq;
import com.mcxgroup.business.req.TrainSaveReq;
import com.mcxgroup.business.resp.TrainQueryResp;
import com.mcxgroup.business.service.TrainSeatService;
import com.mcxgroup.business.service.TrainService;
import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/train")
public class TrainController {
    @Resource
    private TrainService trainService;

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryAll() {
        List<TrainQueryResp> queryAll = trainService.queryAll();
        return new CommonResp<>(queryAll);
    }

}
