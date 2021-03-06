package com.myworld.cauth.secure.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myworld.cauth.secure.data.entity.SysAdmin;
import com.myworld.cauth.secure.data.entity.SysRole;
import com.myworld.cauth.secure.data.entity.SysUser;
import com.myworld.cauth.secure.data.entity.UserInfo;
import com.myworld.cauth.secure.data.model.CurrentUser;
import com.myworld.cauth.secure.data.repository.IdPhotoRepository;
import com.myworld.cauth.secure.data.repository.SysAdminRepository;
import com.myworld.cauth.secure.data.repository.SysUserRepository;
import com.myworld.cauth.secure.data.repository.UserInfoRepository;
import com.myworld.cauth.secure.security.MyUserKeyService;
import com.myworld.cauth.secure.security.redis.JwtCheckService;
import com.myworld.cauth.secure.security.validateCode.sms.SmsCodeProcessor;
import com.myworld.cauth.common.*;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.model.SimpleUser;
import com.myworld.cauth.common.properties.SecurityConstants;
import com.myworld.cauth.common.util.JwtUtil;
import com.myworld.cauth.common.util.ResultUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Service
public class SysUserServiceImpl implements SysUserService {
    private static Logger log = LogManager.getRootLogger();
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private IdPhotoRepository idPhotoRepository;
    @Autowired
    private SmsCodeProcessor smsCodeProcessor;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private JwtCheckService jwtCheckService;
    @Autowired
    private SysAdminRepository sysAdminRepository;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UtilService utilService;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public ApiResult doesUserExists(String username){
        String theUsername = MyUserKeyService.getRealUsername(username);
        SysUser sysUser = sysUserRepository.findByUsername(theUsername);
        SysAdmin sysAdmin = sysAdminRepository.findByUsername(theUsername);
        if(sysUser != null || sysAdmin != null){
            log.info("????????????");
            return ResultUtil.success("????????????");
        }else {
            return ResultUtil.error(-2, "???????????????");
        }
    }

    /**
     * ??????SysUser???UserInfo
     */
    @Override
    public ApiResult register(String username, String smsCode, String password, HttpServletRequest request, HttpServletResponse response){
        //??????????????????request?????????ServletWebRequest???????????????smsCodeProcessor???validate()
        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
        ApiResult apiResult = smsCodeProcessor.validate(servletWebRequest);
        if (apiResult.getCode() != 0){
            return ResultUtil.error(-2, "?????????????????????");
        }
        //?????? ???????????????????????????
        String realUsername = MyUserKeyService.getRealUsername(username);
        SysUser user =  sysUserRepository.findByUsername(realUsername);
        SysAdmin admin = sysAdminRepository.findByUsername(realUsername);
        if (user != null || admin != null){
            return ResultUtil.error(-2, "????????????????????????????????????");
        }

        String realPassword = MyUserKeyService.getRealPassword(password);
        return this.createSysUser(realUsername, realPassword, realUsername);
    }

    /**
     * ???????????????jingrongbank?????????
     * ??????????????????????????????????????????????????????
     */
    @Override
    public ApiResult createSysUser(String realUsername, String realPassword, String creator) {
        String theRandom = String.valueOf((int)((Math.random()*9 + 1) * 1000));
        String idDetail = SecurityConstants.USER_ID_DETAIL_PREFIX + new Date().getTime() + theRandom;
        SysUser sysUser = new SysUser();
        sysUser.setCreator(creator);
        sysUser.setUpdater(creator);

        sysUser.setIdDetail(idDetail);
        sysUser.setUsername(realUsername);
        String thePassword = passwordEncoder.encode(realPassword);
        sysUser.setPassword(thePassword);

        //??????????????????SysRole
        Set<SysRole> sysRoles = sysRoleService.updateRole("CLIENT");
        sysUser.setRoles(sysRoles);
        sysUserRepository.save(sysUser);
        return this.createUserInfo(sysUser);
    }

    @Override
    public ApiResult createUserInfo(SysUser sysUser) {
        UserInfo userInfo = new UserInfo();
        userInfo.setIdDetail(sysUser.getIdDetail());
        userInfo.setCreator(sysUser.getCreator());
        userInfo.setUpdater(sysUser.getUpdater());
        userInfo.setCreateTime(sysUser.getCreateTime());
        userInfo.setUpdateTime(sysUser.getUpdateTime());
        userInfo.setUsername(sysUser.getUsername());
        userInfo.setPlayerType("CLIENT");
        userInfo.setAccountStatus("?????????");
        userInfo.setCreditLevel("??????");
        userInfo.setCreditLevelConfirm("??????CCCCC");
        userInfoRepository.save(userInfo);
        return ResultUtil.success();
    }



    /**
     * ??????SysUser???UserInfo
     * ?????????????????????????????????UserInfo??????idnumber, username?????????
     * ??????????????????????????????????????????????????????
     */
    @Override
    public ApiResult changeUsername(String usernameold, String username, String smsCode, String idnumber, HttpServletRequest request, HttpServletResponse response){
        Timestamp timestamp = new Timestamp(new Date().getTime());
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();

        //1????????????????????????
        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
        ApiResult apiResult = smsCodeProcessor.validate(servletWebRequest);
        if (apiResult.getCode() != 0){
            return ResultUtil.error(-2, "?????????????????????");
        }
        //2???????????????????????????????????????????????????
        SysUser user = sysUserRepository.findByUsername(username);
        SysAdmin admin = sysAdminRepository.findByUsername(username);
        if (user != null || admin != null){
            return ResultUtil.error(-2, "?????????????????????");
        }

        //3?????????idDetail??????Sysuser???username, ?????????????????????usernameold????????????
        SysUser sysUser = sysUserRepository.findByIdDetail(idDetail);
        if (sysUser == null) {
            return ResultUtil.error(-2, "????????????????????????????????????");
        }
        String theUsername = sysUser.getUsername();
        usernameold = MyUserKeyService.getRealUsername(usernameold);
        username = MyUserKeyService.getRealUsername(username);
        if (!theUsername.equals(usernameold)) {
            return ResultUtil.error(-2, "?????????????????????");
        }
        //4???????????????????????????????????????UserInfo????????????username, ??????????????????usernameold????????????
        idnumber = idnumber.replace(" ", "+");
        String realIdnumber = MyUserKeyService.aesDecrypt(idnumber);
        UserInfo userInfo = userInfoRepository.findByIdnumber(realIdnumber);
        if (userInfo == null) {
            return ResultUtil.error(-2, "???????????????????????????????????????");
        }
        String usernameSave = userInfo.getUsername();
        if (!usernameSave.equals(usernameold)){
            return ResultUtil.error(-2, "?????????????????????");
        }

        // ????????????????????????????????????sysuser???userInfo???
        sysUser.setUsernameold(usernameold);
        sysUser.setUsername(username);
        sysUser.setUpdater(username);
        sysUser.setUpdateTime(timestamp);
        sysUserRepository.save(sysUser);

        userInfo.setUsername(username);
        userInfo.setUpdater(username);
        userInfo.setUpdateTime(timestamp);
        userInfoRepository.save(userInfo);

        String token = request.getHeader(JwtUtil.HEADER_AUTH);
        userInfoService.syncPhoneUser(userInfo, token);
        return ResultUtil.success();
    }

    /**
     * ??????SysUser
     * ???????????????jingrongbank?????????
     * ??????????????????????????????????????????????????????
     */
    @Override
    public ApiResult changePassword(String passwordold, String password, HttpServletRequest request, HttpServletResponse response){
        Timestamp timestamp = new Timestamp(new Date().getTime());
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();
        SysUser sysUser = sysUserRepository.findByIdDetail(idDetail);
        if (sysUser == null) {
            return ResultUtil.error(-2, "????????????????????????????????????");
        }

        // ??????passwordold??????????????????passwordSave????????????
        String usernameSave = sysUser.getUsername();
        String passwordSave = sysUser.getPassword();
        passwordold = MyUserKeyService.getRealPassword(passwordold);
        password = MyUserKeyService.getRealPassword(password);
        if (passwordEncoder.matches(passwordold, passwordSave)) {
            log.info("?????????????????????????????????????????????");
            String encodePassword = passwordEncoder.encode(password);
            sysUser.setPasswordold(passwordSave);
            sysUser.setPassword(encodePassword);
            sysUser.setUpdater(usernameSave);
            sysUser.setUpdateTime(timestamp);
            sysUserRepository.save(sysUser);
            return ResultUtil.success();
        } else {
            return ResultUtil.error(-2, "??????????????????");
        }
    }

    /**
     * ??????SysUser
     * ???????????????jingrongbank?????????
     * ??????????????????????????????????????????????????????
     */
    @Override
    public ApiResult resetPassword(String username, String password){
        Timestamp timestamp = new Timestamp(new Date().getTime());
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();
        SysUser sysUser = sysUserRepository.findByIdDetail(idDetail);
        if (sysUser == null) {
            return ResultUtil.error(-2, "????????????");
        }
        // 1?????????Sysuser??????username????????????????????????username???????????????????????????????????????????????????
        String realUsername = MyUserKeyService.getRealUsername(username);
        String usernameSave = sysUser.getUsername();
        if (!usernameSave.equals(realUsername)) {
            return ResultUtil.error(-2, "??????????????????");
        }

        // 2???????????????????????????????????????5???????????????????????????????????????5????????????????????????
        Timestamp lastLoginTime = sysUser.getLastLoginTime();
        long validTime = lastLoginTime.getTime() + 1000 * 60 * 5;
        long currentTime = timestamp.getTime();
        log.info("????????????????????????validTime???: {}", validTime);
        log.info("????????????currentTime???: {}", currentTime);
        if (currentTime > validTime) {
            return ResultUtil.error(-2, "????????????????????????????????????");
        }

        String realPassword = MyUserKeyService.getRealPassword(password);
        log.info("?????????password???: {}", realPassword);
        String encodePassword = passwordEncoder.encode(realPassword);
        log.info("????????????????????????encodePassword???: {}", encodePassword);

        // ??????????????????????????????Sysuser
        sysUser.setPasswordold(sysUser.getPassword());
        sysUser.setPassword(encodePassword);
        sysUser.setUpdater(usernameSave);
        sysUser.setUpdateTime(timestamp);
        sysUserRepository.save(sysUser);
        return ResultUtil.success();
    }

    //?????????admin????????????
    @Override
    public ApiResult adminCreateUser(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();
        //?????? ???????????????????????????
        String realUsername = MyUserKeyService.getRealUsername(username);
        SysUser user =  sysUserRepository.findByUsername(realUsername);
        SysAdmin admin = sysAdminRepository.findByUsername(realUsername);
        if (user != null || admin != null){
            return ResultUtil.error(-2, "????????????????????????????????????");
        }
        String realPassword = MyUserKeyService.getRealPassword(password);

        SysAdmin sysAdmin = sysAdminRepository.findByIdDetail(idDetail);
        String creator = sysAdmin.getUsername();

        return this.createSysUser(realUsername, realPassword, creator);
    }


    @Override
    public ApiResult adminDelUser(String idDetail) {
        String realIdDetail = MyUserKeyService.decodeUserIdDetail(idDetail);
        idPhotoRepository.deleteAllByUserIdDetail(realIdDetail);
        userInfoRepository.deleteByIdDetail(realIdDetail);
        sysUserRepository.deleteByIdDetail(realIdDetail);
        return ResultUtil.success();
    }


    @Override
    public ApiResult adminGetAdminList() throws IOException {
        log.info("???????????????adminGetAdminList?????????");
        List<SysAdmin> sysAdmins = sysAdminRepository.findAll(Sort.by(Sort.Direction.DESC, "updateTime"));
        StringBuilder sb = new StringBuilder();
        for (SysAdmin sa : sysAdmins) {
            String saStr = objectMapper.writeValueAsString(sa);
            sb.append("--").append(saStr);
        }
        String adminListStr = MyUserKeyService.aesEncrypt(sb.toString());
        return ResultUtil.success(adminListStr);
    }

    @Override
    public ApiResult adminSetAdmin(String username, String adminPower, String password){
        Timestamp timestamp = new Timestamp(new Date().getTime());
        String realUsername = MyUserKeyService.getRealUsername(username);
        String realPassword = MyUserKeyService.getRealPassword(password);
        SysUser sysUser = sysUserRepository.findByUsername(realUsername);
        if (sysUser != null) {
            return ResultUtil.error(-2, "???????????????????????????????????????");
        }

        SysAdmin sysAdmin = sysAdminRepository.findByUsername(realUsername);
        if (sysAdmin == null) {
            sysAdmin = new SysAdmin();
            String theRandom = String.valueOf((int)(Math.random()*9 + 1) * 1000);
            String idDetail = SecurityConstants.ADMIN_ID_DETAIL_PREFIX + new Date().getTime() + theRandom;
            String wid = SecurityConstants.ADMIN_WID_PREFIX + utilService.getYearMonthDayNow();
            SysAdmin admin = sysAdminRepository.findByWid(wid);
            if (admin != null) {
                return ResultUtil.error(-2, "????????????????????????????????????");
            }
            sysAdmin.setIdDetail(idDetail);
            sysAdmin.setWid(wid);
            Set<SysRole> sysRoles = sysRoleService.updateRole(SecurityConstants.ROLE_ADMIN);
            sysAdmin.setRoles(sysRoles);
        }
        sysAdmin.setUsername(realUsername);
        sysAdmin.setPassword(passwordEncoder.encode(realPassword));
        sysAdmin.setAdminPower(adminPower);
        sysAdmin.setUpdateTime(timestamp);
        sysAdminRepository.save(sysAdmin);
        return ResultUtil.success(sysAdmin);
    }

    @Override
    public ApiResult adminDelAdmin(String idDetail) {
        // String realIdDetail = MyUserKeyService.aesDecrypt(idDetail);
        long nums = sysAdminRepository.count();
        if (nums == 1) {
            return ResultUtil.error(-2, "??????1????????????????????????????????????");
        }
        SysAdmin sysAdmin = sysAdminRepository.findByIdDetail(idDetail);
        String username = sysAdmin.getUsername();
        if (username.equals("15895501880")) {
            return ResultUtil.error(-2, "????????????????????????????????????");
        }
        sysAdminRepository.deleteByIdDetail(idDetail);
        return ResultUtil.success();
    }


    @Override
    public String tokenToUsername(String token) {
        String userIdDetail = jwtCheckService.tokenToIdDetail(token);
        if (userIdDetail == null) {
            return null;
        }
        SysUser sysUser = sysUserRepository.findByIdDetail(userIdDetail);
        if (sysUser != null) {
            return sysUser.getUsername();
        }
        SysAdmin sysAdmin = sysAdminRepository.findByIdDetail(userIdDetail);
        if (sysAdmin != null) {
            return sysAdmin.getUsername();
        }
        return null;
    }

    @Override
    public String tokenToUserIdDetail(String token) {
        return jwtCheckService.tokenToIdDetail(token);
    }

    @Override
    public String tokenToSimpleUser(String token) {
        String userIdDetail = jwtCheckService.tokenToIdDetail(token);
        if (userIdDetail == null) {
            return null;
        }
        UserInfo userInfo = userInfoRepository.findByIdDetail(userIdDetail);
        if (userInfo == null) {
            return null;
        }
        SimpleUser simpleUser = this.userInfoService.makeSimpleUser(userInfo, token);
        return JSONObject.toJSONString(simpleUser);
    }

}
