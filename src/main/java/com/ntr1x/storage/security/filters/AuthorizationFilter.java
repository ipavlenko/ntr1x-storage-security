package com.ntr1x.storage.security.filters;

import java.security.Principal;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import com.ntr1x.storage.security.services.IContextService;
import com.ntr1x.storage.security.services.IContextService.UriInfoState;

import lombok.RequiredArgsConstructor;

@Component
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthorizationFilter implements ContainerRequestFilter {
    
    @Inject
    private IContextService security;
    
    @Override
    public void filter(ContainerRequestContext rc) {
        
        rc.setSecurityContext(
            new SecurityContextInstance(
                security,
                new UriInfoState(rc.getUriInfo())
            )
        );
    }
    
    @RequiredArgsConstructor
    public static class SecurityContextInstance implements SecurityContext {
        
        private final IContextService security;
        private final IContextService.UriInfoState state;
        
        @Override
        @Transactional
        public boolean isUserInRole(String role) {
            return security.isUserInRole(state, role);
        }

        @Override
        public Principal getUserPrincipal() {
            return security.getUserPrincipal();
        }

        @Override
        public String getAuthenticationScheme() {
            return SecurityContext.FORM_AUTH;
        }
        
        @Override
        public boolean isSecure() {
            return true;
        }
    }
}