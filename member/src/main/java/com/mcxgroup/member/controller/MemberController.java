package com.mcxgroup.member.controller;
import com.mcxgroup.common.resp.CommonResp;
import com.mcxgroup.member.req.MemberLoginReq;
import com.mcxgroup.member.req.MemberRegisterReq;
import com.mcxgroup.member.req.MemberSendCodeReq;
import com.mcxgroup.member.resp.MemberLoginResp;
import com.mcxgroup.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Resource
    MemberService memberService;

    @GetMapping("/count")
    public CommonResp<Integer> count(){
        int count = memberService.count();
        CommonResp<Integer> commonResp = new CommonResp<>();
        commonResp.setContent(count);
        return commonResp;
    }

    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq dto){
        Long register = memberService.register(dto);
//        CommonResp<Long> commonResp = new CommonResp<>();
//        commonResp.setContent(register);
//        return commonResp;
        return new CommonResp<>(register);
    }

    @PostMapping("/send-code")
//    requestBody就是改成了用Json传递，不是用的Form表单传递
    public CommonResp<Long> sendCode(@Valid @RequestBody MemberSendCodeReq dto){
        memberService.sendCode(dto);
        return new CommonResp<>();
    }
    @PostMapping("/login")
    public CommonResp<MemberLoginResp> login(@Valid @RequestBody MemberLoginReq dto){
        MemberLoginResp login = memberService.login(dto);
        return new CommonResp<>(login);
    }
}
