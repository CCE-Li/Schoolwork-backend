package cn.uptra.schoolwork.modules.admin.controller;

import cn.uptra.schoolwork.common.result.R;
import cn.uptra.schoolwork.modules.user.entity.User;
import cn.uptra.schoolwork.modules.user.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Admin - 用户管理")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "分页查询用户列表")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Page<User>> listUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return R.success(userService.page(page, null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    @PreAuthorize("hasRole('ADMIN')")
    public R<User> getUserById(@PathVariable Long id) {
        return R.success(userService.getById(id));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userService.updateById(user);
        return R.success();
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "更新用户角色")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> updateUserRole(
            @PathVariable Long id,
            @RequestParam int status) {
        userService.updateUserRole(id, status);
        return R.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return R.success();
    }

    @GetMapping("/statistics/register")
    @Operation(summary = "获取用户注册统计")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Object> getRegisterStatistics(
            // 将两个日期变成非必须参数
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        // 设置默认日期范围（如果未提供）
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1); // 默认查询最近一个月
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        // 将LocalDate转换为字符串格式，格式为yyyy-MM-dd
        String startDateStr = startDate.toString();
        String endDateStr = endDate.plusDays(1).toString(); // 包含结束日期当天的数据
        return R.success(userService.getRegisterStatistics(startDateStr, endDateStr));
    }

    @GetMapping("/statistics/active")
    @Operation(summary = "获取活跃用户统计")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Object> getActiveUserStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        // 设置默认日期范围（如果未提供）
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1); // 默认查询最近一个月
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        // 将LocalDate转换为字符串格式，格式为yyyy-MM-dd
        String startDateStr = startDate.toString();
        String endDateStr = endDate.plusDays(1).toString(); // 包含结束日期当天的数据
        return R.success(userService.getActiveUserStatistics(startDateStr, endDateStr));
    }
}
