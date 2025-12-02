package cn.uptra.schoolwork.modules.admin.controller;

import cn.uptra.schoolwork.common.result.R;
import cn.uptra.schoolwork.common.security.CustomUserDetails;
import cn.uptra.schoolwork.modules.order.entity.Order;
import cn.uptra.schoolwork.modules.order.service.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "Admin - 订单管理")
@RequiredArgsConstructor
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "分页查询所有订单")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Page<Order>> listOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        return R.success(orderService.adminListOrders(page, orderNo, status));
    }

    /**
     * 销售统计
     * 返回map
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/statistics/sales")
    @Operation(summary = "销售统计")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getSalesStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> result = (Map<String, Object>)orderService.getSalesStatistics(startDate, endDate);
        if (result == null) { // 处理result为空时
            result = new HashMap<>();
        }
        return result;
    }

    @GetMapping("/{orderNo}")
    @Operation(summary = "获取订单详情")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Order> getOrderDetail(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String orderNo) {
        Long userId = userDetails.getUId();
        return R.success(orderService.getOrderDetail(orderNo, userId));
    }

    @PutMapping("/{oid}/status/{status}")
    @Operation(summary = "更新订单状态")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> updateOrderStatus(
            @PathVariable("oid") String orderId,
            @PathVariable Integer status) {
        orderService.updateOrderStatus(orderId, status);
        return R.success();
    }
    
    @PutMapping("/{oid}/ship")
    @Operation(summary = "订单发货")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> shipOrder(
            @PathVariable("oid") String orderId,
            @RequestParam(required = false) String shippingCompany,
            @RequestParam(required = false) String shippingNo) {
        orderService.shipOrder(orderId, shippingCompany, shippingNo);
        return R.success();
    }

    @GetMapping("/statistics/daily")
    @Operation(summary = "获取每日订单统计")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Map<String, Object>> getDailyStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return R.success();
    }

}
