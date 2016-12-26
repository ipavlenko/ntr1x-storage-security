package com.ntr1x.storage.security.services;

import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.ntr1x.storage.security.filters.AuthenticationFilter.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
@RequiredArgsConstructor
public class ContextService implements IContextService {

    @Inject
    private ISecurityService security;
    
    @Inject
    private HttpServletRequest request;

    @Override
    public Principal getUserPrincipal() {
        return (UserPrincipal) request.getAttribute(UserPrincipal.class.getName());
    }
    
    @Override
    @Transactional
    public boolean isUserInRole(UriInfoState state, String role) {
                
        UserPrincipal principal = (UserPrincipal) request.getAttribute(UserPrincipal.class.getName());
        
        if (principal == null
            || principal.session == null
            || principal.session.getUser() == null
        ) {
        	throw new NotAuthorizedException("No active session");
        }
        
        if (!principal.session.getUser().isEmailConfirmed()) {
        	throw new ForbiddenException("Account is not confirmed");
        }
            
        switch (role) {
            case "auth": return true;
        }
        
        if (role.startsWith("res://")) {
            
            StrSubstitutor substitutor = new StrSubstitutor(state.params);
            substitutor.setVariablePrefix('{');
            substitutor.setVariableSuffix('}');
            String name = substitutor.replace(role).substring("res://".length());

            int pos = name.indexOf(':');
            if (pos >= 0) {
                
                String resource = name.substring(0, pos);
                String action = name.substring(pos + 1);

                return security.isUserInRole(principal.getSession().getUser(), resource, action);
            }
            
            return false;
        }
        
        return false;
    }
}
