# Train Project

2023年9月22日开始施工💡


## 用途

此处的pom和main不放具体代码，是作为大项目的位置，主要负责管理各个模块的框架版本。



## 接口Api示例

### passenger信息保存 /member/passenger/save

- 实现了成员信息插入
- ![截屏2023-10-02 20.23.18](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-10-02%2020.23.18.png)
- IdeaJ的http测试，必须要空一行才可以。
- **注意：**接口8000的话会被没有token拦截，可以测试8080

### passenger信息查询接口 /member/passenger/query-list



![截屏2023-10-05 22.14.56](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-10-05%2022.14.56.png)



Mapper生成器：



![image-20231021172941466](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20231021172941466.png)



### Enum.js生成：

![image-20231021173159353](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20231021173159353.png)





### 支持Quartz的Job的接口



![截屏2023-10-24 20.38.21](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-10-24%2020.38.21.png)

## 功能

### 基于令牌的快速库存判断

令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存快

![截屏2023-11-23 15.06.15](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-11-23%2015.06.15.png)

