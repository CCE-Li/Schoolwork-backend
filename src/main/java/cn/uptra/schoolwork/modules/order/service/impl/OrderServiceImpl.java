package cn.uptra.schoolwork.modules.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;




import cn.uptra.schoolwork.common.exception.BusinessException;
import cn.uptra.schoolwork.modules.order.entity.Order;
import cn.uptra.schoolwork.modules.order.mapper.OrderMapper;
import cn.uptra.schoolwork.modules.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.uptra.schoolwork.modules.book.entity.Book;
import cn.uptra.schoolwork.modules.book.service.BookService;
import cn.uptra.schoolwork.modules.cart.entity.Cart;
import cn.uptra.schoolwork.modules.cart.service.CartService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private CartService cartService;

    /**
     * 创建订单（从购物车）
     * @param uid 用户ID
     * @param addressId 地址ID
     * @return 订单号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrderFromCart(Long uid, Long addressId) {
        // 1. 获取用户购物车中的所有商品
        List<Cart> cartItems = cartService.listByUid(uid);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException("购物车为空，无法创建订单");
        }
        
        // 2. 获取所有图书ID并批量查询图书信息
        List<Long> bookIds = cartItems.stream()
                .map(Cart::getBid)
                .collect(Collectors.toList());
        List<Book> books = bookService.getBooksByIds(bookIds);
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getBid, b -> b, (a, b) -> a));
        
        // 3. 构建订单商品列表并计算总价
        List<Map<String, Object>> orderBooks = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        
        for (Cart cartItem : cartItems) {
            Book book = bookMap.get(cartItem.getBid());
            if (book == null) {
                throw new BusinessException("图书不存在: " + cartItem.getBid());
            }
            
            Map<String, Object> bookInfo = new HashMap<>();
            bookInfo.put("bid", cartItem.getBid());
            bookInfo.put("number", cartItem.getNumber());
            orderBooks.add(bookInfo);
            
            // 计算小计并累加到总价
            BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(cartItem.getNumber()));
            totalPrice = totalPrice.add(itemTotal);
        }
        
        // 4. 创建订单
        Order order = new Order();
        order.setOid(generateOrderNo());
        order.setUid(uid);
        order.setStatus(Order.Status.UNPAID);
        order.setBooks(orderBooks);
        order.setTotal_Price(totalPrice);
        order.setCreate_Time(LocalDateTime.now());
        
        this.save(order);
        
        // 5. 清空用户购物车
        cartService.lambdaUpdate()
                .eq(Cart::getUid, uid)
                .remove();
        
        return order.getOid();
    }

    /**
     * 立即购买创建订单
     */
    @Override
    public String createOrderDirectly(Long userId, Long bookId, Integer quantity, Long addressId) {
        Order order = new Order();
        order.setOid(generateOrderNo());
        order.setUid(userId);
        order.setStatus(Order.Status.UNPAID);
        order.setCreate_Time(LocalDateTime.now());

        save(order);
        return order.getOid();
    }

    /**
     * 获取订单详情
     */
    @Override
    public Order getOrderDetail(String orderNo, Long userId) {
        return orderMapper.getOrderDetail(orderNo, userId);
    }



    /**
     * 获取订单详情
     * @param uid 用户id
     * @return 订单详情
     */
    @Override
    public Page<Order> listUserOrders(Long uid, Integer pageNum, Integer pageSize, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUid, uid);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreate_Time);
        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> result = this.page(page, wrapper);
        
        // 补充图书详情信息
        enrichOrdersWithBookDetails(result.getRecords());
        
        return result;
    }
    
    /**
     * 为订单列表补充图书详情信息（name, price, coverUrl）
     */
    private void enrichOrdersWithBookDetails(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }
        
        // 收集所有订单中的 bookId
        Set<Long> allBookIds = new HashSet<>();
        for (Order order : orders) {
            if (order.getBooks() != null) {
                for (Map<String, Object> bookItem : order.getBooks()) {
                    Object bidObj = bookItem.get("bid");
                    if (bidObj != null) {
                        allBookIds.add(Long.valueOf(bidObj.toString()));
                    }
                }
            }
        }
        
        if (allBookIds.isEmpty()) {
            return;
        }
        
        // 批量查询图书信息
        List<Book> books = bookService.getBooksByIds(new ArrayList<>(allBookIds));
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getBid, b -> b, (a, b) -> a));
        
        // 补充图书详情到每个订单的 books 中
        for (Order order : orders) {
            if (order.getBooks() != null) {
                for (Map<String, Object> bookItem : order.getBooks()) {
                    Object bidObj = bookItem.get("bid");
                    if (bidObj != null) {
                        Long bid = Long.valueOf(bidObj.toString());
                        Book book = bookMap.get(bid);
                        if (book != null) {
                            bookItem.put("name", book.getTitle());
                            bookItem.put("price", book.getPrice());
                            bookItem.put("coverUrl", book.getCoverUrl());
                        }
                    }
                }
            }
        }
    }

    /**
     * 取消订单
     * @param oid
     * @param uid
     * @return
     */
    @Override
    public boolean cancelOrder(String oid, Long uid) {
        return orderMapper.cancelOrder(oid, uid);
    }

    /**
     * 确认收货
     * @param oid
     * @param uid
     * @return
     */
    @Override
    public boolean confirmReceipt(String oid, Long uid) {
        return orderMapper.confirmReceipt(oid, uid);
    }


    /**
     * 订单支付
     * @param orderNo
     * @param userId
     * @param paymentMethod
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(String orderNo, Long userId, String paymentMethod) {
        Order order = lambdaQuery()
                .eq(Order::getOid, orderNo)
                .eq(Order::getUid, userId)
                .one();
        
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 只有未支付的订单可以支付
        if (!Objects.equals(order.getStatus(), Order.Status.UNPAID)) {
            throw new BusinessException("订单状态异常，无法支付");
        }
        
        // 模拟支付成功
        order.setStatus(Order.Status.PAID);
        return updateById(order);
    }

    /**
     * 通过oid获取订单金额
     * @param orderNo
     * @return
     */
    @Override
    public BigDecimal getOrderAmount(String orderNo) {
        //
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOid, orderNo));
        if(order == null) return null;
        return order.getTotal_Price();
    }
    
    @Override
    public Page<Order> adminListOrders(Page<Order> page, String orderNo, Integer status) {
        return this.page(page, new LambdaQueryWrapper<Order>()
                .eq(StringUtils.isNotBlank(orderNo), Order::getOid, orderNo)
                .eq(Objects.nonNull(status), Order::getStatus, status)
                .orderByDesc(Order::getCreate_Time));
    }
    
    @Override
    public void updateOrderStatus(String orderNo, Integer status) {
        // 支持按 oid 或 id 查询
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOid, orderNo));
        if (order == null) {
            // 尝试按 id 查询（前端可能传的是数字 id）
            try {
                Integer id = Integer.parseInt(orderNo);
                order = this.getById(id);
            } catch (NumberFormatException ignored) {
            }
        }
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(status);
        this.updateById(updateOrder);
    }
    
    @Override
    public void shipOrder(String orderNo, String shippingCompany, String shippingNo) {
        // 支持按 oid 或 id 查询
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOid, orderNo));
        if (order == null) {
            try {
                Integer id = Integer.parseInt(orderNo);
                order = this.getById(id);
            } catch (NumberFormatException ignored) {
            }
        }
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态是否为已支付（假设状态2为已支付）
        // 只有已支付的订单才能发货
        if (!Objects.equals(order.getStatus(), Order.Status.PAID)) {
            throw new BusinessException("只有已支付的订单才能发货");
        }

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Order.Status.SHIPPED); // 发货后状态：已发货
        // 注意：根据数据库结构，orders表没有shipping_company和shipping_no字段
        // 如果需要存储物流信息，需要先添加到orders表或创建新表
        this.updateById(updateOrder);
    }
    
    @Override
    public Map<String, Object> getDailyOrderStatistics(LocalDate startDate, LocalDate endDate) {
        // 确保开始日期和结束日期有效
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30); // 默认查询最近30天
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // 查询订单统计数据
        List<Map<String, Object>> stats = getBaseMapper().selectDailyOrderStats(
            startDate.atStartOfDay(),
            endDate.plusDays(1).atStartOfDay()
        );
        
        // 计算汇总数据
        int totalOrders = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (Map<String, Object> stat : stats) {
            totalOrders += (int) stat.get("order_count");
            totalAmount = totalAmount.add((BigDecimal) stat.get("total_amount"));
        }
        
        // 返回统计数据
        Map<String, Object> result = new HashMap<>();
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("totalOrders", totalOrders);
        result.put("totalAmount", totalAmount);
        result.put("dailyStats", stats);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getSalesStatistics(LocalDate startDate, LocalDate endDate) {
        // 确保开始日期和结束日期有效
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(6); // 默认查询最近6个月
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // 查询销售统计数据
        List<Map<String, Object>> stats = getBaseMapper().selectSalesStats(
            startDate.atStartOfDay(),
            endDate.plusDays(1).atStartOfDay()
        );
        
        // 计算汇总数据
        int totalOrders = 0;
        int totalItems = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (stats != null) {
            for (Map<String, Object> stat : stats) {
                // 安全地获取并处理可能为null的值
                totalOrders += stat.get("order_count") != null ? ((Number) stat.get("order_count")).intValue() : 0;
                totalItems += stat.get("total_items") != null ? ((Number) stat.get("total_items")).intValue() : 0;
                if (stat.get("total_amount") != null) {
                    totalAmount = totalAmount.add((BigDecimal) stat.get("total_amount"));
                }
            }
        }
        // 返回统计数据
        Map<String, Object> result = new HashMap<>();
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("totalOrders", totalOrders);
        result.put("totalItems", totalItems);
        result.put("totalAmount", totalAmount);
        result.put("stats", stats != null ? stats : new ArrayList<>());
        
        return result;
    }
    
    // 生成订单号
    private String generateOrderNo() {
        // 时间戳 + 随机数
        return "O" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
    }
}
