package cn.uptra.schoolwork.modules.order.service;

import cn.uptra.schoolwork.modules.order.entity.Order;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface OrderService extends IService<Order> {
    /**
     * 创建订单（从购物车）
     */
    String createOrderFromCart(Long uid, Long addressId);
    
    /**
     * 立即购买创建订单
     */
    String createOrderDirectly(Long uid, Long bid, Integer quantity, Long addressId);
    
    /**
     * 获取订单详情
     */
    Order getOrderDetail(String orderNo, Long userId);
    
    /**
     * 分页查询用户订单列表
     */
    Page<Order> listUserOrders(Long uid, Integer pageNum, Integer pageSize, Integer status);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(String oid, Long uid);

    /**
     * 支付订单（模拟支付）
     */
    boolean payOrder(String orderNo, Long uid, String paymentMethod);
    
    /**
     * 获取订单总金额
     */
    BigDecimal getOrderAmount(String oid);
    
    /**
     * 管理员分页查询订单列表
     */
    Page<Order> adminListOrders(Page<Order> page, String orderNo, Integer status);
    
    /**
     * 更新订单状态
     */
    void updateOrderStatus(String orderNo, Integer status);
    
    /**
     * 订单发货
     */
    void shipOrder(String orderNo, String shippingCompany, String shippingNo);
    
    /**
     * 获取每日订单统计
     */
    Object getDailyOrderStatistics(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取销售统计
     */
    Map<String, Object> getSalesStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 确认收货
     */
    boolean confirmReceipt(String oid, Long uid);
}
