package com.mcxgroup.member.config;


import com.mcxgroup.common.interceptor.LogInterceptor;
import com.mcxgroup.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

   @Resource
   LogInterceptor logInterceptor;

   @Resource
   MemberInterceptor memberInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
       //开启Log登录之后的拦截器，解决打印的日志没有时间戳的问题
       registry.addInterceptor(logInterceptor);

       // 路径不要包含context-path
       registry.addInterceptor(memberInterceptor)
               .addPathPatterns("/**")
               .excludePathPatterns(
                       "/1",
                       "/member/member/send-code",
                       "/member/member/login"
               );
   }
}
