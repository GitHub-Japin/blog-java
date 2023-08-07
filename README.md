<p align="center"></p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">基于SpringBoot+Vue的多人Blog设计</h1>
<h4 align="center">此项目基于SpringBoot + Apache Shiro + jwt + mybatis/Mybatis Plus + redis + mysql + EasyExcel</h4>
<h3>本项目用于个人学习用途，仍不完善</h3>

V1.0未完成功能：分类功能、评论点赞功能
V1.1完成功能：分类功能

v1.3相比于v1.0增加了Swagger文档、定时邮件、Echarts图标、统计功能等、使用了shardingjdbc实现分库分表、读写分离等

| 项目结构   |                                         |
| :--- | ---- |
| common     | 公共代码，异常处理、结果类等            |
| config     | 配置类、mybatisplus、redis、shiro、Swagger配置等 |
| controller | 控制层代码                              |
| dao        | 存放Mapper接口                          |
| jwt        | jwt相关代码，过滤器、token生成等        |
| pojo       | 实体类                                  |
| service    | 服务层代码                              |
| shiro      |shiro授权认证|



| 已实现功能：                  | 备注                              |
| :---------------------------- | --------------------------------- |
| 用户管理                      | 增删查改、excel导出               |
| 分类管理                      | 增删查改                          |
| 博客管理                      | 增删查改、预览、redis缓存分页结果 |
| 统计管理                      | 使用Echarts进行分类下博客数量统计等 |
| 登录注册                      |                                   |
| shiro与jwt联合控制token失效等 |                                   |
| Swagger文档 |                                   |



| 开发配置       |                   |
| :------------- | ----------------- |
| JDK            | 1.8.0_291         |
| 数据库连接工具 | Navicat Premium15 |
| Mysql          | 5.7.36            |
| redis          | 3.2.100           |
| Maven          | 3.6.3             |
|                |                   |
