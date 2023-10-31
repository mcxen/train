package com.mcxgroup.batch.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName BusinessFeign
 * @Description
 * @Author McXen@2023/10/31
 **/
//下面这个business是spring.application.name=batch的batch这个，MoreOver 要注册之后才可以识别
//@FeignClient("business")
//下面是手动调用,
@FeignClient(name = "business",url = "http://127.0.0.1:8081/business")
public interface BusinessFeign {
    @GetMapping("/hello")
    String hello1();
}
