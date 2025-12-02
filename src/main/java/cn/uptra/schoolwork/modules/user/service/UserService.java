package cn.uptra.schoolwork.modules.user.service;


import cn.uptra.schoolwork.common.result.R;
import cn.uptra.schoolwork.modules.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;


public interface UserService extends IService<User> {
    public User getUserByUsername(String username);
    public User getUserByUid(Long uid);
    public boolean saveUser(User user);
    public boolean updateUserPassword(Long uid, String newPassword);
    public boolean updateUserRole(Long id, int role);
    public R<Object> getRegisterStatistics(String startDate, String endDate);
    R<Object> getActiveUserStatistics(String startDate, String endDate);

}
