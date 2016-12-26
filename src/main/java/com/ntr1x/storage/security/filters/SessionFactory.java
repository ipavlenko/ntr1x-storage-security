package com.ntr1x.storage.security.filters;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.ntr1x.storage.security.filters.AuthenticationFilter.UserPrincipal;
import com.ntr1x.storage.security.model.ISession;

@Configuration
public class SessionFactory {

    @Inject
    private HttpServletRequest request;

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public ISession produce() {

        UserPrincipal principal = (UserPrincipal) request.getAttribute(UserPrincipal.class.getName());
        return principal == null ? null : principal.session;
    }
}