package cn.uptra.schoolwork.modules.user.mapper;


import cn.uptra.schoolwork.modules.user.entity.User;
import cn.uptra.schoolwork.modules.user.entity.UserProfiles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 获取注册用户统计
     * @param startDate
     * @param endDate
     * @return
     */
    @Select("SELECT COUNT(*) AS total, DATE_FORMAT(date_created, '%Y-%m-%d') AS date_created\n" +
            "FROM users\n" +
            "WHERE date_created BETWEEN #{startDate} AND #{endDate}\n" +
            "GROUP BY date_created")
    List<Map<String, Object>> getRegisterStatistics(String startDate, String endDate);

    /**
     * 获取活跃用户统计
     * @param startDate
     * @param endDate
     * @return
     */
    @Select("SELECT COUNT(*) AS total, DATE_FORMAT(date_modified, '%Y-%m-%d') AS date_modified\n" +
            "FROM users\n" +
            "WHERE date_modified BETWEEN #{startDate} AND #{endDate}\n" +
            "GROUP BY date_modified")
    List<Map<String, Object>> getActiveUserStatistics(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
