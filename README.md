<p align="center">
    <img src="https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/12/easyink%20logo.png" />
</p>
<p align="center">
  <img src="https://img.shields.io/badge/edition-V1.18.2-brightgreen" />
  <img src="https://img.shields.io/badge/license-GPL%203.0-yellow" />
  <img src="https://img.shields.io/badge/mysql-5.7%2B-orange" />
  <img src="https://img.shields.io/badge/jdk-1.8.301(64bit)%2B-red" />
</p>
<h4 align="center">
 <a href="http://www.lianluoyi.cn" target="_blank">官网</a> |
 <a href="http://www.easyink.net" target="_blank">体验</a> |
 <a href="https://gitee.com/lianluoyi/easyink-install/wikis/%E7%94%A8%E6%88%B7%E6%8C%87%E5%8D%97" target="_blank">安装</a> 
</h4>

<h4 align="center">如果我们的项目对您有帮助的话，帮忙动动金手指在右上角点个🌟star，您的支持是我们开源团队的最大动力💪。</p>
<hr />

## :rocket: 做最简单易用的系统

`easyink` 是基于企业微信生态的一站式私域流量运营平台。包含**客户引流、客户互动、托管后台、会话存档审计**这几个主要功能模块。

为企微用户提供更便捷的企微后台管理功能，提供更全面、更规范的运营服务能力，深化企业与私域客户的互动，提高客户转化率。

## :taxi: 快速体验

- 建议使用谷歌浏览器访问

- [多租户体验入口](http://www.easyink.net)

- 该环境采用第三方应用+代开发应用模式，可借助[第三方应用操作手册](https://www.yuque.com/docs/share/591b5dff-f705-413e-b167-e8ef72d519bf?#O35E2)辅助体验

- [单租户体验入口](http://119.91.63.136:8091)   账号密码：admin/easyink2021

- 该环境采用自建应用模式，可借助[自建应用操作手册](https://www.yuque.com/docs/share/9217b462-a4c2-4d4a-97cb-48eebf800784?#hsf4v)辅助体验

## :factory: 关联项目

| 项目名              | 项目说明                                                | 项目地址 |
| ------------------- | ------------------------------------------------------- | -------- |
| **easyink_System(当前项目)**    | **EasyInk后端服务**                                       | **https://github.com/lianluoyi/easyink_System** |
| easyink_Dashboard | easyink前端服务，主要负责后台UI界面展示               | https://github.com/lianluoyi/easyink_Dashboard |
| easyink_Sidebar   | easyink前端服务，主要负责企微客户端中的侧边栏界面展示 | https://github.com/lianluoyi/easyink_Sidebar |


## :star2: 功能特性


- [x] 账号体系打通企业微信
- [x] 简洁美观的页面
- [x] 自定义 UI 主题
- [x] 支持多租户和单租户
- [x] 快速部署和使用
- [x] 完整的接口、组件文档
- [x] 简单易用的框架



> 了解有关 easyink 功能的更多信息，请访问  [「自建应用操作手册」](https://www.yuque.com/docs/share/9217b462-a4c2-4d4a-97cb-48eebf800784?#hsf4v)、[「三方应用操作手册」](https://www.yuque.com/docs/share/591b5dff-f705-413e-b167-e8ef72d519bf?#O35E2)

## :book: 功能清单

[![easyink](https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/22/功能清单.jpeg)]()

## :page_with_curl: 项目演示图

[![easyink](https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/22/首页统计.jpeg)]()

[![easyink](https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/22/运营中心-客户群活码.jpeg)]()

[![easyink](https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/22/客户中心.jpeg)]()

[![easyink](https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/22/应用管理.jpeg)]()

[![easyink](https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/22/企业风控.jpeg)]()

[![easyink](https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/22/系统设置.jpeg)]()

[![easyink](https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/22/系统监控.jpeg)]()

## :factory: 业务架构
[![easyink](https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/26/%E4%B8%9A%E5%8A%A1%E6%9E%B6%E6%9E%84%E5%9B%BE.png)]()

## :house: 项目结构

- **后端**

```
com.easyink     
├── easyink-common      // 全局配置、工具模块
├── easyink-framework   // 系统框架模块
├── easyink-quartz      // 定时任务模块
├── easyink-admin       // 后台管理接口
├── easyink-wecom       //  企微业务模块
│       └── client        // 请求客户端
│       └── domain        // 实体类 
│       └── fatory        // 工厂类
│       └── interceptor   // 请求拦截器
│       └── listener      // 事件监听
│       └── login         // 登录
│       └── service       // 业务接口
│       └── strategy      // 回调处理策略
```

- **前端**

详细项目结构见 [**easyink-dashboard**](https://github.com/lianluoyi/easyink_Dashboard) 和 [**easyink-sidebar**](https://github.com/lianluoyi/easyink_Sidebar) 的readme文件


## :bulb: 系统依赖

- **后端**

| 环境依赖            | 版本            |
| ------------------- | --------------- |
| JDK                 | `1.8.301(64bit)+` |
| Mysql               | `5.7+`             |
| Redis               | `4.0.6+`          |
| Maven               | `3.5.2+`          |
| ElasticSearch(可选) |   `7.10.1+`       |

  

- **前端**

| 环境依赖 | 版本 |
| -------- | ---- |
| node     |   `V14.18.1+`    |
| npm      |   `6.14.15+`   |


## :airplane: 技术栈

**前端**

| 技术栈名称 | 说明                                                         | 地址                                    |
| ---------- | ------------------------------------------------------------ | --------------------------------------- |
| es6        | 全称 ECMAScript 6.0 ，是 JavaScript 的下一个版本标准         | https://www.w3schools.com/js/js_es6.asp |
| vue        | 是一套用于构建用户界面的渐进式框架                           | https://github.com/vuejs                |
| vuex       | 是一个专为Vue.js 应用程序开发的状态管理模式+ 库              | https://vuex.vuejs.org                  |
| vue router | 是 [Vue.js](http://v3.vuejs.org/) 的官方路由                 | https://router.vuejs.org/zh/            |
| vue cli    | 是一个基于 Vue.js 进行快速开发的完整系统                     | https://cli.vuejs.org/zh/               |
| axios      | 是一个基于 promise 的 HTTP 库，可以用在浏览器和 node.js 中   | https://github.com/axios/axios          |
| element-ui | 是一套为开发者、设计师和产品经理准备的基于Vue 2.0 的桌面端组件库 | https://github.com/ElemeFE/element      |

**后端**

| 技术栈名称    | 说明                                                         | 地址                                           |
| ------------- | ------------------------------------------------------------ | ---------------------------------------------- |
| SpringBoot    | 主框架                                                       | http://spring.io/projects/spring-boot          |
| MyBatis       | 一款优秀的持久层框架，它支持定制化 SQL、存储过程以及高级映射 | http://www.mybatis.org/mybatis-3/zh/index.html |
| Druid         | 数据库连接池。Druid能够提供强大的监控和扩展功能              | https://github.com/alibaba/druid               |
| redis         | 是一个开源的使用ANSI C语言编写、支持网络、可基于内存亦可持久化的日志型、Key-Value数据库， | https://redis.io/                              |
| elasticSearch | 分布式的使用 REST 接口的搜索引擎                             | https://github.com/elastic/elasticsearch       |
| Jwt           | JSON Web Token                                               | https://jwt.io/                                |



## :checkered_flag: 开始入门

### 1. 本地服务器

从 github.net 克隆：

```
git clone https://github.com/lianluoyi/easyink_System.git
```

> 服务器默认运行在8090端口，访问localhost:8090查看。

初始化和拉取 UI 模块：

```
git clone https://github.com/lianluoyi/easyink_Dashboard.git
git clone https://github.com/lianluoyi/easyink_Sidebar.git
```

> 您需要使用 `Nginx` 来部署 UI 模块或使用开发软件来启动它。
> 项目启动命令见对应项目的readme文档，
`Nginx`部署步骤见 -> [前端部署文档](https://gitee.com/lianluoyi/easyink_Dashboard/wikis/%E5%89%8D%E7%AB%AF%E9%83%A8%E7%BD%B2%E6%96%87%E6%A1%A3?sort_id=5953163)

### 2. 在 K8s 上快速部署

除了本地服务器，我们还提供了快速部署的方法。

请查看: [How to deploy easyink on K8s cluster](https://gitee.com/lianluoyi/easyink-install/wikis/%E7%94%A8%E6%88%B7%E6%8C%87%E5%8D%97)



## :sunglasses: 鸣谢

- [「forest」](https://github.com/dromara/forest):A high-level and lightweight HTTP client framework for Java. it makes sending HTTP requests in Java easier.
- [「mybatis-plus」](https://github.com/baomidou/mybatis-plus):An powerful enhanced toolkit of MyBatis for simplify development
- [「Knife4j」](https://github.com/xiaoymin/swagger-bootstrap-ui):Swagger-bootstrap-ui is the Swagger front-end UI implementation, the purpose is to replace the Swagger default UI implementation Swagger-UI, make the document more friendly...
- [「hutool」](https://github.com/dromara/hutool):A set of tools that keep Java sweet.
- [「moment」](https://github.com/moment):Parse, validate, manipulate, and display dates in javascript.
- [「lodash」](https://github.com/lodash/lodash):A modern JavaScript utility library delivering modularity, performance, & extras.
- [「vue-styleguidist」](https://vue-styleguidist.github.io):Created from react styleguidist for Vue Components with a living style guide.



## :sparkling_heart: 联系我们

<img src=https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/06/01/%E6%8A%80%E6%9C%AF%E4%BA%A4%E6%B5%81%E7%BE%A4%E6%B4%BB%E7%A0%81.png width=200 height=200 />


## :pencil2: 贡献指南

请参阅[贡献指南](https://github.com/lianluoyi/easyink_System/blob/master/.github/CONTRIBUTING.md)

##  :triangular_ruler:行为规范

请参阅[行为规范](https://github.com/lianluoyi/easyink_System/blob/master/.github/CODE_OF_CONDUCT.md)。

## :pencil: 版权和许可

本项目遵循[GPL 3.0]()开源协议

