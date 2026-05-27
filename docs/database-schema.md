# 数据库设计规范

##  字段
###  强制字段（每张表必须有）
- id BIGINT PRIMARY KEY AUTO_INCREMENT
- created_at DATETIME DEFAULT NOW()
- created_by BIGINT DEFAULT NULL
### 可选字段
- updated_at DATETIME DEFAULT NOW()
- updated_by BIGINT DEFAULT NULL
- deleted_by BIGINT DEFAULT NULL
- deleted_at BIGINT DEFAULT 0

## 2. 命名规范
- 表名：小写 + 下划线
  user、user_role、order_info
- 字段：小写 + 下划线
  user_name、phone、email
- 索引：idx_字段名
  idx_user_name、idx_phone

## 3. 字段类型规范
- 主键：BIGINT
- 状态：TINYINT
- 时间：DATETIME
- 字符串：VARCHAR，长度合理
- 大文本：TEXT
- 金额：DECIMAL(18,2)

## 4. 索引规范
- 必须有主键，主键采用雪花ID
- 查询条件必须建索引
- 禁止超过 5 个联合索引
- 禁止索引字段为 NULL

## 5. 禁止行为
- 禁止使用外键
- 禁止使用触发器
- 禁止使用存储过程
- 禁止 SELECT *

## 6. SQL 规范
- 禁止 join 超过 3 张表
- 必须使用 MyBatis-flex
- 禁止出现全表扫描
- 批量操作必须用 batch