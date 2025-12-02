# 后台用户管理接口文档 (Admin User Management API)

## 基础信息
- **Base URL**: `/api/admin/users`
- 所有接口都需要 `ADMIN` 角色权限

## 接口列表

### 1. 分页查询用户列表
- **URL**: `GET /api/admin/users`
- **描述**: 获取用户分页列表
- **请求参数**:
  - `pageNum` (可选, 默认值: 1): 页码
  - `pageSize` (可选, 默认值: 10): 每页数量
  - `keyword` (可选): 搜索关键词
- **响应**: 分页的用户列表

### 2. 获取用户详情
- **URL**: `GET /api/admin/users/{id}`
- **描述**: 根据ID获取用户详情
- **路径参数**:
  - `id`: 用户ID
- **响应**: 用户详情

### 3. 更新用户状态
- **URL**: `PUT /api/admin/users/{id}/status`
- **描述**: 更新用户状态（启用/禁用）
- **路径参数**:
  - `id`: 用户ID
- **请求参数**:
  - `status`: 状态码 (0:禁用, 1:启用)
- **响应**: 操作结果

### 4. 更新用户角色
- **URL**: `PUT /api/admin/users/{id}/role`
- **描述**: 更新用户角色
- **路径参数**:
  - `id`: 用户ID
- **请求参数**:
  - `status`: 角色状态码
- **响应**: 操作结果

### 5. 删除用户
- **URL**: `DELETE /api/admin/users/{id}`
- **描述**: 删除指定ID的用户
- **路径参数**:
  - `id`: 用户ID
- **响应**: 操作结果

### 6. 获取用户注册统计
- **URL**: `GET /api/admin/users/statistics/register`
- **描述**: 获取用户注册统计信息
- **请求参数**:
  - `startDate` (可选): 开始日期 (格式: yyyy-MM-dd)
  - `endDate` (可选): 结束日期 (格式: yyyy-MM-dd)
- **响应**: 注册统计数据

### 7. 获取活跃用户统计
- **URL**: `GET /api/admin/users/statistics/active`
- **描述**: 获取活跃用户统计信息
- **请求参数**:
  - `startDate` (可选): 开始日期 (格式: yyyy-MM-dd)
  - `endDate` (可选): 结束日期 (格式: yyyy-MM-dd)
- **响应**: 活跃用户统计数据

## 公共响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {} // 具体数据
}
```

## 错误码说明
- 200: 请求成功
- 401: 未授权
- 403: 禁止访问
- 404: 资源不存在
- 500: 服务器内部错误
