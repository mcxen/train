# Getting Started

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
