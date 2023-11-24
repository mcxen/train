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
import org.springframework.web.bind.annotation.*;

/**
* @author MCXEN
* @project Train
*/

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {
    @Resource
    private BeforeConfirmOrderService beforeConfirmOrderService;

    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {
        beforeConfirmOrderService.beforeDoConfirm(req);
        return new CommonResp<>();
    }
}
