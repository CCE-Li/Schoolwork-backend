package cn.uptra.schoolwork.modules.cart.service;

import cn.uptra.schoolwork.modules.cart.entity.Cart;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

public interface CartService extends IService<Cart> {
    Cart getCartByIdAndUidAndBid(Long uid, Long bid);
    // 按用户ID查询购物车项（推荐添加）
    List<Cart> listByUid(Long uid);

}
