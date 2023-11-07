package com.mcxgroup.business.controller.admin;

import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.business.req.ConfirmOrderQueryReq;
import com.mcxgroup.business.req.ConfirmOrderSaveReq;
import com.mcxgroup.business.resp.ConfirmOrderQueryResp;
import com.mcxgroup.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @author MCXEN
* @project Train
*/

@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {
    @Resource
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderSaveReq req) {
        confirmOrderService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> queryResps(@Valid ConfirmOrderQueryReq req) {
        PageResp<ConfirmOrderQueryResp> list = confirmOrderService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        confirmOrderService.delete(id);
        return new CommonResp<>();
    }
}
