package com.mcxgroup.common.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginMemberContext {
    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberContext.class);

    private static ThreadLocal<MemberLoginRespDto> member = new ThreadLocal<>();

    public static MemberLoginRespDto getMember() {
        return member.get();
    }

    public static void setMember(MemberLoginRespDto member) {
        LoginMemberContext.member.set(member);
    }

    public static Long getId() {
        try {
            return member.get().getId();
        } catch (Exception e) {
            LOG.error("获取登录会员信息异常", e);
            throw e;
        }
    }

}
