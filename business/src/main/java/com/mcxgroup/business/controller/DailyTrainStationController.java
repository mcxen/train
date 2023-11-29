package com.mcxgroup.business.controller;

import com.mcxgroup.business.req.DailyTrainStationQueryAllReq;
import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.business.resp.DailyTrainStationQueryResp;
import com.mcxgroup.business.service.DailyTrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/daily-train-station")
public class DailyTrainStationController {
    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @GetMapping("/query-by-train-code")
    public CommonResp<List<DailyTrainStationQueryResp>> queryByTrain(@Valid DailyTrainStationQueryAllReq req) {
        List<DailyTrainStationQueryResp> list = dailyTrainStationService.queryByTrain(req.getDate(), req.getTrainCode());
        return new CommonResp<>(list);
    }

}
