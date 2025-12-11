package cn.uptra.schoolwork.modules.order.controller;

import cn.uptra.schoolwork.common.result.R;
import cn.uptra.schoolwork.modules.book.entity.Book;
import cn.uptra.schoolwork.modules.book.service.BookService;
import cn.uptra.schoolwork.modules.order.entity.Order;
import cn.uptra.schoolwork.modules.order.service.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import cn.uptra.schoolwork.common.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单管理控制器
 */
@RestController
@Tag(name = "订单管理")
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final BookService bookService;

    @Operation(summary = "创建订单（从购物车）")
    @PostMapping("/createFromCart")
    public R<String> createOrderFromCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long addressId) {
        Long userId = userDetails.getUId();
        String orderNo = orderService.createOrderFromCart(userId, addressId);
        return R.success(orderNo);
    }

    @Operation(summary = "立即购买创建订单")
    @PostMapping("/createDirectly")
    public R<String> createOrderDirectly(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long bid,
            @RequestParam Integer number,
            @RequestParam Long addressId) {
        Long uid = userDetails.getUId();
        String orderNo = orderService.createOrderDirectly(uid, bid, number, addressId);
        
        // 获取刚创建的订单
        Order order = orderService.lambdaQuery()
                .eq(Order::getOid, orderNo)
                .one();
        
        if (order == null) {
            return R.error(500, "订单创建失败");
        }
        
        List<Map<String, Object>> books = new ArrayList<>();
        books.add(Map.of("bid", bid, "number", number));
        order.setBooks(books);
        
        List<Book> bookList = bookService.getBookByBid(bid);
        if (bookList == null || bookList.isEmpty()) {
            return R.error(404, "图书不存在");
        }
        Book book = bookList.get(0);
        order.setTotal_Price(BigDecimal.valueOf(number).multiply(book.getPrice()));
        
        orderService.updateById(order);
        return R.success(order.getOid());
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/detail/{orderNo}")
    public R<Order> getOrderDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String orderNo) {
        Long userId = userDetails.getUId();
        Order order = orderService.getOrderDetail(orderNo, userId);
        if (order == null) {
            return R.error(404, "订单不存在");
        }
        return R.success(order);
    }

    @Operation(summary = "分页查询用户订单列表")
    @GetMapping("/list")
    public R<List<Map<String, Object>>> listUserOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        Long userId = userDetails.getUId();
        Page<Order> orderPage = orderService.listUserOrders(userId, pageNum, pageSize, status);
        List<Map<String, Object>> orderList = orderPage.getRecords().stream()
                .map(order -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("orderNo", order.getOid());
                    orderMap.put("createTime", order.getCreate_Time());
                    orderMap.put("status", order.getStatus());
                    orderMap.put("totalPrice", order.getTotal_Price());
                    orderMap.put("books", order.getBooks());
                    return orderMap;
                })
                .collect(Collectors.toList());
        return R.success(orderList);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{orderNo}")
    public R<Void> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String orderNo) {
        Long userId = userDetails.getUId();
        boolean success = orderService.cancelOrder(orderNo, userId);
        return success ? R.success() : R.error(400, "订单取消失败");
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirmReceipt/{orderNo}")
    public R<Void> confirmReceipt(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String orderNo) {
        Long userId = userDetails.getUId();
        boolean success = orderService.confirmReceipt(orderNo, userId);
        return success ? R.success() : R.error(400, "确认收货失败");
    }

    @Operation(summary = "支付订单")
    @PostMapping("/pay/{orderNo}")
    public R<Void> payOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String orderNo,
            @RequestParam String paymentMethod) {
        Long userId = userDetails.getUId();
        boolean success = orderService.payOrder(orderNo, userId, paymentMethod);
        return success ? R.success() : R.error(400, "支付失败");
    }

    @Operation(summary = "获取订单金额")
    @GetMapping("/amount/{orderNo}")
    public R<BigDecimal> getOrderAmount(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String orderNo) {
        Long userId = userDetails.getUId();
        BigDecimal amount = orderService.getOrderAmount(orderNo);
        return R.success(amount);
    }
}
