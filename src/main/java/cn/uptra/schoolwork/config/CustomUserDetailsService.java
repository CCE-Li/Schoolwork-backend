package cn.uptra.schoolwork.config;

import cn.uptra.schoolwork.common.security.CustomUserDetails;
import cn.uptra.schoolwork.modules.user.entity.User;
import cn.uptra.schoolwork.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 获取用户角色，这里假设用户有一个role字段表示角色
        // 如果没有role字段，可以默认给一个ROLE_USER角色
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 这里需要根据您的实际角色字段进行调整
        if (user.getStatus() != null && user.getStatus() == 1) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // 返回自定义的UserDetails对象，包含用户UID
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                true, // 账号是否启用
                true, // 账号是否未过期
                true, // 凭证是否未过期
                true, // 账号是否未锁定
                authorities, // 用户权限
                user.getUid() // 用户UID
        );
    }
}
