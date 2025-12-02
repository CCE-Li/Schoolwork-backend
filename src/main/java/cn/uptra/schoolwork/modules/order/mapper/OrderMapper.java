package cn.uptra.schoolwork.modules.order.mapper;

import cn.uptra.schoolwork.modules.order.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import cn.uptra.schoolwork.modules.order.entity.Order;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 获取每日订单统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 每日统计结果，包含日期、订单数、总金额
     */
    @Select("""
        SELECT 
            DATE(create_time) AS date,
            COUNT(*) AS order_count,
            SUM(total_price) AS total_amount
        FROM orders
        WHERE create_time BETWEEN #{startTime} AND #{endTime}
        GROUP BY DATE(create_time)
        ORDER BY date
    """)
    @Results({
        @Result(column = "date", property = "date"),
        @Result(column = "order_count", property = "orderCount"),
        @Result(column = "total_amount", property = "totalAmount")
    })
    List<Map<String, Object>> selectDailyOrderStats(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取销售统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 销售统计结果，包含日期、订单数、商品总数、总金额
     */
    @Select("""
        SELECT 
            DATE(o.create_time) AS date,
            COUNT(DISTINCT o.oid) AS order_count,
            SUM(JSON_LENGTH(o.books, '$.items')) AS total_items,
            SUM(o.total_price) AS total_amount
        FROM orders o
        WHERE o.create_time BETWEEN #{startTime} AND #{endTime}
        GROUP BY DATE(o.create_time)
        ORDER BY date
    """)
    @Results({
        @Result(column = "date", property = "date"),
        @Result(column = "order_count", property = "orderCount"),
        @Result(column = "total_items", property = "totalItems"),
        @Result(column = "total_amount", property = "totalAmount")
    })
    List<Map<String, Object>> selectSalesStats(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 获取用户订单总金额
     * @return 订单总金额
     */
    @Select("SELECT SUM(total_price) FROM orders WHERE oid = ${oid}")
    BigDecimal getOrderAmount(String oid);



    /**
     * 生成取消订单的方法
     * 根据用户uid和订单号取消订单
     */
    @Update("UPDATE orders SET status = 5 WHERE oid = #{oid} AND uid = #{uid}")
    boolean cancelOrder(String oid, Long uid);

    /**
     * 确认收货
     * @param oid
     * @param uid
     * @return
     */
    @Update("UPDATE orders SET status = 3 WHERE oid = #{oid} AND uid = #{uid}")
    boolean confirmReceipt(String oid, Long uid);

    @Select("SELECT * FROM orders WHERE oid = #{oid} AND uid = #{uid}")
    Order selectOrderByOidAndUid(String oid, Long uid);
}
