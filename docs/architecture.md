# 系统架构文档

## 1. 项目概述
本项目为微服务架构，采用前后端分离：
- 后端：SpringCloud Alibaba 微服务生态
- 前端：Vue3 + TypeScript + reka-ui + ant-design-vue
- 部署：jar + Docker + K8s / 虚拟机
- 环境：dev / test / prod

## 2. 后端技术栈
- 核心框架：SpringBoot 3.x / SpringCloud 2025
- 服务治理：Nacos / OpenFeign / Sentinel
- 数据存储：MySQL + Redis + MyBatis-Flex
- 消息队列：RocketMQ
- 安全：sa-token
- 工具包：Hutool、Jackson、guava

## 3. 后端模块划分
```
mdp/
├── mdp-apps          # 业务模块
│   ├── md-server/    # 启动服务
│   │   ├── boot-server/     # 单体启动类
│   │   └── worker-server/   # 任务执行服务
│   ├── md-api/       # API服务层
│   ├── md-workbench/ # 工作台业务
│   ├── md-console/   # 控制台业务
│   ├── md-open/      # 开发者平台
│   ├── md-gateway/   # 网关服务
│   └── md-public/    # 公共模块
├── mdp-base          # 核心工具库和 starter    
│   ├── md-all          # 核心工具类
│   ├── md-annotation   # 注解
│   ├── md-bom          # BOM管理
│   ├── md-boot         # Spring Boot 增强器
│   ├── md-cache-starter   # 缓存 starter
│   ├── md-captcha-starter # 验证码 starter
│   ├── md-cloud-starter  # 微服务 starter
│   ├── md-codegen        # 代码生成器
│   ├── md-core           # 核心类
│   ├── md-db             # 数据库相关
│   ├── md-db-mybatis-flex  # MyBatis-Flex 封装
│   ├── md-db-uid         # 雪花id 生成器
│   ├── md-echo-starter   # 字段回显 starter
│   ├── md-json-starter   # json starter
│   ├── md-log-starter    # 日志 starter
│   ├── md-mvc-flex       # MVC三层父类封装
│   ├── md-openapi3-starter # OpenAPI3 starter
│   ├── md-powerjob-worker-spring-boot-starter # PowerJob执行器 starter
│   ├── md-sa-token        # sa-token扩展
│   ├── md-scan-starter   # 接口扫描 starter
│   ├── md-sop-support    # 开放平台基础功能、工具封装
│   ├── md-util           # 工具类
│   ├── md-validator-starter # 表单校验 starter
│   └── md-xss-starter     # xss starter 
├── mdp-parent             #  父工程
└── mdp-sdk                 # 对外SDK
    ├── mdp-sdk-core            # SDK核心类
    └── mdp-simple-sdk          # SDK业务接口
```

## 4. 前端技术栈
- Vue3 + TypeScript
- Pinia 状态管理
- Vite 构建
- reka-ui + ant-design-vue
- Axios 封装请求
- 权限：前端路由守卫 + 后端鉴权

## 5. 架构分层（强制）
1. controller -> 对外接口层
2. service -> 业务逻辑 
3. mapper -> 数据访问 
4. entity -> 数据模型

## 6. 编码规范
- 禁止 Controller 写业务，可以做简单参数校验
- 异常统一捕获
- 日志必须使用 SLF4J + Logback
- 接口必须入参校验