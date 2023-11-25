package com.mcxgroup.business.controller;

import com.mcxgroup.business.req.ConfirmOrderDoReq;
import com.mcxgroup.business.req.ConfirmOrderQueryReq;
import com.mcxgroup.business.resp.ConfirmOrderQueryResp;
import com.mcxgroup.business.service.BeforeConfirmOrderService;
import com.mcxgroup.business.service.ConfirmOrderService;
import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
* @author MCXEN
* @project Train
*/
@Slf4j
@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {
    @Resource
    private BeforeConfirmOrderService beforeConfirmOrderService;

    @Resource
    private ConfirmOrderService confirmOrderService;
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {
        log.info("doConfirm处遇到的ConfirmOrderDoReq MemberId为：「{}」/ ",req.getMemberId());
        Long id = beforeConfirmOrderService.beforeDoConfirm(req);
        return new CommonResp<>(String.valueOf(id));//返回接口ID
    }
    @GetMapping("/query-line-count/{id}")
    public CommonResp<Integer> queryLineCount(@PathVariable Long id) {
        int count = confirmOrderService.queryLineCount(id);
        return new CommonResp<>(count);//返回接口ID
    }
}
