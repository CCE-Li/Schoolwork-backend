package cn.uptra.schoolwork.modules.book.controller;


import cn.uptra.schoolwork.common.result.PageResult;
import cn.uptra.schoolwork.common.result.R;
import cn.uptra.schoolwork.common.security.CustomUserDetails;
import cn.uptra.schoolwork.modules.book.entity.Book;
import cn.uptra.schoolwork.modules.book.entity.Review;
import cn.uptra.schoolwork.modules.book.service.BookService;
import cn.uptra.schoolwork.modules.book.service.ReviewService;
import cn.uptra.schoolwork.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;

    /**
     * 书籍列表接口, 可根据作者、标题、标签进行筛选
     * required = false 表示参数可选（非必读）
     * @param author
     * @param title
     * @return  书籍列表
     */
    // @GetMapping("")
    // public List<Book> ListBooks(
    //                             @RequestParam(required = false) String bid,
    //                             @RequestParam(required = false) String author,
    //                             @RequestParam(required = false) String title,
    //                             @RequestParam(required = false) String tags) {
    //     if (bid != null) {
    //         return bookService.getBookByBid(Integer.parseInt(bid));
    //     }
    //     else {
    //         return bookService.listBooks(author, title, tags);
    //     }
    // }
    @GetMapping
    public R<PageResult<Book>> getBooks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String tags) {
        
        PageResult<Book> result = bookService.getBooks(page, pageSize, title, author, tags);
        return R.success(result);
    }

    // TODO: 平均评分 + 所有评分数量 + 所有评论
    @GetMapping("/{bid}")
    public R<?> about(@PathVariable("bid") Long bid) {
        Double avgRating = reviewService.getAverageRatingByBid(bid);
        Integer reviewCount = reviewService.getReviewCountByBid(bid);
        List<Review> reviews = reviewService.getAllReviewsByBid(bid);
        
        return R.success(new java.util.HashMap<String, Object>() {{
            put("averageRating", avgRating);
            put("reviewCount", reviewCount);
            put("reviews", reviews);
        }});
    }

    /**
     * 添加评论功能
     * @param review
     * @return 评论结果
     */
    @PostMapping("/{bid}")
    public R<Book> addReview(@RequestBody Review review,
                             @PathVariable("bid") Long bid,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long uid = userDetails.getUId();
        // 基本校验：uid/book 存在性
        if (uid == null) {
            return R.error("uid 参数缺失");
        }
        if (userService.getUserByUid(uid) == null) {
            return R.error("用户不存在");
        }

        if (bid == null) {
            return R.error("bid 参数缺失");
        }
        List<Book> books = bookService.getBookByBid(bid);
        if (books == null || books.isEmpty()) {
            return R.error("对应的书籍不存在");
        }

        // 判断是否已评论
        Integer count = reviewService.hasUserReviewedBook(bid, uid);
        if (count != null && count > 0) {
            return R.error("您已对该书发表评论，不能重复评论");
        }

        // 校验评分
        if (review == null) return R.error("请求体为空");
        Integer rating = review.getRating();
        if (rating == null || rating < 1 || rating > 5) {
            return R.error("评分必须在 1 到 5 之间");
        }

        // 设置必要字段并保存
        review.setBid(bid);
        review.setUid(uid);
        boolean ok = reviewService.save(review);
        if (ok) {
            return R.success();
        } else {
            return R.error("保存评论失败");
        }
    }
}
