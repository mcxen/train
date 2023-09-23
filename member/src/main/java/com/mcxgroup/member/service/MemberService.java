package com.mcxgroup.member.service;

import com.mcxgroup.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    @Resource
    private MemberMapper memberMapper;
    public int count(){
        return memberMapper.count();
    }
}
