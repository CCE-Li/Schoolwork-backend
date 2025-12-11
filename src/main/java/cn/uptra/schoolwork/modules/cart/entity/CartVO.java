package cn.uptra.schoolwork.modules.cart.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartVO {
    private Long id;         // 购物车项ID
    private Long uid;        // 用户ID
    private Long bid;        // 图书ID
    private String title;    // 图书标题
    private String image;    // 图书封面
    private BigDecimal price;// 图书价格
    private Integer number;  // 购买数量
    private String author;   // 作者
    private String tags;     // 标签（逗号分隔）
    private Integer stock;   // 库存
    private Boolean checked; // 是否选中
}