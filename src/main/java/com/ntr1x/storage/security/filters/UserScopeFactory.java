package com.ntr1x.storage.security.filters;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class UserScopeFactory {

    @Inject
    private HttpServletRequest request;

    @Bean("scope")
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public IUserScope produce() {

        return (IUserScope) request.getAttribute(IUserScope.class.getName());
    }
}