package com.mcxgroup.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.resp.MemberLoginResp;
import com.mcxgroup.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Component
public class MemberInterceptor implements HandlerInterceptor {
    //前置拦截器，获取header的token，然后保存到LocalThread.
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("MemberInterceptor is working ... ");
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)){
            log.info("拦截器获取登录的token：{}",token);
            System.out.println("token = " + token);
            JSONObject memberJson = JwtUtil.getJSONObject(token);
            log.info("拦截器获取登录的JSONObject：{}",memberJson);
            LoginMemberContext.setMember(JSONUtil.toBean(memberJson, MemberLoginResp.class));
        }
        log.info("MemberInterceptor closed .");
        return true;
    }
}
