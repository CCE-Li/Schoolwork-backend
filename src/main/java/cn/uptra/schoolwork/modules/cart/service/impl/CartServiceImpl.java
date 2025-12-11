package cn.uptra.schoolwork.modules.cart.service.impl;

import cn.uptra.schoolwork.modules.cart.entity.Cart;
import cn.uptra.schoolwork.modules.cart.mapper.CartMapper;
import cn.uptra.schoolwork.modules.cart.service.CartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    /**
     * 根据用户ID和商品ID获取购物车项
     * @param uid 用户ID
     * @param bid 商品ID
     * @return 购物车项
     */
    @Override
    public Cart getCartByIdAndUidAndBid(Long uid, Long bid) {
        return this.baseMapper.getCartByIdAndUidAndBid(uid, bid);
    }

    @Override
    public List<Cart> listByUid(Long uid) {
        return lambdaQuery().eq(Cart::getUid, uid).list();
    }
}
