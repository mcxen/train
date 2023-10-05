package com.mcxgroup.member.controller;


import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.dto.CommonResp;
import com.mcxgroup.member.dto.PassengerQueryDto;
import com.mcxgroup.member.dto.PassengerRespDto;
import com.mcxgroup.member.dto.PassengerSaveReqDto;
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
    public CommonResp<Object> save(@Valid @RequestBody PassengerSaveReqDto req) {
        passengerService.save(req);
        return new CommonResp<>();
    }


//    查询的用Get请求，更新用Post
    @GetMapping("/query-list")
    public CommonResp<List<PassengerRespDto>> queryList(@Valid PassengerQueryDto req) {
        req.setMemberId(LoginMemberContext.getId());
        List<PassengerRespDto> list = passengerService.queryList(req);
        return new CommonResp<>(list);
    }
 /*
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query-mine")
    public CommonResp<List<PassengerQueryResp>> queryMine() {
        List<PassengerQueryResp> list = passengerService.queryMine();
        return new CommonResp<>(list);
    }


    @GetMapping("/init")
    public CommonResp<Object> init() {
        passengerService.init();
        return new CommonResp<>();
    }

  */
}
