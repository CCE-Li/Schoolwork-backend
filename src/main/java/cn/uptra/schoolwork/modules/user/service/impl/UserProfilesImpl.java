package cn.uptra.schoolwork.modules.user.service.impl;

import cn.uptra.schoolwork.modules.user.entity.UserProfiles;
import cn.uptra.schoolwork.modules.user.mapper.UserProfilesMapper;
import cn.uptra.schoolwork.modules.user.service.UserProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserProfilesImpl extends ServiceImpl<UserProfilesMapper, UserProfiles> implements UserProfileService {
    /**
     * 通过uid获取用户信息
     * @param uid
     * @return
     */
    @Override
    public UserProfiles getUserByUid(Long uid) {
        return this.baseMapper.getUserProfilesByUid(uid);
    }

    /**
     * 为用户信息表设置uid
     * @param uid
     */
    @Override
    public void setUserProfilesUid(Long uid) {
        UserProfiles userProfiles = new UserProfiles();
        userProfiles.setUid(uid);
        this.save(userProfiles);
    }

    @Override
    public boolean updateByUid(UserProfiles userProfiles) {
        return this.baseMapper.updateByUid(userProfiles);
    }
}
