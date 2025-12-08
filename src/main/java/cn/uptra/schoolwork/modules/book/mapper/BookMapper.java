package cn.uptra.schoolwork.modules.book.mapper;

import cn.uptra.schoolwork.modules.book.entity.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
    @Select("SELECT * FROM books WHERE bid = #{bid} ")
    List<Book> getBookByBid(Integer bid);

    // 通过标题、标签、作者模糊查询书籍
    @Select("SELECT * FROM books WHERE title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR tags LIKE CONCAT('%', #{keyword}, '%') " +
            "OR author LIKE CONCAT('%', #{keyword}, '%')")
    List<Book> searchBooksByKeyword(String keyword);

    @Select("<script>" +
            "SELECT * FROM books " +
            "<where> " +
            "<if test='author != null'> " +
            "AND author LIKE CONCAT('%', #{author}, '%') " +
            "</if> " +
            "<if test='title != null'> " +
            "AND title LIKE CONCAT('%', #{title}, '%') " +
            "</if> " +
            "<if test='tags != null'> " +
            "AND tags LIKE CONCAT('%', #{tags}, '%') " +
            "</if> " +
            "</where>" +
            "</script>")
    List<Book> listBooks(String author, String title, String tags);

     @Select("<script>" +
            "SELECT * FROM books WHERE 1=1 " +
            "<if test='title != null'> AND title LIKE CONCAT('%', #{title}, '%') </if>" +
            "<if test='author != null'> AND author LIKE CONCAT('%', #{author}, '%') </if>" +
            "<if test='tagList != null'>" +
            "  <foreach collection='tagList' item='tag' open=' AND (' separator=' OR ' close=')'>" +
            "    tags LIKE CONCAT('%', #{tag}, '%')" +
            "  </foreach>" +
            "</if>" +
            " ORDER BY id DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Book> selectBooks(@Param("offset") int offset, 
                           @Param("pageSize") int pageSize,
                           @Param("title") String title, 
                           @Param("author") String author,
                           @Param("tagList") List<String> tagList);

    @Select("<script>" +
            "SELECT COUNT(*) FROM books WHERE 1=1 " +
            "<if test='title != null'> AND title LIKE CONCAT('%', #{title}, '%') </if>" +
            "<if test='author != null'> AND author LIKE CONCAT('%', #{author}, '%') </if>" +
            "<if test='tagList != null'>" +
            "  <foreach collection='tagList' item='tag' open=' AND (' separator=' OR ' close=')'>" +
            "    tags LIKE CONCAT('%', #{tag}, '%')" +
            "  </foreach>" +
            "</if>" +
            "</script>")
    int countBooks(@Param("title") String title, 
                   @Param("author") String author,
                   @Param("tagList") List<String> tagList);
}
