package cn.uptra.schoolwork.modules.user.controller;


import cn.uptra.schoolwork.common.result.R;
import cn.uptra.schoolwork.common.security.CustomUserDetails;
import cn.uptra.schoolwork.modules.book.entity.Book;
import cn.uptra.schoolwork.modules.user.dto.UpdatePasswordRequest;
import cn.uptra.schoolwork.modules.user.entity.User;
import cn.uptra.schoolwork.modules.user.entity.UserFavourites;
import cn.uptra.schoolwork.modules.user.entity.UserProfiles;
import cn.uptra.schoolwork.modules.user.service.UserFavouriteService;
import cn.uptra.schoolwork.modules.user.service.UserProfileService;
import cn.uptra.schoolwork.modules.user.service.UserService;
import cn.uptra.schoolwork.modules.user.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserFavouriteService userFavouriteService;

    private static final int MIN_PASSWORD_LENGTH = 10;

    /**
     * 更新用户信息接口：（post请求，明文修改用户信息）
     * name、email、phone、address、prefer
     * 判断点：1.是否与原来相同 2.是否为空 3.格式是否正确 4.长度是否符合要求 5.偏好暂定
     * @param userProfiles 用户信息
     * @param uid 用户uid
     * @return 统一响应：成功返回用户信息，失败返回错误提示
     */
    @PostMapping("updatelist")
    public R<User> updateList(@RequestBody(required = false) UserProfiles userProfiles,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long uid = userDetails.getUId();
        User user = userService.getUserByUid(uid);
        if (user == null) {
            return R.error("用户不存在");
        } else {
            if (userProfiles == null) return R.error("请求体为空");

            // 如果当前用户还没有 user_profiles 记录，则新建一条
            UserProfiles nowProfiles = userProfileService.getUserByUid(uid);
            boolean isNew = false;
            if (nowProfiles == null) {
                nowProfiles = new UserProfiles();
                nowProfiles.setUid(uid);
                isNew = true;
            }

            String name = userProfiles.getName();
            String email = userProfiles.getEmail();
            String phone = userProfiles.getPhone();
            String address = userProfiles.getAddress();
            String prefer = userProfiles.getPrefer();
            if (name != null && !name.equals(nowProfiles.getName())) {
                nowProfiles.setName(name);
            }
            if (email != null && !email.equals(nowProfiles.getEmail())) { // email加入格式校验
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    return R.error("邮箱格式不正确");
                }
                nowProfiles.setEmail(email);
            }
            if (phone != null && !phone.equals(nowProfiles.getPhone()) && phone.length() <= 15) {
                nowProfiles.setPhone(phone);
            }
            if (address != null && !address.equals(nowProfiles.getAddress())) {
                nowProfiles.setAddress(address);
            }
            if (prefer != null && !prefer.equals(nowProfiles.getPrefer())) {
                nowProfiles.setPrefer(prefer);
            }
            boolean ok;
            if (isNew) {
                ok = userProfileService.save(nowProfiles);
            } else {
                ok = userProfileService.updateByUid(nowProfiles);
            }
            if (ok) {
                return R.success(user);
            } else {
                return R.error("更新用户信息失败");
            }
        }
    }

    /**
     * 修改用户信息接口： （post请求，明文修改用户信息）
     * 目前只支持修改密码，后续可扩展
     * @param req 前端传递的用户信息
     * @return  统一响应：成功返回用户信息，失败返回错误提示
     */
    @PostMapping("/update")
    public R<User> update(@RequestBody UpdatePasswordRequest req,
                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long uid = userDetails.getUId();
        if (req == null) {
            return R.error("请求体为空");
        }

        String newPassword = req.getPassword();
        if (!StringUtils.hasText(newPassword)) {
            return R.error("password 不能为空");
        }

        PasswordValidator validator = new PasswordValidator(MIN_PASSWORD_LENGTH);
        if (!validator.isStrong(newPassword)) {
            return R.error("密码不符合复杂度要求：长度至少 " + MIN_PASSWORD_LENGTH + " 位，且至少包含 大写、小写、数字、特殊字符 中的三类");
        }

        String username = req.getUsername();
        Long targetUid = null;

        if (StringUtils.hasText(username)) {
            User found = userService.getUserByUsername(username);
            if (found == null) return R.error("根据 username 未找到用户");
            targetUid = found.getUid();
        } else {
            if (uid == null) return R.error("未提供 username，且 uid 参数为空");
            User found = userService.getUserByUid(uid);
            if (found == null) return R.error("根据 uid 未找到用户");
            targetUid = found.getUid();
            if (targetUid == null) targetUid = uid;
        }

        boolean ok = userService.updateUserPassword(targetUid, newPassword);
        if (ok) return R.success();
        return R.error("修改密码失败");
    }

    /**
     * 返回用户收藏列表接口： （get请求，返回用户收藏列表）
     * @return  统一响应：成功返回收藏列表，失败返回错误提示
     */
    @GetMapping("/favourites")
    public List<Book> getFavourites(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long uid = userDetails.getUId();
        User user = userService.getUserByUid(uid);
        if (user == null) {
            return null;
        } else {
            List<UserFavourites> favourites = userFavouriteService.getUserFavouriteByUid(uid);
            List<Book> books = userFavouriteService.getFavouriteBooksByUid(uid);
            return books;
        }

    }


}
