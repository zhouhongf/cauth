package com.myworld.cauth.secure.controller;


import com.myworld.cauth.secure.security.redis.JwtCheckService;
import com.myworld.cauth.secure.service.SysUserService;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.properties.SecurityConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class SysUserController {
    private static Logger log = LogManager.getRootLogger();
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtCheckService jwtCheckService;

    /**
     * 用于忘记密码，短信登录时，异步查询输入的手机号是否存在
     */
    @GetMapping("/users")
    public ApiResult doesUserExists(@RequestParam String username) {
        return sysUserService.doesUserExists(username);
    }
    /**
     *  用户注册
     *  /register
     */
    @PostMapping(SecurityConstants.DEFAULT_REGISTER_URL)
    public ApiResult register(@RequestParam("mobile") String mobile, @RequestParam("smsCode") String smsCode, @RequestBody String password, HttpServletRequest request, HttpServletResponse response) {
        return sysUserService.register(mobile, smsCode, password, request, response);
    }


    /**
     * 变更用户账号，即手机号
     * 内部feign操作
     */
    @PostMapping("/doChangeUsername")
    public ApiResult changeUsername(@RequestParam String usernameold, @RequestParam String mobile, @RequestParam String smsCode, @RequestBody String idnumber, HttpServletRequest request, HttpServletResponse response) {
        return sysUserService.changeUsername(usernameold, mobile, smsCode, idnumber, request, response);
    }
    /**
     * 修改密码
     * 内部feign操作
     */
    @PostMapping("/doChangePassword")
    public ApiResult changePassword(@RequestParam String passwordold, @RequestBody String password, HttpServletRequest request, HttpServletResponse response) {
        return sysUserService.changePassword(passwordold, password, request, response);
    }
    /**
     * 忘记密码，重置密码
     * 内部feign操作
     */
    @PostMapping("/doResetPassword")
    public ApiResult resetPassword(@RequestParam String username, @RequestBody String password) {
        log.info("要重置的password是：{}", password);
        return sysUserService.resetPassword(username, password);
    }


    /**
     *  检查并更新令牌，返回refreshToken
     *  内部feign操作
     */
    @GetMapping("/checkAndRefreshJwtToken/{token}")
    public String checkAndRefreshJwtToken(@PathVariable String token) {
        return jwtCheckService.checkAndRefreshJwtToken(token);
    }

    @GetMapping("/checkJwtAndRefreshCurrentUser/{token}")
    public String checkJwtAndRefreshCurrentUser(@PathVariable String token) {
        return jwtCheckService.checkJwtAndRefreshCurrentUser(token);
    }

    @PostMapping("/adminCreateUser")
    public ApiResult adminCreateUser(@RequestParam String username, @RequestBody String password, HttpServletRequest request, HttpServletResponse response) {
        return sysUserService.adminCreateUser(username, password, request, response);
    }

    @PostMapping("/adminDelUser")
    public ApiResult adminDelUser(@RequestBody String idDetail) {
        return sysUserService.adminDelUser(idDetail);
    }


    @GetMapping("/adminGetAdminList")
    public ApiResult adminGetAdminList() throws IOException {
        return sysUserService.adminGetAdminList();
    }

    @PostMapping("/adminSetAdmin")
    public ApiResult adminSetAdmin(@RequestParam String username, @RequestParam String adminPower, @RequestBody String password) {
        return sysUserService.adminSetAdmin(username, adminPower, password);
    }

    @PostMapping("/adminDelAdmin")
    public ApiResult adminDelAdmin(@RequestBody String idDetail) {
        return sysUserService.adminDelAdmin(idDetail);
    }


    /**
     * 内部feign操作
     */
    @PostMapping("/tokenToUsername")
    public String tokenToUsername(@RequestBody String token) {
        return sysUserService.tokenToUsername(token);
    }

    @PostMapping("/tokenToUserIdDetail")
    public String tokenToUserIdDetail(@RequestBody String token) {
        return sysUserService.tokenToUserIdDetail(token);
    }

    @PostMapping("/tokenToSimpleUser")
    public String tokenToSimpleUser(@RequestBody String token) {
        return sysUserService.tokenToSimpleUser(token);
    }
}
