package cn.uptra.schoolwork.modules.book.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 图书实体类
 */
@Data
@TableName("books")
public class Book {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 图书ID
     */
    private Long bid;
    
    /**
     * 图书标题
     */
    private String title;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 标签，多个标签用逗号分隔
     */
    private String tags;
    
    /**
     * 封面图片URL
     */
    @TableField("cover_url")
    private String coverUrl;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 库存
     */
    private Integer stock;
    
    /**
     * 销量
     */
    private Integer sales;

    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 获取标签数组
     * @return 标签数组
     */
    public String[] getTagArray() {
        if (tags == null || tags.isEmpty()) {
            return new String[0];
        }
        return tags.split(",");
    }
}
