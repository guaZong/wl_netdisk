# 未来网盘
## 目前正在开发中.....
- 未来网盘后端代码仓库,前端仓库地址: 暂未公开

- 本网盘适用人群:私人合作团队,家庭版网盘,搭建网盘服务器

## 项目介绍

- 技术栈:
  - 框架: SpringCloud、SpringBoot 、SpringMVC 、SpringSecurity 、Mybatis 、Mybatis-plus、Netty、xxl-job
  - 数据库: Mysql 、Redis
  - 消息队列: RabbitMQ
  - 搜索引擎: ElasticSearch
  - 存储: Minio
  - 数据同步: canal
  
- 项目配合使用其他工具
  - 接口工具: Apifox (如有兴趣查看该项目接口(只读)   guaZong 在 Apifox 中邀请你加入团队 未来网盘 https://app.apifox.com/invite?token=lYd_EHT66K6nCTueYdM4D)
  - 部署工具: nginx、docker


- 项目用途
  - 项目主要用作小团队网盘使用或者用作家庭版网盘,对于大规模用户使用的话后续需要自购存储服务或者搭建存储服务器
  - 该项目对于家庭版网盘或者小团队网盘项目部署使用来说,特点就是私密,高效,管理员可以开关注册功能,保证团队成员人数和文件保密性,系统包含IM聊天系统,团队内用户可在内部进行资料共享和交流
## 安装和使用 提供项目的安装和使用指南，包括依赖项、环境配置、命令示例等。

### springboot项目安装使用

- 编写application.yml,将mysql,redis,rabbitmq,elasticsearch等信息填写完毕,随后填写阿里云相关信息
- shell脚本部署 ,环境配置
- 存储方式
  - 单机服务存储,简单方便
  - Minio存储,高可用,存储大文件
- 
  
## 功能说明

### 文件模块

### 聊天模块

### 用户模块
