package cn.uptra.schoolwork.modules.user.mapper;

import cn.uptra.schoolwork.modules.user.entity.UserProfiles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserProfilesMapper extends BaseMapper<UserProfiles> {
    @Select("""
            SELECT * FROM user_profiles WHERE uid = #{uid}
            """)
    UserProfiles getUserProfilesByUid(Long uid);

    @Update("""
            UPDATE user_profiles
            SET name = #{name},
                email = #{email},
                phone = #{phone},
                address = #{address},
                prefer = #{prefer}
            WHERE uid = #{uid}
            """)
    boolean updateByUid(UserProfiles userProfiles);
}
