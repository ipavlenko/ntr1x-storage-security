package com.ntr1x.storage.security.filters;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class UserPrincipalFactory {

    @Inject
    private HttpServletRequest request;

    @Bean("principal")
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public IUserPrincipal produce() {

        return (IUserPrincipal) request.getAttribute(IUserPrincipal.class.getName());
    }
}