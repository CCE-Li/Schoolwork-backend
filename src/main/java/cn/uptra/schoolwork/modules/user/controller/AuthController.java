package cn.uptra.schoolwork.modules.user.controller;

import cn.uptra.schoolwork.common.result.R;
import cn.uptra.schoolwork.modules.user.entity.User;
import cn.uptra.schoolwork.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import cn.uptra.schoolwork.common.jwt.JwtUtil;

@RestController
@RequestMapping("/api/auth") // 前端接口
public class AuthController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;


    /**
     * 登录接口 (post请求，明文验证uid和密码)
     * @param user 前端传递的uid + 密碼
     * @return 统一响应：成功返回token，失败返回错误提示
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        // 直接查询管理员用户
        if (username.equals("admin") && password.equals("admin123")) {
            return R.success("Bearer " + jwtUtil.generateToken(username, "ADMIN"));
        }
        // 从数据库查询登录用户
        User dbUser = userService.getUserByUsername(username);
        if (dbUser == null) {
            return R.error("用户不存在");
        }
        else if (!passwordEncoder.matches(password, dbUser.getPassword())) {
            return R.error("密码错误");
        } else if (dbUser.getStatus() == 0)  {
            return R.error("此用户被禁用，请练习管理员");
        }else {
            return R.success(user,"Bearer " + jwtUtil.generateToken(username, "USER"));
        }
    }
    
    /**
     * 用户注册接口
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/regist")
    public R<User> register(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userService.getUserByUsername(user.getUsername()) != null) {
            return R.error("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 生成并设置 uid
        user.setUid(System.currentTimeMillis());
        
        // 保存用户
        boolean result = userService.save(user);
        if (result) {
            return R.success("注册成功");
        } else {
            return R.error("注册失败");
        }
    }
}
