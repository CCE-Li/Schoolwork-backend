package cn.uptra.schoolwork.modules.user.service.impl;

import cn.uptra.schoolwork.common.result.R;
import cn.uptra.schoolwork.modules.user.entity.User;
import cn.uptra.schoolwork.modules.user.mapper.UserMapper;
import cn.uptra.schoolwork.modules.user.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * 通过用户名获取用户
     * @param username
     * @return
     */
    @Override
    public User getUserByUsername(String username) {
        return this.lambdaQuery().eq(User::getUsername, username).one();
    }

    /**
     * 保存用户
     * @param user
     * @return
     */
    @Override
    public boolean saveUser(User user) {
        // 生成雪花uid
        Long snowflakeUid = IdWorker.getId();
        // 将生成的uid取模赋值给user的uid
        user.setUid(snowflakeUid);
        return this.save(user);
    }

    public User getUserByUid(Long uid) {
        return this.lambdaQuery().eq(User::getUid, uid).one();
    }

    /**
     * 更新用户密码
     * @param uid
     * @param newPassword
     * @return
     */
    @Override
    public boolean updateUserPassword(Long uid, String newPassword) {
        User user = this.getUserByUid(uid);
        if (user != null) {
            // 使用与注册时相同的加密方式存储新密码
            String encoded = passwordEncoder.encode(newPassword);
            user.setPassword(encoded);
            return this.updateById(user);
        }
        return false;
    }

    /**
     * 上传用户身份
     */
    @Override
    public boolean updateUserRole(Long uid, int role) {
        User user = this.getUserByUid(uid);
        if (user != null) {
            user.setStatus(role);
            return this.updateById(user);
        }
        return false;
    }

    /**
     * 获取注册用户统计
     * @param startDate
     * @param endDate
     * @return R<Object>
     */
    @Override
    public R<Object> getRegisterStatistics(String startDate, String endDate) {
        List<Map<String, Object>> list = this.baseMapper.getRegisterStatistics(startDate, endDate);
        return R.success(list);

    }

    /**
     * 用户活跃度统计
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public R<Object> getActiveUserStatistics(String startDate, String endDate) {
        List<Map<String, Object>> list = this.baseMapper.getActiveUserStatistics(startDate, endDate);
        return R.success(list);
    }
}
