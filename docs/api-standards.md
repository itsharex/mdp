# API 接口设计规范

## 1. 统一返回格式
```
{
"code": 0,        // 业务状态码
"msg": "成功",       // 提示信息
"data": Object,     // 数据
"path": ""     // 请求路径
"extra": Object     // 附加数据
"timestamp": Long     // 响应时间戳
}
```

## 2. 状态码规范
- 0    成功
- -1    系统繁忙
- -2    超时状态码
- -9    参数验证异常状态码
- -10    操作异常状态码

## 3. 请求方式
- POST 分页查询
- GET 其他查询
- POST 新增/提交/修改/删除

## 4. URL 命名规范
- 小写 + 驼峰
- URL规则：/网关前缀/服务前缀/模块/表名/方法
- 网关前缀: 固定为api
- 服务前缀: workbench、console、open等
- 模块: system、msg等
- 表名: user、file、msgTask等
- 方法: page、save、update、delete、getById等
- 例：/api/workbench/system/dict/page
- 例：/api/console/organization/user/getById?id=${id}

## 5. 参数规范
- 路径参数：/user/{id}
- 查询参数：?name=1&age=2
- 表单：Content-Type: form-data
- JSON：Content-Type: application/json

## 6. 接口规范
- 分页接口使用POST请求
- 分页接口入参使用：PageParams<XxxQuery>
- 分页接口返回值使用：R<Page<XxxVo>>

## 7. 接口安全
- 敏感接口必须签名
- 防重：nonce + timestamp
- 频率限制：Sentinel