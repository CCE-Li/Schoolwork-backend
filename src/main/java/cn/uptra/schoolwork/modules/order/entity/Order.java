package cn.uptra.schoolwork.modules.order.entity;

import cn.uptra.schoolwork.common.handler.JsonTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单实体类
 */
@Data
@TableName(value = "orders", autoResultMap = true)
public class Order {
    
    @TableId("id")
    private Integer id;

    private String oid; // 订单编号

    private Long uid;    // 用户ID
    private Integer status; // 订单状态

    @TableField(value = "books", typeHandler = JsonTypeHandler.class)
    private List<Map<String, Object>> books; // 订单商品JSON数组

    @TableField("total_price")
    private BigDecimal total_Price; // 订单总金额

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private LocalDateTime create_Time; // 创建时间

    
    // 订单状态枚举
    public static class Status {
        public static final int UNPAID = 0;     // 待支付
        public static final int PAID = 1;       // 已支付
        public static final int SHIPPED = 2;    // 已发货
        public static final int COMPLETED = 3;  // 已完成
        public static final int CANCELLED = 4;  // 已取消
    }
    
    // 用于存储解析后的订单项
    @TableField(exist = false)
    private List<Map<String, Object>> items;
    
    // 获取订单项数量
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
    
    // 兼容前端字段名：totalPrice
    public BigDecimal getTotalPrice() {
        return this.total_Price;
    }
    
    // 兼容前端字段名：createTime
    public LocalDateTime getCreateTime() {
        return this.create_Time;
    }
}
