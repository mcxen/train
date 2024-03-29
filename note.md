# Getting Started

参考Git路线：

https://github.com/JohnConstantion/train/commits/master



## 子模块设置

新建module之后 Idea就会自动加一个Module加一个packaging

![image-20230922160435713](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230922160435713.png)

## Log

### 2023年9月22日 

- 修改git为git而不是http;

```shell
PS F:\项目\train> git remote remove main 
PS F:\项目\train> git remote -v         
PS F:\项目\train> git remote add origin git@github.com:mcxen/train.git
PS F:\项目\train> git remote -v                                       
origin  git@github.com:mcxen/train.git (fetch)
origin  git@github.com:mcxen/train.git (push)
PS F:\项目\train> git show
```

- 直接删掉了父的src文件目录



- Slf4j

Lombok的注解可以帮助你自动生成Logger字段，从而轻松使用SLF4J来记录日志。通过在类上添加`@Slf4j`注解，Lombok将自动生成一个名为`log`的Logger实例，然后你可以使用SLF4J API通过`log`实例来记录日志消息。这样结合使用Lombok和SLF4J可以更方便地进行日志记录，并减少了手动创建Logger实例的工作。



- AOP拦截，打印日志

导入LogAspect.java 

<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230922205243901.png" alt="image-20230922205243901" style="zoom:67%;" />



测试结果：

![image-20230922205800565](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230922205800565.png)



### 2023年9月23日

- 数据库为：

<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230923104934905.png" alt="image-20230923104934905" style="zoom: 67%;" />



- 超级大坑，mybatis 2.x版本不支持springboot 3，服了



- spring 无法import service 解决办法。删掉srevice，重新导入就可以了



### 2023年9月24日

- 如果报 404，有可能是识别不到controller，就比如我没有配置
- ![image-20230924111406012](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230924111406012.png)



- 加入统一的返回
- ![image-20230924112228161](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230924112228161.png)



- 雪花算法
- ![image-20230924132946895](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230924132946895.png)

![SnowFlake 雪花算法生成分布式ID](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/50ba2570-e86c-11ea-8115-8d7d715b7847)

数据中心和工作机器



- 安装node js

下载官网的node js，配置环境变量，修改registry

```shell
C:\Users\mcxen>node -v
v18.18.0

C:\Users\mcxen>npm -v
9.8.1

C:\Users\mcxen>npm config get registry
https://registry.npmjs.org/

C:\Users\mcxen>npm config set registry https://registry.npm.taobao.org

npm install -g @vue/cli@5.0.8
npm install -g @vue/cli

```



- 重启Idea J之后才会生效
- ![image-20230924135342202](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230924135342202.png)



- vue这个vue文件又可以当作是组件又可以当作是views
- ![image-20230924164843451](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230924164843451.png)
- 这个放在哪里都可以，vue主要是三个部分，tmplate和script





### 2023年9月27日

增加Axios开发的这句话

![image-20230927204222157](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230927204222157.png)

系统会自动带上这个server

![image-20230927204204988](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230927204204988.png)



### 2023年9月29日

- 加入头部的导航栏，分区分块vue设计
- 在macos打开，得注意用系统的terminal去安装vue cli和在外面npm Install就可以
- 下面的git，因为使用了代理，所以显示报错，需要修改
- 
- <img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-09-29%2010.40.38.png" alt="截屏2023-09-29 10.40.38" style="zoom: 50%;" />
- 使用JWT完成单点登录，
- <img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230929111133502.png" alt="image-20230929111133502" style="zoom:50%;" />、



### 2023年10月2号

- 解决全屏覆盖导致程序崩溃

- ![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/bb93b75fb6884e6e8d82e2a2a1c0bfc5.png)

- 在vue.config.js加入devServer那个代码

- ```js
  const { defineConfig } = require('@vue/cli-service')
  module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
      client: {
        overlay: false
      }
    }
  })
  ```



- 实现了成员信息插入
- ![截屏2023-10-02 20.23.18](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-10-02%2020.23.18.png)
- 来来的，那个http测试，必须要空一行才可以。注意：接口8000的话会被没有token拦截，可以测试8080



- 前置拦截器

- ```java
  @Slf4j
  public class MemberIntercepto implements HandlerInterceptor {
      //前置拦截器，获取header的token，然后保存到LocalThread.
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
          String token = request.getHeader("token");
          if (StrUtil.isNotBlank(token)){
              log.info("拦截器获取登录的token：{}",token);
              JSONObject memberJson = JwtUtil.getJSONObject(token);
              log.info("拦截器获取登录的JSONObject：{}",memberJson);
              LoginMemberContext.setMember(JSONUtil.toBean(memberJson, MemberLoginRespDto.class));
          }
          return true;
      }
  }
  ```

- ```java
  //LoginMemberContext.java
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
  ```

- 

### 2023年10月8号

- 解决id序列化
- <img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E5%BA%8F%E5%88%97%E5%8C%96.gif" alt="序列化" style="zoom: 67%;" />
- 



### 2023年10月11日

- FTL实现实体类模版

- ```java
  <#list typeSet as type>
  <#if type=='Date'>
  import java.util.Date;
  import com.fasterxml.jackson.annotation.JsonFormat;
  </#if>
  <#if type=='BigDecimal'>
  import java.math.BigDecimal;
  </#if>
  </#list>
  ```

- 



### 2023年10月21日

- 这里必须一块生成这个toJson到Enum。js不然这个js里面就一个Array
- ![截屏2023-10-21 18.36.41](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-10-21%2018.36.41.png)

自动获取火车车次：vue代码：



![截屏2023-10-21 21.39.10](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-10-21%2021.39.10.png)

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20231021214033780.png" alt="image-20231021214033780" style="zoom:50%;" />





- 实现自动生成
- ![image-20231024160914597](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20231024160914597.png)



- 支持定时任务

![截屏2023-10-24 17.03.59](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-10-24%2017.03.59.png)



### 2023年11月16日

#### 缓存

- mybatis缓存

需要实现序列化，对应的Train实体，这样才会开启二级缓存

![image-20231116102223605](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20231116102223605.png)



- mybatis是对持久层添加缓存，也可以springboot对api接口添加缓存



#### sentinel限流

走https://ghproxy.com/ 下载 sentinel release

下载jar包

```sh
java -Dserver.port=18080 -Dcsp.sentinel.dashboard.server=localhost:18080 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.6.jar
```



#### Nacos搭配sentinel 实现本地保存配置

添加依赖

```xml
<!--        增加sentinel和nacos的一起的配置-->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>
```

设置参数

```properties
# sentinel和nacos
spring.cloud.sentinel.datasource.nacos.nacos.server-addr=localhost:8848
spring.cloud.sentinel.datasource.nacos.nacos.namespace=train
spring.cloud.sentinel.datasource.nacos.nacos.group-id=TRAIN_GROUP
spring.cloud.sentinel.datasource.nacos.nacos.data-id=sentinel
spring.cloud.sentinel.datasource.nacos.nacos.rule-type=flow

```



![截屏2023-11-22 11.10.42](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-11-22%2011.10.42.png)





```json
[{
    "resource":"doConfirm",
    "limitApp":"default",
    "grade":1,
    "count":100,
    "strategy":0,
    "controlBehavior":0,
    "clusterMode":false
},{
    "resource":"confirmOrderDo",
    "limitApp":"default",
    "grade":1,
    "count":4,
    "strategy":0,
    "controlBehavior":0,
    "clusterMode":false
}]

```

上面的配置就是对应：

> ```sh
>     "resource":"doConfirm",
>     "limitApp":"default", 针对来源
>     "grade":1, 0和1<>阈值类型，QPS还是线程数
>     "count":100,阈值
>     "strategy":0,流控模式
>     "controlBehavior":0,流控效果
>     "clusterMode":false
> ```

![image-20231122111246153](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20231122111246153.png)



效果：

![截屏2023-11-22 11.15.54](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-11-22%2011.15.54.png)



## BUG集合：

### 无法创建Bean - 原因往往在最后一个Caused by

```sh
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'com.mcxgroup.batch.feign.BusinessFeign': FactoryBean threw exception on object creation
	at org.springframework.beans.factory.support.FactoryBeanRegistrySupport.doGetObjectFromFactoryBean(FactoryBeanRegistrySupport.java:154)
```



![截屏2023-11-02 18.56.28](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-11-02%2018.56.28.png)



我滴Fuck

代码正常运行时，重启后报Error creating bean with name错误，一直找不到原因；查看网上信息说是注解扫描问题， 并没有修改代码。

后来发现一句话：造成该报错无非这几个原因：**扫描不到包、导包导错、注解没加或加错，类型、类名不正确**等；

**排查：查看报错所有日志的Caused by，原因往往在最后一个Caused by。**

重点是上面加粗字，排查后发现错误为Feign PathVariable annotation was empty on param 0,


原因是在使用FeignClient时，对@PathVariable没有给出默认名字
应该是：

```java
@PathVariable("name") String s
```

但是没有给出默认name：

```java
@PathVariable() String s
```

解决方式：开启参数保留
idea为例
Settings->Java Compiler
在Additonal command line parameters添加-parameters



![截屏2023-11-02 19.07.48](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-11-02%2019.07.48.png)



### 无法解析创建cust的mapper



```sh
eans.BeanInstantiationException: Failed to instantiate [org.apache.ibatis.session.SqlSessionFactory]: Factory method 'sqlSessionFactory' threw exception with message: Failed to parse mapping resource: 'file [/Users/mcxw/Downloads/train/business/target/classes/mapper/cust/SkTokenMapperCust.xml]'
	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:171)
	at org.springframework.beans.factory.support.ConstructorResolver.instantiate(ConstructorResolver.java:648)
	... 61 common frames omitted
Caused by: java.io.IOException: Failed to parse mapping resource: 'file [/Users/mcxw/Downloads/train/business/target/classes/mapper/cust/SkTokenMapperCust.xml]'
	at org.mybatis.spring.SqlSessionFactoryBean.buildSqlSessionFactory(SqlSessionFactoryBean.java:615)
	at org.mybatis.spring.SqlSessionFactoryBean.afterPropertiesSet(SqlSessionFactoryBean.java:492)
	at org.mybatis.spring.SqlSessionFactoryBean.getObject(SqlSessionFactoryBean.java:635)
```



![截屏2023-11-23 11.30.48](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-11-23%2011.30.48.png)

实际就是没有配置传进来的参数，

得导入：`@Param("XX")`

```java
public interface SkTokenMapperCust {
    int decrease(@Param("date")Date date, @Param("trainCode")String trainCode);
}
```





### 操作Redis，报：ERR value is not an integer or out of range

主要是序列化问题引起的，如果之前使用的是RedisTemplate，改成StringRedis Template



<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20231123214645827.png" alt="image-20231123214645827" style="zoom: 50%;" />



### 在springboot 3整合 RocketMQ可能遇见的BUG

> 实现RocketMQ发送，spring.factories功能在Spring Boot 3.0被移除，替代方案为
>
> META- INFO/spring/org.springframework. boot.autoconfigure. AutoConfiguration.imports

![image-20231124155959153](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20231124155959153.png)
