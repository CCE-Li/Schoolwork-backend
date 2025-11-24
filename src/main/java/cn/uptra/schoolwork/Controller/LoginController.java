package cn.uptra.schoolwork.Controller;

import cn.uptra.schoolwork.Model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        // 这里可以添加实际的认证逻辑，例如检查用户名和密码是否正确
        if ("admin".equals(user.getUsername()) && "password".equals(user.getPassword())) {
            return "登录成功";
        } else {
            return "用户名或密码错误";
        }
    }
}
