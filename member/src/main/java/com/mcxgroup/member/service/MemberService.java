package com.mcxgroup.member.service;

import cn.hutool.core.collection.CollUtil;
import com.mcxgroup.common.exception.BusinessException;
import com.mcxgroup.common.exception.BusinessExceptionEnum;
import com.mcxgroup.member.domain.Member;
import com.mcxgroup.member.domain.MemberExample;
import com.mcxgroup.member.dto.MemberRegisterDto;
import com.mcxgroup.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MemberService {
    @Resource
    private MemberMapper memberMapper;
    public int count(){
        return Math.toIntExact(memberMapper.countByExample(null));
        //返回的是lOng转成int
    }
    public Long register(MemberRegisterDto dto){
        String mobile = dto.getMobile();
        MemberExample example = new MemberExample();
        example.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(example);

        if (CollUtil.isNotEmpty(members)){
//            return members.get(0).getId();
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);

        memberMapper.insert(member);
        return member.getId();
    }
}
