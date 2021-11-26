package com.myworld.cauth.secure.security.redis;


import com.alibaba.fastjson.JSONObject;
import com.myworld.cauth.config.SecurityProperties;
import com.myworld.cauth.secure.data.entity.SysAdmin;
import com.myworld.cauth.secure.data.model.CurrentAdmin;
import com.myworld.cauth.secure.data.model.CurrentUser;
import com.myworld.cauth.secure.data.repository.SysAdminRepository;
import com.myworld.cauth.secure.security.MyUserKeyService;
import com.myworld.cauth.secure.security.TokenKeyGenerator;
import com.myworld.cauth.common.util.JwtUtil;
import com.myworld.cauth.common.properties.SecurityConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PublicKey;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
public class JwtCheckService {
    private static Logger log = LogManager.getRootLogger();
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate<String, Object> template;

    @Autowired
    private SysAdminRepository sysAdminRepository;

    /**
     * 原jwtToken在宽限期内续签
     * 生成一个新的refreshToken
     * 将原jwtToken放入redis黑名单
     */
    public String checkAndRefreshJwtToken(String token) {
        log.info("开始检查或更新JwtToken");
        // 验证 jwkToken是否是blacklist名单上的token，如果是则从redis中根据key取出value，将新的token返回给前端，并保存于前端header中
        String theKey = SecurityConstants.JWT_BLACKLIST + token;
        String refreshToken = (String)template.opsForValue().get(theKey);
        if (refreshToken != null){
            log.info("已有refreshToken，旧的token不能再使用,将自动被替换");
            return refreshToken;
        }

        // 解析旧的token, 并把旧的token放进redis的blackList中，续签一个新的token返回给前端
        PublicKey publicKey = TokenKeyGenerator.getPublicKey();
        Map<String, Object> map = jwtUtil.parseToken(token, publicKey);
        Long expireTime = (Long)map.get(JwtUtil.EXPIRE_TIME);
        List<String> serviceNames = (List<String>)map.get(JwtUtil.HEADER_SERVICE);
        String role = (String)map.get(JwtUtil.HEADER_ROLE);
        String idDetail = (String)map.get(JwtUtil.HEADER_ID_DETAIL);

        //续签一个新的token, tokeType为refresh-token
        Long expireTimeRefresh = new Date().getTime() + securityProperties.getRefreshTokenValidSeconds() * 1000;
        refreshToken = jwtUtil.generateToken(SecurityConstants.DEFAULT_JWT_REFRESH_TOKEN, serviceNames, role, idDetail, expireTimeRefresh, TokenKeyGenerator.getPrivateKey());

        //把旧的token存入redis黑名单,时间为到期时间和当前时间的差额，key为旧token，value为refreshToken
        Long currentTime = new Date().getTime();
        long diffTime = expireTime - currentTime;
        if (diffTime > 0) {
            template.opsForValue().set(theKey, refreshToken, diffTime / 1000, TimeUnit.SECONDS);
        }
        return refreshToken;
    }

    /**
     * 在gateway执行filter时，如果token在宽限期内，则返回含有refreshToken的currentUserStr给前端
     */
    public String checkJwtAndRefreshCurrentUser(String token) {
        log.info("开始执行checkJwtAndRefreshCurrentUser方法");
        // 解析旧的token
        PublicKey publicKey = TokenKeyGenerator.getPublicKey();
        Map<String, Object> map = jwtUtil.parseToken(token, publicKey);
        String idDetail = (String)map.get(JwtUtil.HEADER_ID_DETAIL);
        String role = (String)map.get(JwtUtil.HEADER_ROLE);
        List<String> serviceNames = (List<String>)map.get(JwtUtil.HEADER_SERVICE);
        Long expireTime = (Long)map.get(JwtUtil.EXPIRE_TIME);


        long currentTime = new Date().getTime();
        Long expireTimeRefresh = currentTime + securityProperties.getRefreshTokenValidSeconds() * 1000;
        // 1、验证 jwkToken是否是blacklist名单上的token，如果是则从redis中根据key取出value，将新的token返回给前端，并保存于前端header中
        String theKey = SecurityConstants.JWT_BLACKLIST + token;
        String refreshToken = (String)template.opsForValue().get(theKey);
        // 2、如果没有在redis的blacklist名单上根据旧token作为key，找到refreshToken的值，则重新签一个refreshToken
        if (refreshToken == null){
            // (1)续签一个新的token, tokeType为refresh-token, 其中idDetail仍然是加密的
            refreshToken = jwtUtil.generateToken(SecurityConstants.DEFAULT_JWT_REFRESH_TOKEN, serviceNames, role, idDetail, expireTimeRefresh, TokenKeyGenerator.getPrivateKey());
            // (2) 把旧的token放进redis的blackList中, 时间为到期时间和当前时间的差额，key为旧token，value为refreshToken
            long diffTime = expireTime - currentTime;
            if (diffTime > 0) {
                template.opsForValue().set(theKey, refreshToken, diffTime / 1000, TimeUnit.SECONDS);
            }
        }

        if (role.equals("ADMIN")) {
            SysAdmin sysAdmin = sysAdminRepository.findByIdDetail(idDetail);
            if (sysAdmin != null) {
                CurrentAdmin currentAdmin = new CurrentAdmin();
                currentAdmin.setToken(refreshToken);
                currentAdmin.setWid(idDetail);
                currentAdmin.setExpireTime(String.valueOf(expireTimeRefresh));
                currentAdmin.setAdminPower(sysAdmin.getAdminPower());
                String cadminStr = JSONObject.toJSONString(currentAdmin);
                return MyUserKeyService.aesEncrypt(cadminStr);
            }
        }
        CurrentUser cuser = new CurrentUser();
        cuser.setToken(refreshToken);
        cuser.setWid(idDetail);
        cuser.setPlayerType(role);
        cuser.setExpireTime(String.valueOf(expireTimeRefresh));
        String cuserStr = JSONObject.toJSONString(cuser);
        return MyUserKeyService.aesEncrypt(cuserStr);
    }

    /**
     * 在用户设置wid后，更新一个新的token给前端用户
     */
    public CurrentUser refreshCurrentUserForWid(String token, String wid) {
        log.info("开始执行refreshCurrentUser方法");
        // 解析旧的token，token中没有wid
        PublicKey publicKey = TokenKeyGenerator.getPublicKey();
        Map<String, Object> map = jwtUtil.parseToken(token, publicKey);
        String idDetail = (String)map.get(JwtUtil.HEADER_ID_DETAIL);
        String role = (String)map.get(JwtUtil.HEADER_ROLE);
        List<String> serviceNames = (List<String>)map.get(JwtUtil.HEADER_SERVICE);
        Long expireTime = (Long)map.get(JwtUtil.EXPIRE_TIME);

        long currentTime = new Date().getTime();
        Long expireTimeRefresh = currentTime + securityProperties.getRefreshTokenValidSeconds() * 1000;

        // 签一个新的token, tokeType为refresh-token, 其中idDetail仍然是加密的
        String refreshToken = jwtUtil.generateToken(SecurityConstants.DEFAULT_JWT_REFRESH_TOKEN, serviceNames, role, idDetail, expireTimeRefresh, TokenKeyGenerator.getPrivateKey());
        // 把旧的token放进redis的blackList中, 时间为到期时间和当前时间的差额，key为旧token，value为refreshToken
        long diffTime = expireTime - currentTime;
        if (diffTime > 0) {
            String theKey = SecurityConstants.JWT_BLACKLIST + token;
            template.opsForValue().set(theKey, refreshToken, diffTime / 1000, TimeUnit.SECONDS);
        }

        CurrentUser cuser = new CurrentUser();
        cuser.setToken(refreshToken);
        cuser.setWid(wid);
        cuser.setPlayerType(role);
        cuser.setExpireTime(String.valueOf(expireTimeRefresh));
        return cuser;
    }

    /**
     *  用户修改用户名，修改密码，重置密码的操作时的检测
     */
    public String checkJwtToken(HttpServletRequest request, HttpServletResponse response){
        log.info("【开始执行checkJwtToken方法】");
        Enumeration enu = request.getHeaderNames();
        while (enu.hasMoreElements()){
             String paraName = (String)enu.nextElement();
             log.info("request中的参数" + paraName + "值为:" + request.getHeader(paraName));
        }

        String token = request.getHeader(JwtUtil.HEADER_AUTH);
        log.info("从request中获取到的token是：{}", token);
        if(token == null){
            return null;
        }
        return this.tokenToIdDetail(token);
    }

    /**
     * 解析token，token有效则返回用户的idDetail，token无效则返回null
     */
    public String tokenToIdDetail(String token) {
        String theKey = SecurityConstants.JWT_BLACKLIST + token;
        String refreshToken = (String)template.opsForValue().get(theKey);
        if (refreshToken != null){
            log.info("不能使用已进入blacklist的token来执行重要的操作");
            return null;
        }

        PublicKey publicKey = TokenKeyGenerator.getPublicKey();
        Map<String, Object> map = jwtUtil.parseToken(token, publicKey);
        String groupName = (String)map.get(JwtUtil.GROUP);
        Long expireTime = (Long)map.get(JwtUtil.EXPIRE_TIME);
        String idDetail = (String)map.get(JwtUtil.HEADER_ID_DETAIL);

        // 如果token中的group标记不是本机构的，则返回false
        if (!groupName.equals(JwtUtil.GROUP_NAME)){
            return null;
        }
        // 如果token的有效期小于当前时间，则该token已过期，不能使用
        if (expireTime < new Date().getTime()){
            log.info("该token已过期");
            return null;
        }
        // return MyUserKeyService.decodeUserIdDetail(idDetail);
        return idDetail;
    }

}
