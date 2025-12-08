package cn.uptra.schoolwork.modules.book.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@TableName("reviews")
public class Review {
    @TableId("id")
    private Integer id;
    private Long uid;
    private Long bid;
    private Integer rating;
    private String comment;
    private LocalDateTime review_time;
}
