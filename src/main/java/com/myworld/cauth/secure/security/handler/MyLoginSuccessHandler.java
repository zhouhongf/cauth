package com.myworld.cauth.secure.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.myworld.cauth.config.SecurityProperties;
import com.myworld.cauth.secure.data.entity.SysAdmin;
import com.myworld.cauth.secure.data.entity.SysUser;
import com.myworld.cauth.secure.data.model.CurrentAdmin;
import com.myworld.cauth.secure.data.model.CurrentUser;
import com.myworld.cauth.secure.data.repository.SysAdminRepository;
import com.myworld.cauth.secure.data.repository.SysUserRepository;
import com.myworld.cauth.secure.security.MyUserKeyService;
import com.myworld.cauth.secure.security.TokenKeyGenerator;
import com.myworld.cauth.common.*;
import com.myworld.cauth.common.model.LoginType;
import com.myworld.cauth.common.properties.SecurityConstants;
import com.myworld.cauth.common.util.JwtUtil;
import com.myworld.cauth.common.util.ResultUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

import java.util.*;


/**
 * 登录成功处理器
 */
@Component("myLoginSuccessHandler")
public class MyLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
    private static Logger log = LogManager.getRootLogger();
    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysAdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UtilService utilService;

    /**
     * 登录成功处理器
     * 用户点击注销，即向服务器发出销毁token的请求，原token放入redis黑名单，key为token,value为"user has logout"
     * 用户登录成功后，重新生成token
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> auths = userDetails.getAuthorities();
        Iterator it = auths.iterator();
        while(it.hasNext()){
            String role = it.next().toString();
            if(role.equals("ADMIN")){
                this.makeCurrentAdmin(role, request, response, authentication);
            }else{
                this.makeCurrentUser(role, request, response, authentication);
            }
        }
    }

    /**
     * 返回非完整的currentAdmin信息
     * 关键是返回token给前端，供前端携带token跳转至登录后的页面
     * 再在登录后的页面初始化的过程中，根据对应的服务，获得完整的cunrrentAdmin信息
     */
    public void makeCurrentAdmin(String role, HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        SysAdmin adminLogin = (SysAdmin) authentication.getPrincipal();

        adminLogin.setLastLoginTime(timestamp);
        adminLogin.setLastLoginIp(this.utilService.getIpAddress(request));
        adminRepository.save(adminLogin);

        List<String> serviceNames = new ArrayList<>();
        serviceNames.add(SecurityConstants.SERVICE_AUTH);
        serviceNames.add(SecurityConstants.SERVICE_WEALTH);
        serviceNames.add(SecurityConstants.SERVICE_CHAT);
        serviceNames.add(SecurityConstants.SERVICE_SSTUDENT);
        // String adminIdDetail = MyUserKeyService.encodeUserIdDetail(adminLogin.getIdDetail());
        String adminIdDetail = adminLogin.getIdDetail();

        Long expireTime = new Date().getTime() + securityProperties.getAccessTokenValidSeconds() * 1000;
        String theToken = jwtUtil.generateToken(SecurityConstants.DEFAULT_JWT_ACCESS_TOKEN, serviceNames, role, adminIdDetail, expireTime, TokenKeyGenerator.getPrivateKey());

        CurrentAdmin cadmin = new CurrentAdmin();
        cadmin.setToken(theToken);
        cadmin.setAdminPower(adminLogin.getAdminPower());
        cadmin.setWid(adminIdDetail);
        cadmin.setExpireTime(String.valueOf(expireTime));
        String cadminStr = JSONObject.toJSONString(cadmin);
        String cadminStrEncode = MyUserKeyService.aesEncrypt(cadminStr);
        log.info("【cadmin是：{}】", cadminStr);
        log.info("【加密后的cadmin是：{}】", cadminStrEncode);

        if (LoginType.JSON.equals(securityProperties.getLoginType())) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONObject.toJSONString(ResultUtil.success(cadminStrEncode)));
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    /**
     * 返回非完整的currentUser信息
     * 关键是返回token给前端，供前端携带token跳转至登录后的页面
     * 再在登录后的页面初始化的过程中，根据对应的服务，获得完整的cunrrentUser信息
     */
    public void makeCurrentUser(String role, HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        SysUser userlogin = (SysUser) authentication.getPrincipal();

        userlogin.setLastLoginTime(timestamp);
        userlogin.setLastLoginIp(this.utilService.getIpAddress(request));
        sysUserRepository.save(userlogin);

        List<String> serviceNames = new ArrayList<>();
        serviceNames.add(SecurityConstants.SERVICE_AUTH);
        serviceNames.add(SecurityConstants.SERVICE_WEALTH);
        serviceNames.add(SecurityConstants.SERVICE_CHAT);
        serviceNames.add(SecurityConstants.SERVICE_SSTUDENT);
        String userIdDetail = userlogin.getIdDetail();
        // String userIdDetail = MyUserKeyService.encodeUserIdDetail(userlogin.getIdDetail());

        Long expireTime = new Date().getTime() + securityProperties.getAccessTokenValidSeconds() * 1000;
        String theToken = jwtUtil.generateToken(SecurityConstants.DEFAULT_JWT_ACCESS_TOKEN, serviceNames, role, userIdDetail, expireTime, TokenKeyGenerator.getPrivateKey());

        CurrentUser cuser = new CurrentUser();
        cuser.setToken(theToken);
        cuser.setPlayerType(role);
        cuser.setWid(userIdDetail);
        cuser.setExpireTime(String.valueOf(expireTime));
        String cuserStr = JSONObject.toJSONString(cuser);
        String cuserStrEncode = MyUserKeyService.aesEncrypt(cuserStr);
        log.info("【cuer是：{}】", cuserStr);
        log.info("【加密后的cuer是：{}】", cuserStrEncode);

        if (LoginType.JSON.equals(securityProperties.getLoginType())) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONObject.toJSONString(ResultUtil.success(cuserStrEncode)));
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }


}
