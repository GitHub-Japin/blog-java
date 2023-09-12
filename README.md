<p align="center"></p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">基于SpringBoot+Vue的多人Blog设计</h1>
<h4 align="center">此项目基于SpringBoot + Apache Shiro + jwt + mybatis/Mybatis Plus + redis + mysql + EasyExcel</h4>
<h3>本项目用于个人学习用途，仍不完善</h3>

V1.0未完成功能：分类功能、评论点赞功能
V1.1完成功能：分类功能

v1.3相比于v1.0增加了Swagger文档、定时邮件、Echarts图标、统计功能等、
使用了shardingjdbc实现读写分离、分库不分表读写分离、分库分表读写分离
修复了部分bug（由于druid版本缘故，将localdatetime改为date，修复了分类的bug）

v1.4代码迁移与优化，新增了日志管理等,修改了Date类型时间比数据库少8小时的bug
-v1.4.4 修改了用户登录功能，添加了带盐值md5加密登录，并且将返回前端的密码与salt隐藏、修复了精度丢失bug
-vue v1.4.4修改了ElementUI弹窗存活时间、设置可关闭等用户友好功能，修复了部分bug

v1.5 修改了登录页面、使用设计模式完成登录接口、部分代码重构优化、配置文件优化

| 项目结构   |                                         |
| :--- | ---- |
| annotation     | 自定义注解            |
| common     | 公共代码，异常处理、结果类等            |
| config     | 配置类、mybatisplus、redis、shiro、Swagger配置、定时任务配置等 |
| constants     | 结果常量 |
| controller | 控制层代码                              |
| dao        | 存放Mapper接口                          |
| interceptor        | 拦截器                          |
| jwt        | jwt相关代码，过滤器、token生成等        |
| model       | 实体类pojo、dto、vo                               |
| service    | 服务层代码                              |
| shiro      |shiro授权认证|
| strategy      |设计模式（工厂+策略）|



| 已实现功能：                  | 备注                              |
| :---------------------------- | --------------------------------- |
| 管理端                     |                |
| 用户管理                      | 增删查改、excel导出               |
| 分类管理                      | 增删查改                          |
| 博客管理                      | 增删查改、预览、redis缓存分页结果 |
| 统计管理                      | 使用Echarts进行分类下博客数量统计、统计用户发博客数量等 |
| 日志管理                      | aop生成、删除                                  |
| 登录注册                      |                                   |
| shiro与jwt联合控制token失效等 |                                   |
| Swagger文档 |                                   |
| :----------------- |:---------------------------------------
| 用户端                     |                |
| 博客展示                     |                |
| 博客评论                     |                |


| 开发配置       |                   |
| :------------- | ----------------- |
| JDK            | 1.8.0_291         |
| 数据库连接工具 | Navicat Premium15 |
| Mysql          | 5.7.36            |
| redis          | 3.2.100           |
| Maven          | 3.6.3             |
|                |                   |

## 效果展示
于assert文件夹
