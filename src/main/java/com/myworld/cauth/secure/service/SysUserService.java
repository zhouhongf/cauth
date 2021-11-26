package com.myworld.cauth.secure.service;

import com.myworld.cauth.secure.data.entity.SysUser;
import com.myworld.cauth.common.model.ApiResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface SysUserService {
    ApiResult doesUserExists(String username);
    ApiResult register(String username, String smsCode, String password, HttpServletRequest request, HttpServletResponse response);
    ApiResult createSysUser(String username, String password, String creator);
    ApiResult createUserInfo(SysUser sysUser);


    ApiResult changeUsername(String usernameold, String username, String smsCode, String idnumber, HttpServletRequest request, HttpServletResponse response);
    ApiResult changePassword(String passwordold, String password, HttpServletRequest request, HttpServletResponse response);
    ApiResult resetPassword(String username, String password);


    //以下为 admin管理部分
    ApiResult adminCreateUser(String username, String password, HttpServletRequest request, HttpServletResponse response);
    ApiResult adminDelUser(String idDetail);
    ApiResult adminGetAdminList() throws IOException;
    ApiResult adminSetAdmin(String username, String adminPower, String password);
    ApiResult adminDelAdmin(String idDetail);

    // 以下为feign调用部分
    String tokenToUsername(String token);
    String tokenToUserIdDetail(String token);
    String tokenToSimpleUser(String token);
}
