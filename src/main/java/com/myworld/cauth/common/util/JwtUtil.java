package com.myworld.cauth.common.util;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.jsonwebtoken.impl.crypto.MacProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil implements Serializable {
    private static Logger log = LogManager.getRootLogger();
    public static final String HEADER_AUTH = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer";
    public static final String TOKEN_TYPE = "tokenType";

    public static final String HEADER_SERVICE = "serviceName";
    public static final String HEADER_ROLE = "role";
    public static final String HEADER_ID_DETAIL = "idDetail";

    public static final String EXPIRE_TIME = "expireTime";
    public static final String GROUP = "group";
    public static final String GROUP_NAME = "xinheJingrong";

    // 可以使用MacProvider.generateKey()，但生成的key是基于本机Mac地址的，其他机器将无法解析
    private SecretKey secret = MacProvider.generateKey();

    /**
     * 生成token
     * 根据tokenType不同，生成不同到期时间的token
     */
    public String generateToken(String tokenType, List serviceNames, String role, String idDetail, Long expireTime, PrivateKey privateKey) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(GROUP, GROUP_NAME);
        map.put(TOKEN_TYPE, tokenType);
        map.put(HEADER_SERVICE, serviceNames);
        map.put(HEADER_ROLE, role);
        map.put(HEADER_ID_DETAIL, idDetail);
        map.put(EXPIRE_TIME, expireTime);
        String jwt = Jwts.builder().setClaims(map).signWith(privateKey, SignatureAlgorithm.RS256).compact();
        return TOKEN_PREFIX + " " +jwt;
    }

    /**
     *  解析token
     */
    public Map<String,Object> parseToken(String token, PublicKey publicKey) {
        if (token != null) {
            Map<String,Object> map = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();
            log.info("解析出来的token body是{}", map.toString());
            return map;
        } else {
            log.info("token is error, please check");
            return null;
        }
    }

}
