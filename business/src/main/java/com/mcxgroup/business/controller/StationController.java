package com.mcxgroup.business.controller;

import com.mcxgroup.business.req.StationQueryReq;
import com.mcxgroup.business.req.StationSaveReq;
import com.mcxgroup.business.resp.StationQueryResp;
import com.mcxgroup.business.service.StationService;
import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组件的后端的COntroller
 */
@RestController
@RequestMapping("/station")
public class StationController {
    @Resource
    private StationService stationService;
    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll() {
        List<StationQueryResp> queryAll = stationService.queryAll();
        return new CommonResp<>(queryAll);
    }
}
