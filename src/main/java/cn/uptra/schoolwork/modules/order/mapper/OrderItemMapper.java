package cn.uptra.schoolwork.modules.order.mapper;

import cn.uptra.schoolwork.modules.order.entity.OrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    /**
     * 根据订单ID查询订单项列表
     */
    List<OrderItem> selectByOrderId(Long orderId);
    
    /**
     * 批量插入订单项
     */
    int batchInsert(List<OrderItem> orderItems);
}
