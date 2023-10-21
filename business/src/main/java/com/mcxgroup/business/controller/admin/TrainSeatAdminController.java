package com.mcxgroup.business.controller.admin;

import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.business.req.TrainSeatQueryReq;
import com.mcxgroup.business.req.TrainSeatSaveReq;
import com.mcxgroup.business.resp.TrainSeatQueryResp;
import com.mcxgroup.business.service.TrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train-seat")
public class TrainSeatAdminController {
    @Resource
    private TrainSeatService trainSeatService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSeatSaveReq req) {
        trainSeatService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainSeatQueryResp>> queryResps(@Valid TrainSeatQueryReq req) {
        PageResp<TrainSeatQueryResp> list = trainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainSeatService.delete(id);
        return new CommonResp<>();
    }
}
