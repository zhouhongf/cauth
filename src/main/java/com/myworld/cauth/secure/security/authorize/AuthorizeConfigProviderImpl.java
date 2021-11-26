package com.myworld.cauth.secure.security.authorize;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorizeConfigProviderImpl implements AuthorizeConfigProvider {
    @Override
    public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
       // config.antMatchers("/borrower/*").access("hasRole('ADMIN') or hasRole('BORROWER')");
//                .antMatchers("/person/{id}").access("@rbacService.checkUserId(authentication,#id)")
//                .anyRequest()
//                .access("@rbacService.hasPermission(request,authentication)");
       // config.antMatchers("/lender/*").access("hasRole('ADMIN') or hasRole('LENDER')");
        config.anyRequest().authenticated();
    }
}
