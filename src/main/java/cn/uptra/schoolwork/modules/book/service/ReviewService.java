package cn.uptra.schoolwork.modules.book.service;

import cn.uptra.schoolwork.modules.book.entity.Review;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ReviewService extends IService<Review> {

    // 从 mapper 抽象到 service：根据 bid 返回书本的平均评分
    Double getAverageRatingByBid(Long bid);

    // 根据 bid 返回书本的所有评论数量
    Integer getReviewCountByBid(Long bid);

    // 根据 bid 和 uid 查询用户是否已评论该书
    Integer hasUserReviewedBook(Long bid, Long uid);

    // 根据 bid 和 uid 查询用户的评论内容
    Review getUserReviewForBook(Long bid, Long uid);

    // 根据 bid 返回书本的所有评论，按时间排序
    List<Review> getAllReviewsByBid(Long bid);

    // 根据 uid 返回用户的所有评论
    List<Review> getAllReviewsByUid(Long uid);

}
