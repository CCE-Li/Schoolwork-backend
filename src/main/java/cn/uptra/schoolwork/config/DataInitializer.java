package cn.uptra.schoolwork.config;

import cn.uptra.schoolwork.modules.user.entity.User;
import cn.uptra.schoolwork.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 检查是否已存在admin用户
        if (userService.getUserByUsername("admin") == null) {
            log.info("Creating default admin user...");
            User admin = new User();
            admin.setUsername("admin");
            // 默认密码为admin123，建议在首次登录后修改
            admin.setPassword(passwordEncoder.encode("admin123"));
            // 设置状态为1表示启用
            admin.setStatus(1);
            // 生成一个随机的UID
            admin.setUid(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
            
            if (userService.save(admin)) {
                log.info("Default admin user created successfully!");
                log.info("Username: admin");
                log.info("Password: admin123");
                log.warn("Please change the default password after first login!");
            } else {
                log.error("Failed to create default admin user!");
            }
        }
    }
}
