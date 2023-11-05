package com.mcxgroup.member.controller;

import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.member.req.PassengerQueryReq;
import com.mcxgroup.member.req.PassengerSaveReq;
import com.mcxgroup.member.resp.PassengerQueryResp;
import com.mcxgroup.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {
    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody PassengerSaveReq req) {
        passengerService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<PassengerQueryResp>> queryResps(@Valid PassengerQueryReq req) {
        PageResp<PassengerQueryResp> list = passengerService.queryResp(req);
        return new CommonResp<>(list);
    }
    @GetMapping("/query-mine")
    public CommonResp<List<PassengerQueryResp>> queryMine() {
        List<PassengerQueryResp> queryResps = passengerService.queryMine();
        return new CommonResp<>(queryResps);
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return new CommonResp<>();
    }

}
