package cn.uptra.schoolwork.modules.admin.controller;

import cn.uptra.schoolwork.modules.admin.entity.Admin;
import cn.uptra.schoolwork.modules.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.uptra.schoolwork.common.result.R;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     * @param admin
     * @return
     */
    @PostMapping("/login")
    public R<Admin> login(@RequestBody   Admin admin) {
        Admin dbAdmin = adminService.getByUsername(admin.getUsername());
        if (dbAdmin != null && dbAdmin.getPassword().equals(admin.getPassword())) {
            return R.success(dbAdmin);
        } else {
            return R.error("Invalid username or password");
        }
    }

    /**
     * 获取管理员信息
     * @param adminId
     * @return
     */
    @PostMapping("/info")
    public R<Admin> getAdminInfo(@RequestBody Long adminId) {
        Admin admin = adminService.getById(adminId);
        if (admin != null) {
            return R.success(admin);
        } else {
            return R.error("Admin not found");
        }
    }
}
