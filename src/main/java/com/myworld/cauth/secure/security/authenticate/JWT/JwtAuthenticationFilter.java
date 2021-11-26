package com.myworld.cauth.secure.security.authenticate.JWT;

import com.myworld.cauth.secure.data.entity.SysAdmin;
import com.myworld.cauth.secure.data.entity.SysUser;
import com.myworld.cauth.secure.data.repository.SysAdminRepository;
import com.myworld.cauth.secure.data.repository.SysUserRepository;
import com.myworld.cauth.secure.security.redis.JwtCheckService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("jwtAuthenticationFilter")
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static Logger log = LogManager.getRootLogger();
    @Autowired
    private JwtCheckService jwtCheckService;
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private SysAdminRepository sysAdminRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("【开始执行JwtAuthenticationFilter】");
        String idDetail = jwtCheckService.checkJwtToken(request, response);

        if (idDetail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("【开始制作token】");
            UsernamePasswordAuthenticationToken authRequest;
            SysUser sysUser = sysUserRepository.findByIdDetail(idDetail);
            if (sysUser != null) {
                log.info("【给sysUser制作token】");
                String username = sysUser.getUsername();
                String password = sysUser.getPassword();
                authRequest = new UsernamePasswordAuthenticationToken(username, password, sysUser.getAuthorities());
            } else {
                SysAdmin sysAdmin = sysAdminRepository.findByIdDetail(idDetail);
                log.info("【给sysAdmin制作token】");
                String username = sysAdmin.getUsername();
                String password = sysAdmin.getPassword();
                authRequest = new UsernamePasswordAuthenticationToken(username, password, sysAdmin.getAuthorities());
            }

            authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authRequest);
        }
        chain.doFilter(request, response);
    }

}
