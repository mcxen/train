package com.mcxgroup.${module}.controller.admin;

import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.${module}.req.${Domain}QueryReq;
import com.mcxgroup.${module}.req.${Domain}SaveReq;
import com.mcxgroup.${module}.resp.${Domain}QueryResp;
import com.mcxgroup.${module}.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @author MCXEN
* @project Train
*/

@RestController
@RequestMapping("/admin/${do_main}")
public class ${Domain}AdminController {
    @Resource
    private ${Domain}Service ${domain}Service;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ${Domain}SaveReq req) {
        ${domain}Service.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> queryResps(@Valid ${Domain}QueryReq req) {
        PageResp<${Domain}QueryResp> list = ${domain}Service.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ${domain}Service.delete(id);
        return new CommonResp<>();
    }
}
