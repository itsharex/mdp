# MDP (Master Data Platform) - 主数据平台后端 业务工程

## 项目简介

mdp-base 是MDP项目的核心工具集，主要用于封装一些通用的工具、功能。

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
```

## 开发指南

### 构建mdp-base模块
```bash
cd mdp-base
mvn clean install
```

### 执行mdp-base模块的代码规范检查
```bash
cd mdp-base
mvn checkstyle:check
```

### 构建mdp-base模块,并跳过代码规范检查
```bash
cd mdp-base
mvn clean install -Dcheckstyle.skip=true
```

### 运行测试
```bash
mvn test
```

### 运行单个测试类
```bash
mvn test -Dtest=ClassName
```

## 开发规范

### 代码风格
- 使用 Lombok 减少样板代码
- 遵循 Spring Boot 最佳实践
- 使用 Sa-Token 进行权限管理
- 集成 MyBatis-Flex 进行数据库操作

## mdp-base 模块详解

### 核心模块说明
- `md-core`: 核心工具类库，包含基础工具、注解、AOP等
- `md-boot`: Spring Boot 增强器，提供自动配置和starter
- `md-db`: 数据库相关功能，包括Druid配置、多数据源支持
- `md-mvc-flex`: MyBatis-Flex 整合，提供数据库操作支持
- `md-sa-token`: Sa-Token 权限管理集成
- `md-validator-starter`: 自定义验证器starter
- `md-xss-starter`: XSS防护starter
- `md-cache-starter`: 缓存管理starter

### Maven 构建配置
- 使用 `mdp-parent` 作为父POM，统一管理版本
- 支持多环境配置：dev、test、prod
- 使用 checkstyle 进行代码规范检查
- 自动生成源码包和Javadoc

### 依赖管理
- 使用 BOM 统一管理版本号
- 主要依赖包括：
  - Spring Boot 3.5.x
  - MyBatis-Flex 1.11.x
  - Sa-Token 1.45.x
  - Hutool 5.8.x
  - FastJSON2 2.0.x

### 代码规范检查
项目使用 Checkstyle 进行代码规范检查，配置文件位于 `checkstyle.xml`
- 文件长度不超过 2500 行
- 每行字符数限制较宽松（10000字符）
- 包名必须小写
- 类名必须符合驼峰命名

