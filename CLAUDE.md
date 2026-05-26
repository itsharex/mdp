# MDP (Master Data Platform) - 主数据平台后端工程

## 项目简介

MDP (Master Data Platform) 是一个基于 Java 17、SpringBoot、Vue3 的中后台快速开发平台，包含三个主要应用：
- **工作台** (md-workbench): 用户统一门户
- **控制台** (md-console): 管理员后台管理
- **开发者平台** (md-open): 第三方开发者接入中心
- **接口服务** (md-api): 对外提供接口供第三方通过SDK调用

MDP 是一个Monorepo项目，包含三个主要模块，分别位于mdp-base、mdp-apps、mdp-sdk目录下。
- mdp-base: 核心工具库 和 starter，封装业务无关的功能或工具
- mdp-apps: 业务模块
- mdp-sdk: 提供给第三方的SDK，所有的接口均来自 @mdp-apps/md-api/api-service 模块

## 核心架构

### 模块结构
```
mdp/
├── mdp-base/          # 核心工具库和 starter
│   ├── md-core/      # 核心工具类
│   ├── md-boot/      # Spring Boot 增强器
│   ├── md-db/        # 数据库相关
│   ├── md-mvc-flex/  # MyBatis-Flex 整合
│   └── ...           # 各种starter模块
├── mdp-apps/     # 业务模块
│   ├── md-server/    # 启动服务
│   │   ├── boot-server/     # 单体启动类
│   │   └── worker-server/   # 任务执行服务
│   ├── md-api/       # API服务层
│   ├── md-workbench/ # 工作台业务
│   ├── md-console/   # 控制台业务
│   ├── md-open/      # 开发者平台
│   ├── md-gateway/   # 网关服务
│   └── md-public/    # 公共模块
└── mdp-sdk/          # 提供给第三方的SDK
```

### 技术栈
- **后端**: Java 17, SpringBoot, MyBatis-Flex, Sa-Token, Redis
- **前端**: Vue3, Ant Design Vue, Vite, TypeScript
- **数据库**: MySQL 8.0+, 达梦数据库
- **文件存储**: 支持本地存储、阿里云OSS、MinIO等

### 核心特性
1. **权限管理**: 应用权限、菜单权限、按钮权限、数据权限、字段权限
2. **单点登录**: 支持SSO、OAuth2等多种协议
3. **数据同步**: 支持钉钉等三方数据同步
4. **代码生成**: 自主开发的代码生成器
5. **监控集成**: Druid监控、日志记录、操作审计

## 开发指南

### 启动应用

#### 单体模式启动
使用 IDE 直接运行 BootServerApplication

应用启动在 `http://localhost:23455`，默认端口 23455

#### 测试环境账号
- 运维管理员: ops_admin/admin
- 开发者管理员: open_admin/admin  
- 企业管理员: admin/admin

### 构建和测试

#### 构建整个项目
```bash
mvn clean install
```

#### 构建单个模块
```bash
cd mdp-apps/md-workbench
mvn clean install
```

#### 运行测试
```bash
mvn test
```

### API文档
启动应用后访问：
- http://localhost:23455/doc.html - 统一API文档
- http://localhost:23455/druid - 数据库监控

## 数据库配置

### 配置文件
主配置文件位于 `mdp-apps/md-server/boot-server/src/main/resources/application.yml`

### 数据库要求
- MySQL 8.0+ 或 达梦数据库
- Redis (端口 16379)
- 修改 `application.yml` 中的数据库连接信息：
  - `spring.datasource.druid.url`
  - `spring.datasource.druid.username`
  - `spring.datasource.druid.password`
  - `spring.data.redis`

## 开发规范

### 代码风格
- 使用 Lombok 减少样板代码
- 遵循 Spring Boot 最佳实践
- 使用 Sa-Token 进行权限管理
- 集成 MyBatis-Flex 进行数据库操作