package cn.uptra.schoolwork.modules.cart.controller;

import cn.uptra.schoolwork.common.result.R;
import cn.uptra.schoolwork.common.security.CustomUserDetails;
import cn.uptra.schoolwork.modules.book.entity.Book;
import cn.uptra.schoolwork.modules.book.service.BookService;
import cn.uptra.schoolwork.modules.cart.entity.Cart;
import cn.uptra.schoolwork.modules.cart.entity.CartVO;
import cn.uptra.schoolwork.modules.cart.service.CartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private BookService bookService;

    /**
     *  添加商品到购物车
     *  1. 请求体包含商品信息（商品ID、数量等）
     *  2. 用户ID作为请求参数传递
     *  3. 当已经添加过相同商品时，数量 + number
     *  4. 用户未登录时，返回错误提示
     *  @param cart 购物车商品信息
     *  @return 操作结果
     */
    @PostMapping("/add")
    public R<Cart> addToCart(@RequestBody Cart cart,
                             @AuthenticationPrincipal CustomUserDetails userDetails){
        Long uid = userDetails.getUId();
        cart.setUid(uid);
        // 确保数量至少为1
        if (cart.getNumber() <= 0) {
            cart.setNumber(1);
        }
        if (uid == null) {
            return R.error("用户未登录，无法添加到购物车");
        } else {
            Cart existingCartItem = cartService.getCartByIdAndUidAndBid(uid, cart.getBid());
            if (existingCartItem != null) {
                // 已存在相同商品，数量加 number
                existingCartItem.setNumber(existingCartItem.getNumber() + cart.getNumber());
                cartService.updateById(existingCartItem);
                return R.error("商品已存在，数量已更新");
            } else {
                // 不存在相同商品，添加新商品
                cart.setUid(uid);
                cartService.save(cart);
                return R.success(cart);
            }
        }
    }

    /**
 * 获取用户的购物车列表（完善版）
 * @param userDetails 登录用户信息
 * @return 包含完整图书信息的购物车列表
 */
@GetMapping("/list")
public R<List<CartVO>> getCartItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
    // 1. 验证用户登录状态
    if (userDetails == null || userDetails.getUId() == null) {
        return R.error("用户未登录，无法获取购物车列表");
    }
    Long uid = userDetails.getUId();

    // 2. 查询当前用户的所有购物车项
    List<Cart> userCarts = cartService.listByUid(uid); // 建议在CartService中添加按UID查询的方法，避免全表扫描
    if (CollectionUtils.isEmpty(userCarts)) {
        return R.success(new ArrayList<>()); // 空购物车返回空列表，而非null
    }

    // 3. 提取所有图书ID，批量查询图书信息（减少数据库查询次数）
    List<Long> bookIds = userCarts.stream()
            .map(Cart::getBid)
            .distinct() // 去重，避免重复查询同一本书
            .collect(Collectors.toList());
    
    List<Book> books = bookService.getBooksByIds(bookIds); // 建议添加批量查询图书的方法
    Map<Long, Book> bookMap = books.stream()
            .collect(Collectors.toMap(Book::getBid, book -> book)); // 图书ID -> 图书对象的映射

    // 4. 组装购物车VO（视图对象），填充图书信息
    List<CartVO> cartVOList = userCarts.stream()
            .map(cart -> {
                CartVO cartVO = new CartVO();
                // 复制购物车基础字段（id、uid、bid、number等）
                BeanUtils.copyProperties(cart, cartVO);
                
                // 根据bid获取图书信息，填充剩余字段
                Book book = bookMap.get(cart.getBid());
                if (book != null) {
                    cartVO.setTitle(book.getTitle()); // 图书标题
                    cartVO.setImage(book.getCoverUrl()); // 图书封面（使用正确的字段名）
                    cartVO.setPrice(book.getPrice()); // 图书价格
                    cartVO.setAuthor(book.getAuthor()); // 作者
                    cartVO.setTags(book.getTags()); // 标签（可按逗号拼接成字符串）
                    cartVO.setStock(book.getStock()); // 库存
                } else {
                    // 图书不存在时的默认值（避免空指针）
                    cartVO.setTitle("图书 #" + cart.getBid());
                    cartVO.setImage("");
                    cartVO.setPrice(BigDecimal.ZERO);
                    cartVO.setAuthor("");
                    cartVO.setTags("");
                    cartVO.setStock(0);
                }

                // 默认未选中（可根据业务需求调整，比如从数据库读取checked状态）
                cartVO.setChecked(false);
                // 数量默认1（如果数据库字段可能为null）
                cartVO.setNumber(cart.getNumber());
                
                return cartVO;
            })
            .collect(Collectors.toList());

    return R.success(cartVOList);
}

    /**
     * 删除购物车项
     * 更具用户框选的bid列表删除对应的购物车项
     * @param uid 用户ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public R<Void> deleteCartItems(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @RequestBody List<Long> bidList) {
        Long uid = userDetails == null ? null : userDetails.getUId();
        if (uid == null) {
            return R.error("用户未登录，无法删除购物车项");
        }
        List<Cart> userCartItems = cartService.list().stream()
                .filter(cart -> cart.getUid().equals(uid) && bidList.contains(cart.getBid()))
                .toList();
        if (!userCartItems.isEmpty()) {
            cartService.removeByIds(userCartItems.stream().map(Cart::getId).toList());
            return R.success();
        } else {
            return R.error("没有找到对应的购物车项");
        }
    }

    /**
     * 更新购物车中商品的数量，前端使用加减号实现
     * @param cart 购物车商品信息
     * @return 操作结果
     */
    @PostMapping("/update")
    public R<Void> updateCartItem(@RequestBody Cart cart,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long uid = userDetails == null ? null : userDetails.getUId();
        if (uid == null) {
            return R.error("用户未登录，无法更新购物车项");
        }
        Cart existingCartItem = cartService.getById(cart.getId());
        if (existingCartItem != null && uid.equals(existingCartItem.getUid())) {
            existingCartItem.setNumber(cart.getNumber());
            cartService.updateById(existingCartItem);
            return R.success();
        } else {
            return R.error("购物车项不存在或不属于当前用户");
        }
    }
}
