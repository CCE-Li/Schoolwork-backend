# 用户订单接口文档 (User Order API)

## 基础信息
- **Base URL**: `/api/order`
- 需要用户登录才能访问
- 请求头需包含 `userId` 字段

## 接口列表

### 1. 从购物车创建订单
- **URL**: `POST /api/order/createFromCart`
- **描述**: 将购物车中的商品创建为订单
- **请求头**:
  - `userId`: 用户ID
- **请求参数**:
  - `addressId`: 收货地址ID
- **响应**: 订单号

### 2. 立即购买创建订单
- **URL**: `POST /api/order/createDirectly`
- **描述**: 直接购买商品，不经过购物车
- **请求头**:
  - `userId`: 用户ID
- **请求参数**:
  - `bookId`: 图书ID
  - `quantity`: 购买数量
  - `addressId`: 收货地址ID
- **响应**: 订单号

### 3. 获取订单详情
- **URL**: `GET /api/order/detail/{orderNo}`
- **描述**: 根据订单号获取订单详情
- **请求头**:
  - `userId`: 用户ID
- **路径参数**:
  - `orderNo`: 订单号
- **响应**: 订单详情

### 4. 分页查询用户订单列表
- **URL**: `GET /api/order/list`
- **描述**: 获取用户的订单列表，支持分页和状态筛选
- **请求头**:
  - `userId`: 用户ID
- **请求参数**:
  - `pageNum` (可选, 默认值: 1): 页码
  - `pageSize` (可选, 默认值: 10): 每页数量
  - `status` (可选): 订单状态
- **响应**: 分页的订单列表

### 5. 取消订单
- **URL**: `POST /api/order/cancel/{orderNo}`
- **描述**: 取消指定订单
- **请求头**:
  - `userId`: 用户ID
- **路径参数**:
  - `orderNo`: 订单号
- **响应**: 操作结果

### 6. 确认收货
- **URL**: `POST /api/order/confirmReceipt/{orderNo}`
- **描述**: 确认收到商品
- **请求头**:
  - `userId`: 用户ID
- **路径参数**:
  - `orderNo`: 订单号
- **响应**: 操作结果

### 7. 支付订单
- **URL**: `POST /api/order/pay/{orderNo}`
- **描述**: 支付指定订单
- **请求头**:
  - `userId`: 用户ID
- **路径参数**:
  - `orderNo`: 订单号
- **请求参数**:
  - `paymentMethod`: 支付方式
- **响应**: 操作结果

### 8. 获取订单金额
- **URL**: `GET /api/order/amount/{orderNo}`
- **描述**: 获取订单金额
- **请求头**:
  - `userId`: 用户ID
- **路径参数**:
  - `orderNo`: 订单号
- **响应**: 订单金额

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
- 400: 请求参数错误
- 401: 未授权/未登录
- 403: 禁止访问
- 404: 资源不存在
- 500: 服务器内部错误

## 注意事项
1. 所有接口都需要在请求头中包含有效的 `userId`
2. 订单状态码说明：
   - 0: 待支付
   - 1: 已支付待发货
   - 2: 已发货
   - 3: 已完成
   - 4: 已取消
   - 5: 已退款
3. 取消订单操作仅在下单后30分钟内可执行
4. 确认收货后，订单状态将变更为"已完成"
