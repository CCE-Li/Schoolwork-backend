package cn.uptra.schoolwork.modules.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("order_items")
public class OrderItem {
    @TableId
    private Long id;
    private Long orderId;       // 订单ID
    private Long bookId;        // 书籍ID
    private String bookName;    // 书籍名称
    private String bookImage;   // 书籍图片
    private BigDecimal price;   // 购买时价格
    private Integer quantity;   // 购买数量
    private BigDecimal totalPrice; // 小计金额
}
