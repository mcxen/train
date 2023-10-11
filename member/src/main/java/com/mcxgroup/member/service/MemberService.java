package com.mcxgroup.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mcxgroup.common.exception.BusinessException;
import com.mcxgroup.common.exception.BusinessExceptionEnum;
import com.mcxgroup.common.util.JwtUtil;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.member.domain.Member;
import com.mcxgroup.member.domain.MemberExample;
import com.mcxgroup.member.mapper.MemberMapper;
import com.mcxgroup.member.req.MemberLoginReq;
import com.mcxgroup.member.req.MemberRegisterReq;
import com.mcxgroup.member.req.MemberSendCodeReq;
import com.mcxgroup.member.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MemberService {
    @Resource
    private MemberMapper memberMapper;
    public int count(){
        return Math.toIntExact(memberMapper.countByExample(null));
        //返回的是lOng转成int
    }
    public Long register(MemberRegisterReq dto){
        String mobile = dto.getMobile();
        Member members = selectByMobile(mobile);

        if (ObjectUtil.isNotNull(members)){
//            return members.get(0).getId();
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        //导入雪花算法
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);

        memberMapper.insert(member);
        return member.getId();
    }
    public void sendCode(MemberSendCodeReq dto){
        String mobile = dto.getMobile();
        Member members = selectByMobile(mobile);

        if (ObjectUtil.isNull(members)){
            log.info("该手机号未注册");
            //如果不存在的就插入新的用户
            Member member = new Member();
            //导入雪花算法
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);

        }else {
            log.info("手机号存在");
        }
        //生产验证码
//        String code = RandomUtil.randomString(4);
        String code = "8888";
        log.info("生成手机验证码：{}",code);
        //保存短信记录 手机号，验证码，有效期 5分钟 是否已使用，业务类型，发送时间，使用时间

        //对接

    }
    public MemberLoginResp login(MemberLoginReq dto){
        String mobile = dto.getMobile();
        Member memberByMobile = selectByMobile(mobile);

        if (ObjectUtil.isNull(memberByMobile)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }
        //校验短信验证码
        if (!"8888".equals(dto.getCode())){
            //抛出验证码错误
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }
        MemberLoginResp loginRespDto = BeanUtil.copyProperties(memberByMobile, MemberLoginResp.class);
        loginRespDto.setToken(JwtUtil.createToken(loginRespDto.getId(), loginRespDto.getMobile()));
        return loginRespDto;
    }

    private Member selectByMobile(String mobile) {
        MemberExample example = new MemberExample();
        example.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(example);
        if (CollUtil.isEmpty(members)){
            return null;
        }else {
            return members.get(0);
        }
    }
}
