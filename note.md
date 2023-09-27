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