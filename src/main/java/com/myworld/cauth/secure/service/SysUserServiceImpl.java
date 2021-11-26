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
            log.info("用户存在");
            return ResultUtil.success("用户存在");
        }else {
            return ResultUtil.error(-2, "用户不存在");
        }
    }

    /**
     * 涉及SysUser和UserInfo
     */
    @Override
    public ApiResult register(String username, String smsCode, String password, HttpServletRequest request, HttpServletResponse response){
        //关键步骤：将request转换为ServletWebRequest，用于进行smsCodeProcessor的validate()
        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
        ApiResult apiResult = smsCodeProcessor.validate(servletWebRequest);
        if (apiResult.getCode() != 0){
            return ResultUtil.error(-2, "验证码验证失败");
        }
        //检查 该用户名是否已存在
        String realUsername = MyUserKeyService.getRealUsername(username);
        SysUser user =  sysUserRepository.findByUsername(realUsername);
        SysAdmin admin = sysAdminRepository.findByUsername(realUsername);
        if (user != null || admin != null){
            return ResultUtil.error(-2, "用户已存在，请勿重复注册");
        }

        String realPassword = MyUserKeyService.getRealPassword(password);
        return this.createSysUser(realUsername, realPassword, realUsername);
    }

    /**
     * 暂且只配置jingrongbank的服务
     * 返回成功后，跳转至登录页面，重新登录
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

        //设置用户角色SysRole
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
        userInfo.setAccountStatus("待完善");
        userInfo.setCreditLevel("普通");
        userInfo.setCreditLevelConfirm("普通CCCCC");
        userInfoRepository.save(userInfo);
        return ResultUtil.success();
    }



    /**
     * 涉及SysUser和UserInfo
     * 变更用户名的关键是根据UserInfo中的idnumber, username来检查
     * 返回成功后，跳转至登录页面，重新登录
     */
    @Override
    public ApiResult changeUsername(String usernameold, String username, String smsCode, String idnumber, HttpServletRequest request, HttpServletResponse response){
        Timestamp timestamp = new Timestamp(new Date().getTime());
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();

        //1、检查短信验证码
        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
        ApiResult apiResult = smsCodeProcessor.validate(servletWebRequest);
        if (apiResult.getCode() != 0){
            return ResultUtil.error(-2, "验证码验证失败");
        }
        //2、检查要变更的新的用户名是否已存在
        SysUser user = sysUserRepository.findByUsername(username);
        SysAdmin admin = sysAdminRepository.findByUsername(username);
        if (user != null || admin != null){
            return ResultUtil.error(-2, "新用户名已存在");
        }

        //3、根据idDetail查询Sysuser的username, 比较提交上来的usernameold是否一致
        SysUser sysUser = sysUserRepository.findByIdDetail(idDetail);
        if (sysUser == null) {
            return ResultUtil.error(-2, "用户令牌不正确，无此用户");
        }
        String theUsername = sysUser.getUsername();
        usernameold = MyUserKeyService.getRealUsername(usernameold);
        username = MyUserKeyService.getRealUsername(username);
        if (!theUsername.equals(usernameold)) {
            return ResultUtil.error(-2, "用户令牌不匹配");
        }
        //4、根据提交的身份证号码，从UserInfo中提取出username, 检查与提交的usernameold是否一致
        idnumber = idnumber.replace(" ", "+");
        String realIdnumber = MyUserKeyService.aesDecrypt(idnumber);
        UserInfo userInfo = userInfoRepository.findByIdnumber(realIdnumber);
        if (userInfo == null) {
            return ResultUtil.error(-2, "身份证号码不正确，无此用户");
        }
        String usernameSave = userInfo.getUsername();
        if (!usernameSave.equals(usernameold)){
            return ResultUtil.error(-2, "用户身份不匹配");
        }

        // 通过以上检查后，便可更新sysuser和userInfo了
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
     * 涉及SysUser
     * 暂且只配置jingrongbank的服务
     * 返回成功后，跳转至登录页面，重新登录
     */
    @Override
    public ApiResult changePassword(String passwordold, String password, HttpServletRequest request, HttpServletResponse response){
        Timestamp timestamp = new Timestamp(new Date().getTime());
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();
        SysUser sysUser = sysUserRepository.findByIdDetail(idDetail);
        if (sysUser == null) {
            return ResultUtil.error(-2, "用户令牌不正确，无此用户");
        }

        // 检查passwordold与原来保存的passwordSave是否一致
        String usernameSave = sysUser.getUsername();
        String passwordSave = sysUser.getPassword();
        passwordold = MyUserKeyService.getRealPassword(passwordold);
        password = MyUserKeyService.getRealPassword(password);
        if (passwordEncoder.matches(passwordold, passwordSave)) {
            log.info("原密码匹配一致，可以修改新密码");
            String encodePassword = passwordEncoder.encode(password);
            sysUser.setPasswordold(passwordSave);
            sysUser.setPassword(encodePassword);
            sysUser.setUpdater(usernameSave);
            sysUser.setUpdateTime(timestamp);
            sysUserRepository.save(sysUser);
            return ResultUtil.success();
        } else {
            return ResultUtil.error(-2, "原密码不正确");
        }
    }

    /**
     * 涉及SysUser
     * 暂且只配置jingrongbank的服务
     * 返回成功后，跳转至登录页面，重新登录
     */
    @Override
    public ApiResult resetPassword(String username, String password){
        Timestamp timestamp = new Timestamp(new Date().getTime());
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();
        SysUser sysUser = sysUserRepository.findByIdDetail(idDetail);
        if (sysUser == null) {
            return ResultUtil.error(-2, "无此用户");
        }
        // 1、比较Sysuser中的username和用户提交上来的username是否一致，如不一致，则返回错误提示
        String realUsername = MyUserKeyService.getRealUsername(username);
        String usernameSave = sysUser.getUsername();
        if (!usernameSave.equals(realUsername)) {
            return ResultUtil.error(-2, "用户名不正确");
        }

        // 2、重置密码定义为用户登录后5分钟时间内重置才有效，超过5分钟以后重置无效
        Timestamp lastLoginTime = sysUser.getLastLoginTime();
        long validTime = lastLoginTime.getTime() + 1000 * 60 * 5;
        long currentTime = timestamp.getTime();
        log.info("重置密码有效时间validTime为: {}", validTime);
        log.info("当前时间currentTime为: {}", currentTime);
        if (currentTime > validTime) {
            return ResultUtil.error(-2, "已超过重置密码的有效时间");
        }

        String realPassword = MyUserKeyService.getRealPassword(password);
        log.info("真实的password是: {}", realPassword);
        String encodePassword = passwordEncoder.encode(realPassword);
        log.info("后台加密后储存的encodePassword是: {}", encodePassword);

        // 上面检验通过后，更新Sysuser
        sysUser.setPasswordold(sysUser.getPassword());
        sysUser.setPassword(encodePassword);
        sysUser.setUpdater(usernameSave);
        sysUser.setUpdateTime(timestamp);
        sysUserRepository.save(sysUser);
        return ResultUtil.success();
    }

    //以下为admin管理部分
    @Override
    public ApiResult adminCreateUser(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();
        //检查 该用户名是否已存在
        String realUsername = MyUserKeyService.getRealUsername(username);
        SysUser user =  sysUserRepository.findByUsername(realUsername);
        SysAdmin admin = sysAdminRepository.findByUsername(realUsername);
        if (user != null || admin != null){
            return ResultUtil.error(-2, "用户已存在，请勿重复注册");
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
        log.info("【开始执行adminGetAdminList方法】");
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
            return ResultUtil.error(-2, "该用户名为普通用户且已存在");
        }

        SysAdmin sysAdmin = sysAdminRepository.findByUsername(realUsername);
        if (sysAdmin == null) {
            sysAdmin = new SysAdmin();
            String theRandom = String.valueOf((int)(Math.random()*9 + 1) * 1000);
            String idDetail = SecurityConstants.ADMIN_ID_DETAIL_PREFIX + new Date().getTime() + theRandom;
            String wid = SecurityConstants.ADMIN_WID_PREFIX + utilService.getYearMonthDayNow();
            SysAdmin admin = sysAdminRepository.findByWid(wid);
            if (admin != null) {
                return ResultUtil.error(-2, "一天仅允许添加一个管理员");
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
            return ResultUtil.error(-2, "最后1个管理员账户，不允许删除");
        }
        SysAdmin sysAdmin = sysAdminRepository.findByIdDetail(idDetail);
        String username = sysAdmin.getUsername();
        if (username.equals("15895501880")) {
            return ResultUtil.error(-2, "该管理员账户，不允许删除");
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
