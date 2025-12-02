# 订单管理接口文档 (Order Management API)

## 基础信息
- **Base URL**: `/api/admin/orders`
- 所有接口都需要 `ADMIN` 角色权限

## 接口列表

### 1. 分页查询所有订单
- **URL**: `GET /api/admin/orders`
- **描述**: 获取订单分页列表
- **请求参数**:
  - `pageNum` (可选, 默认值: 1): 页码
  - `pageSize` (可选, 默认值: 10): 每页数量
  - `orderNo` (可选): 订单号
  - `status` (可选): 订单状态
- **响应**: 分页的订单列表

### 2. 获取销售统计
- **URL**: `GET /api/admin/orders/statistics/sales`
- **描述**: 获取销售统计数据
- **请求参数**:
  - `startDate` (可选): 开始日期 (格式: yyyy-MM-dd)
  - `endDate` (可选): 结束日期 (格式: yyyy-MM-dd)
- **响应**: 销售统计数据

### 3. 获取订单详情
- **URL**: `GET /api/admin/orders/{orderNo}`
- **描述**: 根据订单号获取订单详情
- **请求头**:
  - `userId`: 用户ID
- **路径参数**:
  - `orderNo`: 订单号
- **响应**: 订单详情

### 4. 更新订单状态
- **URL**: `PUT /api/admin/orders/{oid}/status/{status}`
- **描述**: 更新订单状态
- **路径参数**:
  - `oid`: 订单ID
  - `status`: 新状态码
- **响应**: 操作结果

### 5. 订单发货
- **URL**: `PUT /api/admin/orders/{oid}/ship`
- **描述**: 订单发货
- **路径参数**:
  - `oid`: 订单ID
- **请求参数**:
  - `shippingCompany` (可选): 物流公司
  - `shippingNo` (可选): 物流单号
- **响应**: 操作结果

### 6. 获取每日订单统计
- **URL**: `GET /api/admin/orders/statistics/daily`
- **描述**: 获取每日订单统计数据
- **请求参数**:
  - `startDate` (可选): 开始日期 (格式: yyyy-MM-dd)
  - `endDate` (可选): 结束日期 (格式: yyyy-MM-dd)
- **响应**: 每日订单统计数据

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
