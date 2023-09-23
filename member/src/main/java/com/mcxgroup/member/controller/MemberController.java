package com.mcxgroup.member.controller;
import com.mcxgroup.member.dto.MemberRegisterDto;
import com.mcxgroup.member.service.MemberService;
import jakarta.annotation.Resource;
//import com.mcxgroup.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Resource
    MemberService memberService;

    @GetMapping("/count")
    public int count(){
        return memberService.count();
    }

    @PostMapping("/register")
    public Long register(MemberRegisterDto dto){
        return memberService.register(dto);
    }
}
