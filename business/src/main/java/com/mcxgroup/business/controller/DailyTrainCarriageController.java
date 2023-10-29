package com.mcxgroup.business.controller;

import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.business.req.DailyTrainCarriageQueryReq;
import com.mcxgroup.business.req.DailyTrainCarriageSaveReq;
import com.mcxgroup.business.resp.DailyTrainCarriageQueryResp;
import com.mcxgroup.business.service.DailyTrainCarriageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/daily-train-carriage")
public class DailyTrainCarriageController {
    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainCarriageSaveReq req) {
        dailyTrainCarriageService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainCarriageQueryResp>> queryResps(@Valid DailyTrainCarriageQueryReq req) {
        PageResp<DailyTrainCarriageQueryResp> list = dailyTrainCarriageService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainCarriageService.delete(id);
        return new CommonResp<>();
    }
}
