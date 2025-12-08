package cn.uptra.schoolwork.modules.book.mapper;

import cn.uptra.schoolwork.modules.book.entity.Review;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    // 根据bid返回书本的平均评分
    @Select("SELECT AVG(rating) FROM reviews WHERE bid = #{bid} ")
    Double getAverageRatingByBid(Integer bid);

    // 根据bid返回书本的所有评论数量
    @Select("SELECT COUNT(*) FROM reviews WHERE bid = #{bid} ")
    Integer getReviewCountByBid(Integer bid);

    // 根据bid和uid查询用户是否已评论该书
    @Select("SELECT COUNT(*) FROM reviews WHERE bid = #{bid} AND uid = #{uid} ")
    Integer hasUserReviewedBook(Integer bid, Long uid);

    // 根据bid和uid查询用户的评论内容
    @Select("SELECT * FROM reviews WHERE bid = #{bid} AND uid = #{uid} ")
    Review getUserReviewForBook(Integer bid, Long uid);

    // 根据bid返回书本的所有评论，按时间排序
    @Select("SELECT * FROM reviews WHERE bid = #{bid} ORDER BY review_time DESC ")
    List<Review> getAllReviewsByBid(Integer bid);

    // 根据uid返回用户的所有评论
    @Select("SELECT * FROM reviews WHERE uid = #{uid} ")
    List<Review> getAllReviewsByUid(Long uid);
}
