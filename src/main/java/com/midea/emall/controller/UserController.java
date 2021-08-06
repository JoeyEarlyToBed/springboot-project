package com.midea.emall.controller;


import com.midea.emall.common.ApiRestResponse;
import com.midea.emall.common.Constant;
import com.midea.emall.exception.MideaMallException;
import com.midea.emall.exception.MideaMallExceptionEnum;
import com.midea.emall.model.pojo.User;
import com.midea.emall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

/*
描述：用户控制器
 */
@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/test")
    @ResponseBody
    public User personalPage() {
        User user = userService.getUser();
        return user;
    }

    @ResponseBody
    @PostMapping("/register")
    public ApiRestResponse register(@RequestParam("username") String username, String password) throws MideaMallException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(MideaMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MideaMallExceptionEnum.NEED_PASSWORD);
        }
        //密码长度不能少于8位
        if (password.length() < 8) {
            return ApiRestResponse.error(MideaMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(username, password);
        return ApiRestResponse.success();
    }

    @ResponseBody
    @PostMapping("/login")
    public ApiRestResponse login(@RequestParam("username") String userName,
                                 @RequestParam("password") String password,
                                 HttpSession session) throws MideaMallException {
        if (StringUtils.isEmpty(userName)) {
            throw new MideaMallException(MideaMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            throw new MideaMallException(MideaMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        //保存用户信息时，不保存密码（安全保证）
        user.setPassword(null);
        session.setAttribute(Constant.MIDEA_MALL_USER, user);
        return ApiRestResponse.success(user);
    }


    @ResponseBody
    @PostMapping("/user/update")
    public ApiRestResponse updateUserInfo(HttpSession session , @RequestParam("signature") String signature) throws MideaMallException {
        User currentUser = (User) session.getAttribute(Constant.MIDEA_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(MideaMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setPersonalizedSignature(signature);
        user.setId(currentUser.getId());
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(Constant.MIDEA_MALL_USER);
        return ApiRestResponse.success();
    }


    @ResponseBody
    @PostMapping("/admin/login")
    public ApiRestResponse adminLogin(@RequestParam("username") String userName,
                                      @RequestParam("password") String password,
                                 HttpSession session) throws MideaMallException {
        if (StringUtils.isEmpty(userName)) {
            throw new MideaMallException(MideaMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            throw new MideaMallException(MideaMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        //保存用户信息时，不保存密码（安全保证）

        if (userService.checkAdminRole(user)){
            user.setPassword(null);
            session.setAttribute(Constant.MIDEA_MALL_USER, user);
            return ApiRestResponse.success(user);
        }else{
            return ApiRestResponse.error(MideaMallExceptionEnum.NEED_ADMIN);
        }
    }

}
