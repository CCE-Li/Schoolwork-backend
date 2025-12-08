package cn.uptra.schoolwork.modules.user.service;

import cn.uptra.schoolwork.modules.user.entity.UserProfiles;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserProfileService extends IService<UserProfiles> {
    UserProfiles getUserByUid(Long uid);
    void setUserProfilesUid(Long uid);
    boolean updateByUid(UserProfiles userProfiles);
}
