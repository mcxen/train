package com.mcxgroup.member.controller;
import com.mcxgroup.common.dto.CommonResp;
import com.mcxgroup.member.dto.MemberLoginDto;
import com.mcxgroup.member.dto.MemberLoginRespDto;
import com.mcxgroup.member.dto.MemberRegisterDto;
import com.mcxgroup.member.dto.MemberSendCodeDto;
import com.mcxgroup.member.service.MemberService;
import jakarta.annotation.Resource;
//import com.mcxgroup.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CommonResp<Long> register(@Valid MemberRegisterDto dto){
        Long register = memberService.register(dto);
//        CommonResp<Long> commonResp = new CommonResp<>();
//        commonResp.setContent(register);
//        return commonResp;
        return new CommonResp<>(register);
    }

    @PostMapping("/send-code")
//    requestBody就是改成了用Json传递，不是用的Form表单传递
    public CommonResp<Long> sendCode(@Valid @RequestBody MemberSendCodeDto dto){
        memberService.sendCode(dto);
        return new CommonResp<>();
    }
    @PostMapping("/login")
    public CommonResp<MemberLoginRespDto> login(@Valid @RequestBody MemberLoginDto dto){
        MemberLoginRespDto login = memberService.login(dto);
        return new CommonResp<>(login);
    }
}